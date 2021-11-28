package com.cas.mobile;

import org.apereo.cas.authentication.AuthenticationEventExecutionPlan;
import org.apereo.cas.authentication.AuthenticationEventExecutionPlanConfigurer;
import org.apereo.cas.authentication.AuthenticationHandler;
import org.apereo.cas.authentication.adaptive.AdaptiveAuthenticationPolicy;
import org.apereo.cas.authentication.principal.DefaultPrincipalFactory;
import org.apereo.cas.configuration.CasConfigurationProperties;
import org.apereo.cas.services.ServicesManager;
import org.apereo.cas.web.flow.CasWebflowConfigurer;
import org.apereo.cas.web.flow.CasWebflowExecutionPlan;
import org.apereo.cas.web.flow.CasWebflowExecutionPlanConfigurer;
import org.apereo.cas.web.flow.config.CasWebflowContextConfiguration;
import org.apereo.cas.web.flow.resolver.CasDelegatingWebflowEventResolver;
import org.apereo.cas.web.flow.resolver.CasWebflowEventResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.webflow.definition.registry.FlowDefinitionRegistry;
import org.springframework.webflow.engine.builder.support.FlowBuilderServices;
import org.springframework.webflow.execution.Action;

@Configuration
@AutoConfigureBefore(value = CasWebflowContextConfiguration.class)
@EnableConfigurationProperties(CasConfigurationProperties.class)
public class MobileConfiguration implements CasWebflowExecutionPlanConfigurer {

    @Autowired
    private CasConfigurationProperties casProperties;

    @Autowired
    @Qualifier("logoutFlowRegistry")
    private FlowDefinitionRegistry logoutFlowRegitry;

    @Autowired
    @Qualifier("loginFlowRegistry")
    private FlowDefinitionRegistry loginFlowRegistry;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    @Qualifier("builder")
    private FlowBuilderServices builder;

    @Autowired
    @Qualifier("adaptiveAuthenticationPolicy")
    private AdaptiveAuthenticationPolicy adaptiveAuthenticationPolicy;

    @Autowired
    @Qualifier("serviceTicketRequestWebflowEventResolver")
    private CasWebflowEventResolver serviceTicketRequestWebflowEventResolver;

    @Autowired
    @Qualifier("initialAuthenticationAttemptWebflowEventResolver")
    private CasDelegatingWebflowEventResolver initialAuthenticationAttemptWebflowEventResolver;

    @Autowired
    private ServicesManager servicesManager;

    @Autowired
    NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @ConditionalOnMissingBean(name = "mobileWebflowConfigurer")
    @Bean
    public CasWebflowConfigurer mobileWebflowConfigurer() {
        MobileWebflowConfigurer configurer = new MobileWebflowConfigurer(builder,
                loginFlowRegistry, applicationContext, casProperties);
        configurer.setLogoutFlowDefinitionRegistry(logoutFlowRegitry);
        return configurer;
    }

    @Bean
    public Action mobileCredentialsAction() {
        return new MobileCredentialsAction(initialAuthenticationAttemptWebflowEventResolver,
                serviceTicketRequestWebflowEventResolver, adaptiveAuthenticationPolicy);
    }


    @Configuration("mobileAuthenticationEventExecutionPlanConfiguration")
    public class MobileAuthenticationEventExecutionPlanConfiguration
            implements AuthenticationEventExecutionPlanConfigurer {


        //注册验证器
        @DependsOn("namedParameterJdbcTemplate")
        @Bean
        public AuthenticationHandler mobileAuthenticationHandler() {
            //优先验证
            return new MobileHandler("mobileHandler",
                    servicesManager, new DefaultPrincipalFactory(), 1,namedParameterJdbcTemplate,stringRedisTemplate);
        }


        @Override
        public void configureAuthenticationExecutionPlan(final AuthenticationEventExecutionPlan plan) {
            plan.registerAuthenticationHandler(mobileAuthenticationHandler());
        }

    }
    @Override
    public void configureWebflowExecutionPlan(final CasWebflowExecutionPlan plan) {
        plan.registerWebflowConfigurer(mobileWebflowConfigurer());
    }
}