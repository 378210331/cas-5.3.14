package com.cas.controller;

import lombok.extern.slf4j.Slf4j;
import org.apereo.cas.CentralAuthenticationService;
import org.apereo.cas.authentication.*;
import org.apereo.cas.authentication.principal.Service;
import org.apereo.cas.authentication.principal.ServiceFactory;
import org.apereo.cas.configuration.CasConfigurationProperties;
import org.apereo.cas.rest.BadRestRequestException;
import org.apereo.cas.rest.factory.RestHttpRequestCredentialFactory;
import org.apereo.cas.rest.factory.ServiceTicketResourceEntityResponseFactory;
import org.apereo.cas.ticket.InvalidTicketException;
import org.apereo.cas.ticket.TicketGrantingTicket;
import org.apereo.cas.ticket.registry.TicketRegistrySupport;
import org.apereo.cas.web.support.ArgumentExtractor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.MediaType;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;

@org.springframework.web.bind.annotation.RestController("TicketRestController")
@EnableConfigurationProperties({CasConfigurationProperties.class})
@Slf4j
public class TicketRestController {

    private final AuthenticationSystemSupport authenticationSystemSupport;
    private final TicketRegistrySupport ticketRegistrySupport;
    private final ArgumentExtractor argumentExtractor;
    private final ServiceTicketResourceEntityResponseFactory serviceTicketResourceEntityResponseFactory;
    private final RestHttpRequestCredentialFactory credentialFactory;
    private final CentralAuthenticationService centralAuthenticationService;
    private final ServiceFactory serviceFactory;

    public TicketRestController(AuthenticationSystemSupport authenticationSystemSupport, TicketRegistrySupport ticketRegistrySupport, ArgumentExtractor argumentExtractor
            , ServiceTicketResourceEntityResponseFactory serviceTicketResourceEntityResponseFactory, RestHttpRequestCredentialFactory factory, RestHttpRequestCredentialFactory credentialFactory, CentralAuthenticationService centralAuthenticationService,
                                ServiceFactory serviceFactory){
        this.authenticationSystemSupport = authenticationSystemSupport;
        this.ticketRegistrySupport = ticketRegistrySupport;
        this.argumentExtractor  = argumentExtractor;
        this.serviceTicketResourceEntityResponseFactory = serviceTicketResourceEntityResponseFactory;
        this.credentialFactory = credentialFactory;
        this.centralAuthenticationService = centralAuthenticationService;
        this.serviceFactory = serviceFactory;
    }


    @PostMapping(value = "/tickets", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String createTicketGrantingTicket(@RequestBody(required=false) final MultiValueMap<String, String> requestBody,
                                                             final HttpServletRequest request) {
        try {
            final TicketGrantingTicket tgtId = createTicketGrantingTicketForRequest(requestBody, request);
            final Authentication authn = this.ticketRegistrySupport.getAuthenticationFrom(tgtId.getId());
            AuthenticationCredentialsThreadLocalBinder.bindCurrent(authn);
            if (authn == null) {
                throw new InvalidTicketException(tgtId.getId());
            }
            final AuthenticationResultBuilder builder = new DefaultAuthenticationResultBuilder();
            final Service service = this.argumentExtractor.extractService(request);
            if (service == null) {
                throw new IllegalArgumentException("Target service/application is unspecified or unrecognized in the request");
            }
            final AuthenticationResult authenticationResult = builder.collect(authn).build(this.authenticationSystemSupport.getPrincipalElectionStrategy(), service);
            return this.serviceTicketResourceEntityResponseFactory.build(tgtId.getId(), service, authenticationResult).getBody();
        } catch (final Exception e) {
            return "";
        } finally {
            AuthenticationCredentialsThreadLocalBinder.clear();
        }
    }

    protected TicketGrantingTicket createTicketGrantingTicketForRequest(final MultiValueMap<String, String> requestBody,
                                                                        final HttpServletRequest request) {
        final Collection<Credential> credential = this.credentialFactory.fromRequest(request, requestBody);
        if (credential == null || credential.isEmpty()) {
            throw new BadRestRequestException("No credentials are provided or extracted to authenticate the REST request");
        }
        final Service service = this.serviceFactory.createService(request);
        final AuthenticationResult authenticationResult =
                authenticationSystemSupport.handleAndFinalizeSingleAuthenticationTransaction(service, credential);
        return centralAuthenticationService.createTicketGrantingTicket(authenticationResult);
    }
}
