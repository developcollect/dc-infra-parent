package com.developcollect.web.security.oauth2.refresh;

import com.developcollect.web.security.oauth2.TokenRequest;
import lombok.Data;

import javax.servlet.http.HttpServletRequest;

@Data
public class RefreshTokenRequest implements TokenRequest {

    public String grantType;
    public String refreshToken;
    public String clientId;
    public HttpServletRequest request;

    public static RefreshTokenRequest of(HttpServletRequest request) {
        RefreshTokenRequest refreshTokenRequest = new RefreshTokenRequest();
        refreshTokenRequest.setRefreshToken(request.getParameter("refreshToken"));
        refreshTokenRequest.setClientId(request.getParameter("clientId"));
        refreshTokenRequest.setGrantType("refreshToken");
        return refreshTokenRequest;
    }

    @Override
    public HttpServletRequest getServletRequest() {
        return request;
    }
}
