package com.developcollect.web.security.oauth2.auth;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

/**
 * @author Zhu KaiXiao
 * @version 1.0
 * @date 2021/3/9 15:34
 */
public class JwtTokenAuthProvider implements AuthenticationProvider {

    private TokenProcessor tokenProcessor;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        JwtAuthenticationToken jwtAuthenticationToken = (JwtAuthenticationToken) authentication;

        if (!tokenProcessor.verify(jwtAuthenticationToken.getPrincipal().toString())) {
            throw new BadCredentialsException("无效的token");
        }

        Authentication auth = tokenProcessor.loadAuthentication(jwtAuthenticationToken.getPrincipal().toString());
        return auth;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return JwtAuthenticationToken.class.isAssignableFrom(authentication);
    }

    public void setTokenProcessor(TokenProcessor tokenProcessor) {
        this.tokenProcessor = tokenProcessor;
    }
}
