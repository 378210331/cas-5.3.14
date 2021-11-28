package com.cas.mobile;

import lombok.extern.slf4j.Slf4j;
import org.apereo.cas.authentication.Credential;
import org.apereo.cas.authentication.adaptive.AdaptiveAuthenticationPolicy;
import org.apereo.cas.web.flow.actions.AbstractNonInteractiveCredentialsAction;
import org.apereo.cas.web.flow.resolver.CasDelegatingWebflowEventResolver;
import org.apereo.cas.web.flow.resolver.CasWebflowEventResolver;
import org.apereo.cas.web.support.WebUtils;
import org.springframework.webflow.execution.RequestContext;

import javax.servlet.http.HttpServletRequest;

@Slf4j
public class MobileCredentialsAction extends AbstractNonInteractiveCredentialsAction {

    public MobileCredentialsAction(CasDelegatingWebflowEventResolver initialAuthenticationAttemptWebflowEventResolver,
                                   CasWebflowEventResolver serviceTicketRequestWebflowEventResolver,
                                   AdaptiveAuthenticationPolicy adaptiveAuthenticationPolicy) {
        super(initialAuthenticationAttemptWebflowEventResolver, serviceTicketRequestWebflowEventResolver,
                adaptiveAuthenticationPolicy);
    }

    @Override
    protected Credential constructCredentialsFromRequest(RequestContext requestContext) {
        try {
            final HttpServletRequest request;
            request = WebUtils.getHttpServletRequestFromExternalWebflowContext(requestContext);
            MobileCredential credentials = new MobileCredential(request.getParameter("phoneNumber"), request.getParameter("validateCode"));
            if (credentials != null) {
                log.debug("Received mobile authentication request from credentials [{}]", credentials);
                return credentials;
            }
        } catch (final Exception e) {
            log.warn(e.getMessage(), e);
            e.printStackTrace();
        }
        return null;
    }
}
