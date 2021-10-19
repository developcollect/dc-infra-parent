package com.developcollect.web.security.oauth2.auth;

import org.springframework.security.core.Authentication;

import javax.servlet.http.HttpServletRequest;

public interface TokenExtractor {

    /**
     * Extract a token value from an incoming request without authentication.
     *
     * @param request the current ServletRequest
     * @return an authentication token whose principal is an access token (or null if there is none)
     */
    Authentication extract(HttpServletRequest request);


}
