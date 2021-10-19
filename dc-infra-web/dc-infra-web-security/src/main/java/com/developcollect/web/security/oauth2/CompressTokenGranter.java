package com.developcollect.web.security.oauth2;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

public class CompressTokenGranter implements TokenGranter {

    private Map<String, TokenGranter> tokenGranterMap = Collections.emptyMap();

    public CompressTokenGranter(Collection<TokenGranter> tokenGranters) {
        if (tokenGranters == null || tokenGranters.isEmpty()) {
            return;
        }
        tokenGranterMap = tokenGranters.stream().collect(Collectors.toMap(TokenGranter::getGrantType, t -> t));
    }

    @Override
    public Token grant(TokenRequest tokenRequest) {
        return tokenGranterMap.get(tokenRequest.getGrantType()).grant(tokenRequest);
    }

    @Override
    public boolean support(TokenRequest tokenRequest) {
        return tokenGranterMap.containsKey(tokenRequest.getGrantType());
    }

    @Override
    public String getGrantType() {
        throw new RuntimeException("复合颁发器没有颁发类型");
    }
}
