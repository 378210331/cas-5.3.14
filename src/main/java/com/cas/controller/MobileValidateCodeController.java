package com.cas.controller;

import com.cas.model.Result;
import org.apereo.cas.configuration.CasConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 生成并发送手机验证码
 */
@RestController("MobileValidateCodeController")
@EnableConfigurationProperties({CasConfigurationProperties.class})
@RequestMapping("/validateCode")
public class MobileValidateCodeController {

    /**
     * 验证码有效期
     */
    final long  VALID_PERIOD_IN_SECOND = 5 * 60;

    public MobileValidateCodeController(StringRedisTemplate stringRedisTemplate,NamedParameterJdbcTemplate namedParameterJdbcTemplate){
        this.stringRedisTemplate = stringRedisTemplate;
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    StringRedisTemplate stringRedisTemplate;

    NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    final String sql = "select * from sys_user where phone = :phone";

    @GetMapping("/{phoneNumber}")
    public Result<Void> getValidateCode(@PathVariable("phoneNumber")String phoneNumber){
        Map<String,Object> param = new HashMap<>();
        param.put("phone",phoneNumber);
        if(namedParameterJdbcTemplate.queryForList(sql,param).size() == 0){
            return Result.buildError("未找到当前号码对应的用户");
        }
        String full = Long.toString(System.nanoTime());
        String code = full.substring(full.length() - 6);//生成6位短信验证码
        try{
            System.out.println(code);
            //TODO 发送短信逻辑
        }catch (Exception e){
            Result.buildError("发送验证码失败");
        }
        String key = "mobile::" + phoneNumber + "::" + System.currentTimeMillis();
        stringRedisTemplate.opsForValue().set(key,code,VALID_PERIOD_IN_SECOND, TimeUnit.SECONDS);
        return Result.buildSuccess(MessageFormat.format("验证码已经发送至{0},{1}分钟内有效",phoneNumber,VALID_PERIOD_IN_SECOND/60));
    }
}
