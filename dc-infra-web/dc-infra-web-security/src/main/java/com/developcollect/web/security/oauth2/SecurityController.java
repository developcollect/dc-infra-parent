package com.developcollect.web.security.oauth2;

import com.developcollect.web.security.oauth2.refresh.RefreshTokenRequest;
import com.developcollect.web.security.oauth2.usernaem.UsernameTokenRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@RestController
public class SecurityController {

    @Autowired
    private TokenGranter tokenGranter;

    @Autowired
    private AuthenticationEntryPoint authenticationEntryPoint;


    @PostMapping("/oauth/token")
    public Token login(HttpServletRequest request) {
        UsernameTokenRequest accessTokenRequest = UsernameTokenRequest.of(request);
        if (!tokenGranter.support(accessTokenRequest)) {
            throw new BadCredentialsException("不支持 " + accessTokenRequest.getGrantType());
        }

        // 颁发token
        return tokenGranter.grant(accessTokenRequest);

    }

    @PostMapping("/oauth/refresh")
    public Token refresh(HttpServletRequest request) {
        RefreshTokenRequest refreshTokenRequest = RefreshTokenRequest.of(request);
        return tokenGranter.grant(refreshTokenRequest);
    }

    @ExceptionHandler(AuthenticationException.class)
    public void ex(AuthenticationException exception, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        authenticationEntryPoint.commence(request, response, exception);
    }


    public static void main(String[] args) {

    }

}