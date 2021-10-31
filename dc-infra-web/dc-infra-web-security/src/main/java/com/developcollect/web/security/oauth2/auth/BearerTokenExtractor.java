package com.developcollect.web.security.oauth2.auth;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.core.Authentication;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

/**
 * @author Zhu KaiXiao
 * @version 1.0
 * @date 2021/3/9 13:29
 */
public class BearerTokenExtractor implements TokenExtractor {

    private final static Log logger = LogFactory.getLog(BearerTokenExtractor.class);

    @Override
    public Authentication extract(HttpServletRequest request) {
        String tokenValue = extractToken(request);
        if (tokenValue != null) {
            JwtAuthenticationToken authentication = new JwtAuthenticationToken(tokenValue, "");
            return authentication;
        }
        return null;
    }

    protected String extractToken(HttpServletRequest request) {
        // first check the header...
        String token = extractHeaderToken(request);

        // bearer type allows a request parameter as well
        if (token == null) {
            logger.debug("Token not found in headers. Trying request parameters.");
            token = request.getParameter("access_token");
            if (token == null) {
                logger.debug("Token not found in request parameters.  Not an OAuth2 request.");
            } else {
                request.setAttribute("ACCESS_TOKEN_TYPE", "Bearer");
            }
        }

        return token;
    }

    /**
     * Extract the OAuth bearer token from a header.
     *
     * @param request The request.
     * @return The token, or null if no OAuth authorization header was supplied.
     */
    public String extractHeaderToken(HttpServletRequest request) {
        Enumeration<String> headers = request.getHeaders("Authorization");
        // typically there is only one (most servers enforce that)
        while (headers.hasMoreElements()) {
            String value = headers.nextElement();
            if ((value.toLowerCase().startsWith("Bearer".toLowerCase()))) {
                String authHeaderValue = value.substring("Bearer".length()).trim();
                // Add this here for the auth details later. Would be better to change the signature of this method.
                request.setAttribute("ACCESS_TOKEN_TYPE",
                        value.substring(0, "Bearer".length()).trim());
                int commaIndex = authHeaderValue.indexOf(',');
                if (commaIndex > 0) {
                    authHeaderValue = authHeaderValue.substring(0, commaIndex);
                }
                return authHeaderValue;
            }
        }

        return null;
    }

}
