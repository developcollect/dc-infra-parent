package com.developcollect.web.security.oauth2;

public interface TokenGranter {

    Token grant(TokenRequest tokenRequest);


    default boolean support(TokenRequest tokenRequest) {
        return getGrantType().equals(tokenRequest.getGrantType());
    }


    String getGrantType();


}
