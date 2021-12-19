package com.developcollect.web.security.oauth2.auth;

import com.developcollect.web.security.oauth2.Token;
import com.developcollect.web.security.oauth2.TokenRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

public interface TokenProcessor {
    String CLIENT_ID_PAYLOAD_NAME = "cid";
    String USERNAME_PAYLOAD_NAME = "u";
    String AUTHORITIES_PAYLOAD_NAME = "a";
    String USER_ID_PAYLOAD_NAME = "uid";

    Token grantToken(TokenRequest tokenRequest, UserDetails userDetails);

    boolean verify(String token);

    Authentication loadAuthentication(String token);
}
