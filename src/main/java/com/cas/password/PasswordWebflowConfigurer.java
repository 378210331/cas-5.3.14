package com.cas.password;

import org.apereo.cas.configuration.CasConfigurationProperties;
import org.apereo.cas.web.flow.CasWebflowConstants;
import org.apereo.cas.web.flow.configurer.AbstractCasWebflowConfigurer;
import org.springframework.context.ApplicationContext;
import org.springframework.webflow.definition.registry.FlowDefinitionRegistry;
import org.springframework.webflow.engine.ActionState;
import org.springframework.webflow.engine.Flow;
import org.springframework.webflow.engine.ViewState;
import org.springframework.webflow.engine.builder.support.FlowBuilderServices;

/**
 * 扩展caswebflow默认的登录方式,使用自定义密码模式
 */
public class PasswordWebflowConfigurer extends AbstractCasWebflowConfigurer {

    public PasswordWebflowConfigurer(FlowBuilderServices flowBuilderServices, FlowDefinitionRegistry loginFlowDefinitionRegistry, ApplicationContext applicationContext, CasConfigurationProperties casProperties) {
        super(flowBuilderServices, loginFlowDefinitionRegistry, applicationContext, casProperties);

    }
    private static final String EVENT_ID_START_AUTHENTICATE_PASSWORD_ID = "startAuthenticatePassword";

    @Override
    protected void doInitialize() {
        final Flow flow = getLoginFlow();
        if (flow != null) {
            //创建Action
            final ActionState actionState = createActionState(flow, EVENT_ID_START_AUTHENTICATE_PASSWORD_ID,
                    //自定义Action
                    createEvaluateAction("passwordCredentialsAction"));
            //添加成功后的Transition
            actionState.getTransitionSet().add(createTransition(CasWebflowConstants.TRANSITION_ID_SUCCESS,
                    CasWebflowConstants.STATE_ID_CREATE_TICKET_GRANTING_TICKET));
            //添加警告的Transition
            actionState.getTransitionSet()
                    .add(createTransition(CasWebflowConstants.TRANSITION_ID_WARN, CasWebflowConstants.TRANSITION_ID_WARN));
            //添加错误的Transition跳转到login
            actionState.getTransitionSet()
                    .add(createTransition(CasWebflowConstants.TRANSITION_ID_ERROR, CasWebflowConstants.STATE_ID_VIEW_LOGIN_FORM));
            //添加认证错误的的Transition跳转到login并显示错误信息
            actionState.getTransitionSet().add(createTransition(CasWebflowConstants.TRANSITION_ID_AUTHENTICATION_FAILURE,
                    CasWebflowConstants.STATE_ID_HANDLE_AUTHN_FAILURE));

            actionState.getExitActionList().add(createEvaluateAction("clearWebflowCredentialsAction"));
            registerMultifactorProvidersStateTransitionsIntoWebflow(actionState);
            ViewState viewLoginState = (ViewState) flow.getState(CasWebflowConstants.STATE_ID_VIEW_LOGIN_FORM);
            createTransitionForState(viewLoginState, "submitPassword", EVENT_ID_START_AUTHENTICATE_PASSWORD_ID, true);
        }
    }
}
