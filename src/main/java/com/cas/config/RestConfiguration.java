package com.cas.config;

import com.google.code.kaptcha.Producer;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import com.cas.controller.KaptchaController;
import com.cas.controller.MobileValidateCodeController;
import com.cas.controller.ServiceMangerController;
import com.cas.controller.TicketRestController;
import org.apereo.cas.CentralAuthenticationService;
import org.apereo.cas.authentication.AuthenticationSystemSupport;
import org.apereo.cas.authentication.principal.ServiceFactory;
import org.apereo.cas.configuration.CasConfigurationProperties;
import org.apereo.cas.rest.factory.RestHttpRequestCredentialFactory;
import org.apereo.cas.rest.factory.ServiceTicketResourceEntityResponseFactory;
import org.apereo.cas.services.ServicesManager;
import org.apereo.cas.ticket.registry.TicketRegistrySupport;
import org.apereo.cas.web.support.ArgumentExtractor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.Properties;

@Configuration("RestConfiguration")
@EnableConfigurationProperties({CasConfigurationProperties.class})
public class RestConfiguration {

    @Autowired
    @Qualifier("servicesManager")
    private ServicesManager servicesManager;

    @Autowired
    private CasConfigurationProperties casProperties;

    @Autowired
    NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    RedisConnectionFactory redisTicketConnectionFactory;


    @Bean
    public ServiceMangerController serviceMangerController() {
            return new ServiceMangerController(servicesManager,casProperties);
    }

    @Bean
    public MobileValidateCodeController mobileValidateCodeController() {
        return new MobileValidateCodeController(stringRedisTemplate(),namedParameterJdbcTemplate);
    }


     @Autowired
     AuthenticationSystemSupport authenticationSystemSupport;
    @Autowired
     TicketRegistrySupport ticketRegistrySupport;
    @Autowired
     ArgumentExtractor argumentExtractor;
    @Autowired
     ServiceTicketResourceEntityResponseFactory serviceTicketResourceEntityResponseFactory;
    @Autowired
     RestHttpRequestCredentialFactory credentialFactory;
    @Autowired
     CentralAuthenticationService centralAuthenticationService;
    @Autowired
     ServiceFactory serviceFactory;
    
    @Bean
    public TicketRestController ticketRestController(){
        return  new TicketRestController(authenticationSystemSupport,ticketRegistrySupport,argumentExtractor,serviceTicketResourceEntityResponseFactory,credentialFactory,
                credentialFactory,centralAuthenticationService,serviceFactory);
    }


    @Bean(name = "stringRedisTemplate")
    @ConditionalOnMissingBean(StringRedisTemplate.class)
    public StringRedisTemplate stringRedisTemplate() {
        StringRedisTemplate stringRedisTemplate = new StringRedisTemplate(redisTicketConnectionFactory);
        stringRedisTemplate.setEnableTransactionSupport(false);
        stringRedisTemplate.afterPropertiesSet();
        return stringRedisTemplate;
    }

    @Bean(name = "captchaProducer")
    public Producer captchaProducer(){
        DefaultKaptcha defaultKaptcha = new DefaultKaptcha();
        Properties properties = new Properties();
        properties.setProperty("kaptcha.border","yes");//是否有边框
        properties.setProperty("kaptcha.textproducer.font.color","black");//验证码文本字符颜色
        properties.setProperty("kaptcha.image.width","92");//验证码图片宽度
        properties.setProperty("kaptcha.image.height","36");//验证码图片高度
        properties.setProperty("kaptcha.textproducer.font.size","30");//验证码文本字符大小
        properties.setProperty("kaptcha.session.key","captcha");//session中存放验证码的key键
        properties.setProperty("kaptcha.noise.color","white");//验证码噪点颜色
        properties.setProperty("kaptcha.noise.impl","com.google.code.kaptcha.impl.NoNoise");//验证码噪点颜色
        properties.setProperty("kaptcha.textproducer.char.space","4");//验证码文本字符间距
        properties.setProperty("kaptcha.obscurificator.impl","com.google.code.kaptcha.impl.ShadowGimpy");//验证码样式引擎
        properties.setProperty("kaptcha.textproducer.char.length","4");//验证码文本字符长度
        properties.setProperty("kaptcha.textproducer.font.names","宋体,楷体,微软雅黑");//验证码文本字体样式
        defaultKaptcha.setConfig(new Config(properties));
        return defaultKaptcha;
    }

    @Bean
    public KaptchaController kaptchaController(Producer captchaProducer){
        return new KaptchaController(captchaProducer);
    }



}
