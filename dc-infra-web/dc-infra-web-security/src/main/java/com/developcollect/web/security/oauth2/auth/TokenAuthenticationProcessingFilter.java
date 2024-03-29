package com.developcollect.web.security.oauth2.auth;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 参考OAuth2AuthenticationProcessingFilter
 *
 * @author Zhu KaiXiao
 * @version 1.0
 * @date 2021/3/9 13:16
 */
@Slf4j
public class TokenAuthenticationProcessingFilter implements Filter {

    @Setter
    private AuthenticationManager authenticationManager;
    @Setter
    private TokenExtractor tokenExtractor = new BearerTokenExtractor();
    private boolean stateless = true;
    @Setter
    private AuthenticationEntryPoint authenticationEntryPoint;
    @Setter
    private AuthenticationDetailsSource<HttpServletRequest, ?> authenticationDetailsSource;

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        final boolean debug = log.isDebugEnabled();
        final HttpServletRequest request = (HttpServletRequest) req;
        final HttpServletResponse response = (HttpServletResponse) res;

        try {
            Authentication authentication = tokenExtractor.extract(request);

            if (authentication == null) {
                if (stateless && isAuthenticated()) {
                    SecurityContextHolder.clearContext();
                }
                if (debug) {
                    log.debug("No token in request, will continue chain.");
                }
            } else {
                request.setAttribute("ACCESS_TOKEN_VALUE", authentication.getPrincipal());
                if (authenticationDetailsSource != null && authentication instanceof AbstractAuthenticationToken) {
                    AbstractAuthenticationToken needsDetails = (AbstractAuthenticationToken) authentication;
                    needsDetails.setDetails(authenticationDetailsSource.buildDetails(request));
                }
                Authentication authResult = authenticationManager.authenticate(authentication);

                if (debug) {
                    log.debug("Authentication success: " + authResult);
                }

//            eventPublisher.publishAuthenticationSuccess(authResult);
                SecurityContextHolder.getContext().setAuthentication(authResult);

            }
        } catch (AuthenticationException failed) {
            failed.printStackTrace();
            SecurityContextHolder.clearContext();

            if (debug) {
                log.debug("Authentication request failed: " + failed);
            }

            authenticationEntryPoint.commence(request, response, failed);
            return;
        }


        chain.doFilter(request, response);
    }



    private boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && !(authentication instanceof AnonymousAuthenticationToken);
    }
}
