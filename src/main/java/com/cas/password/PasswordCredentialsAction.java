package com.cas.password;

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
public class PasswordCredentialsAction extends AbstractNonInteractiveCredentialsAction {

    public PasswordCredentialsAction(CasDelegatingWebflowEventResolver initialAuthenticationAttemptWebflowEventResolver,
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
            PasswordCredential credentials = new PasswordCredential(request.getParameter("username"),
                    request.getParameter("password"),
                    request.getParameter("captcha"));
            if (credentials != null) {
                log.debug("Received password authentication request from credentials [{}]", credentials);
                return credentials;
            }
        } catch (final Exception e) {
            log.warn(e.getMessage(), e);
            e.printStackTrace();
        }
        return null;
    }
}
