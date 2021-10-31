package com.developcollect.web.security.oauth2;

import lombok.Getter;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class TokenRequest {

    @Getter
    protected String clientId;
    @Getter
    protected String grantType;
    @Getter
    protected Map<String, String> requestParameters = Collections.unmodifiableMap(new HashMap<>());


    public TokenRequest(Map<String, String> requestParameters) {
        if (requestParameters != null) {
            this.requestParameters = Collections
                    .unmodifiableMap(new HashMap<>(requestParameters));
            this.clientId = getParameter("clientId");
            this.grantType = getParameter("grantType");
        }
    }

    public String getParameter(String key) {
        return getRequestParameters().get(key);
    }


    public static TokenRequest of(Map<String, String> requestParameters) {
        return new TokenRequest(requestParameters);
    }

}
