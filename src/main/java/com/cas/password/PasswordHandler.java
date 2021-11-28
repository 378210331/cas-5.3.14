package com.cas.password;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apereo.cas.authentication.AuthenticationHandlerExecutionResult;
import org.apereo.cas.authentication.Credential;
import org.apereo.cas.authentication.handler.support.AbstractPreAndPostProcessingAuthenticationHandler;
import org.apereo.cas.authentication.principal.Principal;
import org.apereo.cas.authentication.principal.PrincipalFactory;
import org.apereo.cas.services.ServicesManager;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.scripting.support.ResourceScriptSource;

import javax.security.auth.login.FailedLoginException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class PasswordHandler extends AbstractPreAndPostProcessingAuthenticationHandler {

    NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    StringRedisTemplate stringRedisTemplate;

    private static final String sql = "select u.id,u.username,u.realname,u.dep_id as depId,password, GROUP_CONCAT(ur.role_id)  as roleId\n" +
            "          from sys_user u LEFT JOIN sys_user_role ur on u.id = ur.user_id where u.username=:username\n" +
            "          group by u.id";

    @Override
    public boolean supports(Credential credential) {
        //判断传递过来的Credential 是否是自己能处理的类型
        return credential instanceof PasswordCredential;
    }

    public PasswordHandler(String name, ServicesManager servicesManager, PrincipalFactory principalFactory, Integer order , NamedParameterJdbcTemplate namedParameterJdbcTemplate, StringRedisTemplate stringRedisTemplate) {
        super(name, servicesManager, principalFactory, order);
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
        this.stringRedisTemplate = stringRedisTemplate;
    }


    @Override
    protected AuthenticationHandlerExecutionResult doAuthentication(Credential credential) throws GeneralSecurityException {
        PasswordCredential passwordCredential = (PasswordCredential) credential;
        String username = passwordCredential.getUsername();
        String password = passwordCredential.getPassword();
        String captcha  = passwordCredential.getCaptcha();
        if(StringUtils.isBlank(username)){
            throw new FailedLoginException("用户名必须填写");
        }
        if(StringUtils.isBlank(password)){
            throw new FailedLoginException("密码必须填写");
        }
        String redisLockKey = "userLock::"+username;
        if( stringRedisTemplate.hasKey(redisLockKey)){
            //是否已经被锁定
            if(Integer.parseInt(stringRedisTemplate.opsForValue().get(redisLockKey)) >= 0){
                long time  = stringRedisTemplate.getExpire(redisLockKey);
                log.error("用户锁定:{},{}",username,password);
                throw new FailedLoginException("账户已被锁定,请等待"+time+"秒后尝试");
            }
        }
        Map<String,Object> param = new HashMap<>();
        param.put("username",username);
        List<Map<String, Object>> dbResult = namedParameterJdbcTemplate.queryForList(sql,param);
        if(dbResult.size() == 0) {
            lockAdd(redisLockKey,"300");
            throw new FailedLoginException("用户不存在或密码不正确");
/*        }else if(! StringUtils.equals(dbResult.get(0).get("password").toString(), SM3Util.SM3Encode(password))){
            lockAdd(redisLockKey,"300");
            throw new FailedLoginException("用户不存在或密码不正确");*/
        }else {
            Map<String,Object> attributes = dbResult.get(0);
            attributes.remove("password");
            stringRedisTemplate.delete(redisLockKey);//登录成功，删除锁
            Principal principal = principalFactory.createPrincipal(attributes.get("username").toString(),attributes);
            return  createHandlerResult(credential, principal);
        }
    }

    public void lockAdd(String key,String time){
        List<String> keys = Collections.singletonList(key);
        Object[] args = {"-4" , time}; //5次输错机会
        DefaultRedisScript<Long> script = new DefaultRedisScript<>();
        script.setResultType(Long.class);
        script.setScriptSource(new ResourceScriptSource(new ClassPathResource("script/loginLockAdd.lua")));//当没有锁，则加-5锁,有锁则 + 1
        stringRedisTemplate.execute(script, keys, args);
    }
}
