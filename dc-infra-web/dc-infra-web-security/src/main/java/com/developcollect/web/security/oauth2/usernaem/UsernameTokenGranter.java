package com.developcollect.web.security.oauth2.usernaem;

import com.developcollect.web.security.oauth2.Token;
import com.developcollect.web.security.oauth2.TokenGranter;
import com.developcollect.web.security.oauth2.TokenRequest;
import com.developcollect.web.security.oauth2.auth.TokenProcessor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

public class UsernameTokenGranter implements TokenGranter {

    private static final String GRANT_TYPE = "username";

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
        UsernameTokenRequest accessTokenRequest = (UsernameTokenRequest) tokenRequest;
        UserDetails userDetails = userDetailsService.loadUserByUsername(accessTokenRequest.getUsername());

        // 首先验证密码
        if (false == passwordEncoder.matches(accessTokenRequest.getPassword(), userDetails.getPassword())) {
            throw new BadCredentialsException("密码错误");
        }
        accessTokenRequest.eraseCredentials();

        // 验证通过后颁发token
        Token token = tokenProcessor.grantToken(accessTokenRequest, userDetails);

        return token;
    }


    @Override
    public boolean support(TokenRequest tokenRequest) {
        return tokenRequest instanceof UsernameTokenRequest && getGrantType().equals(tokenRequest.getGrantType());
    }

    @Override
    public String getGrantType() {
        return GRANT_TYPE;
    }

}
