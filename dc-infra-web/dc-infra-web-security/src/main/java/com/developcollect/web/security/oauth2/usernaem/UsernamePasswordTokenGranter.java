package com.developcollect.web.security.oauth2.usernaem;

import com.developcollect.web.security.oauth2.Token;
import com.developcollect.web.security.oauth2.TokenGranter;
import com.developcollect.web.security.oauth2.TokenRequest;
import com.developcollect.web.security.oauth2.auth.TokenProcessor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

public class UsernamePasswordTokenGranter implements TokenGranter {

    private static final String GRANT_TYPE = "password";

    private PasswordEncoder passwordEncoder;
    private UserDetailsService userDetailsService;
    private TokenProcessor tokenProcessor;

    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public void setUserDetailsService(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    public void setTokenProcessor(TokenProcessor tokenProcessor) {
        this.tokenProcessor = tokenProcessor;
    }

    @Override
    public Token grant(TokenRequest tokenRequest) {
        String username = tokenRequest.getParameter("username");
        String password = tokenRequest.getParameter("password");
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        // 首先验证密码
        if (!passwordEncoder.matches(password, userDetails.getPassword())) {
            throw new BadCredentialsException("密码错误");
        }

        // 验证通过后颁发token
        Token token = tokenProcessor.grantToken(tokenRequest, userDetails);

        return token;
    }


    @Override
    public String getGrantType() {
        return GRANT_TYPE;
    }

}
