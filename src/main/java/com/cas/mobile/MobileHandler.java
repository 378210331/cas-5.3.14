package com.cas.mobile;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apereo.cas.authentication.AuthenticationHandlerExecutionResult;
import org.apereo.cas.authentication.Credential;
import org.apereo.cas.authentication.handler.support.AbstractPreAndPostProcessingAuthenticationHandler;
import org.apereo.cas.authentication.principal.Principal;
import org.apereo.cas.authentication.principal.PrincipalFactory;
import org.apereo.cas.services.ServicesManager;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.security.auth.login.FailedLoginException;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
public class MobileHandler extends AbstractPreAndPostProcessingAuthenticationHandler {

    NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    StringRedisTemplate stringRedisTemplate;

    private static final String sql = "select u.id,u.username,u.realname,u.dep_id as depId,password, GROUP_CONCAT(ur.role_id)  as roleId\n" +
            "          from sys_user u LEFT JOIN sys_user_role ur on u.id = ur.user_id where u.phone=:phone\n" +
            "          group by u.id";

    @Override
    public boolean supports(Credential credential) {
        //判断传递过来的Credential 是否是自己能处理的类型
        return credential instanceof MobileCredential;
    }

    public MobileHandler(String name, ServicesManager servicesManager, PrincipalFactory principalFactory, Integer order , NamedParameterJdbcTemplate namedParameterJdbcTemplate, StringRedisTemplate stringRedisTemplate) {
        super(name, servicesManager, principalFactory, order);
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
        this.stringRedisTemplate = stringRedisTemplate;
    }


    @Override
    protected AuthenticationHandlerExecutionResult doAuthentication(Credential credential) throws GeneralSecurityException {
        MobileCredential mobileIdCredential = (MobileCredential) credential;
        String phoneNumber = mobileIdCredential.getPhoneNumber();
        String validateCode = mobileIdCredential.getValidateCode();
        if(StringUtils.isBlank(phoneNumber)){
            throw new FailedLoginException("手机号码必须填写");
        }
        Set<String> keys = stringRedisTemplate.keys("mobile::"+phoneNumber + "::*");
        if(keys.size() == 0){
            throw new FailedLoginException("验证码不正确");
        }
        List<String> codes = stringRedisTemplate.opsForValue().multiGet(keys);
        if(codes.stream().noneMatch(s ->StringUtils.equals(s,validateCode))){
            throw new FailedLoginException("验证码不正确");
        }
        Map<String,Object> param = new HashMap<>();
        param.put("phone",phoneNumber);
        List<Map<String, Object>> dbResult = namedParameterJdbcTemplate.queryForList(sql,param);
        if(dbResult.size() == 0){
            throw new FailedLoginException("没有当前用户");
        }else {
            Map<String,Object> attributes = dbResult.get(0);
            Principal principal = principalFactory.createPrincipal(attributes.get("username").toString(),attributes);
            return  createHandlerResult(credential, principal);
        }
    }
}
