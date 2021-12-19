package com.developcollect.web.security.oauth2.refresh;

import com.developcollect.web.security.oauth2.Token;
import com.developcollect.web.security.oauth2.TokenGranter;
import com.developcollect.web.security.oauth2.TokenRequest;
import com.developcollect.web.security.oauth2.auth.TokenProcessor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Map;

public class RefreshTokenGranter implements TokenGranter {

    private UserDetailsService userDetailsService;
    private TokenProcessor tokenProcessor;

    @Override
    public Token grant(TokenRequest tokenRequest) {
        Map<String, String> requestParameters = tokenRequest.getRequestParameters();
        String refreshToken = requestParameters.get("refreshToken");

        if (!tokenProcessor.verify(refreshToken)) {
            throw new BadCredentialsException("无效的token");
        }

        Authentication authentication;
        try {
            authentication = tokenProcessor.loadAuthentication(refreshToken);
        } catch (Exception e) {
            throw new BadCredentialsException("无效的token", e);
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(authentication.getPrincipal().toString());

        return tokenProcessor.grantToken(tokenRequest, userDetails);
    }


    public void setUserDetailsService(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    public void setTokenProcessor(TokenProcessor tokenProcessor) {
        this.tokenProcessor = tokenProcessor;
    }


    @Override
    public String getGrantType() {
        return "refreshToken";
    }
}
