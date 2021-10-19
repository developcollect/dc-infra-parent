package com.developcollect.web.security.oauth2.refresh;

import com.developcollect.web.security.oauth2.Token;
import com.developcollect.web.security.oauth2.TokenGranter;
import com.developcollect.web.security.oauth2.TokenRequest;
import com.developcollect.web.security.oauth2.auth.TokenProcessor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

public class RefreshTokenGranter implements TokenGranter {

    private UserDetailsService userDetailsService;
    private TokenProcessor tokenProcessor;

    @Override
    public Token grant(TokenRequest tokenRequest) {
        RefreshTokenRequest refreshTokenRequest = (RefreshTokenRequest) tokenRequest;
        String refreshToken = refreshTokenRequest.getRefreshToken();

        if (!tokenProcessor.verify(refreshToken)) {
            throw new BadCredentialsException("invalid token");
        }

        Authentication authentication = tokenProcessor.loadAuthentication(refreshToken);
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
    public boolean support(TokenRequest tokenRequest) {
        return tokenRequest instanceof RefreshTokenRequest;
    }

    @Override
    public String getGrantType() {
        return "refreshToken";
    }
}
