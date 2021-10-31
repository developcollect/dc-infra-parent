package com.developcollect.web.security.oauth2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;


/**
 * 不走自动配置，只是个配置示例
 */
public class SecurityController {

    @Autowired
    private TokenGranter tokenGranter;

    @Autowired
    private AuthenticationEntryPoint authenticationEntryPoint;


    @PostMapping("/oauth/token")
    public Token login(@RequestParam Map<String, String> parameters) {
        TokenRequest tokenRequest = TokenRequest.of(parameters);
        if (!tokenGranter.support(tokenRequest)) {
            throw new BadCredentialsException("不支持 " + tokenRequest.getGrantType());
        }

        // 颁发token
        return tokenGranter.grant(tokenRequest);

    }

    @PostMapping("/oauth/refresh")
    public Token refresh(@RequestParam Map<String, String> parameters) {
        TokenRequest tokenRequest = TokenRequest.of(parameters);
        if (!"refreshToken".equals(tokenRequest.getGrantType())) {
            throw new IllegalArgumentException("grantType错误");
        }
        return tokenGranter.grant(tokenRequest);
    }

    @ExceptionHandler(AuthenticationException.class)
    public void ex(AuthenticationException exception, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        authenticationEntryPoint.commence(request, response, exception);
    }


}
