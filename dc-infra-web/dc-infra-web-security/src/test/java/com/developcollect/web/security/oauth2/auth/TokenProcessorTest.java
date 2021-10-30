package com.developcollect.web.security.oauth2.auth;

import com.developcollect.web.security.oauth2.Token;
import com.developcollect.web.security.oauth2.usernaem.UsernamePasswordTokenRequest;
import org.junit.Test;
import org.springframework.security.core.userdetails.User;

import java.util.Collections;

public class TokenProcessorTest {

    private static TokenProcessor tokenProcessor = new TokenProcessor("KNJIHQWESNXHHOIWHE98273NH98Y823H98HJKFAH8SDY1");

    @Test
    public void test_token() {
        UsernamePasswordTokenRequest tokenRequest = new UsernamePasswordTokenRequest();
        tokenRequest.setClientId("clientId");
        User user = new User("137", "pass", Collections.emptyList());
        Token token = tokenProcessor.grantToken(tokenRequest, user);
        System.out.println(token);

        System.out.println(tokenProcessor.verify(token.getAccessToken()));
    }

}