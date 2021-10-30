package com.developcollect.web.security.oauth2.usernaem;

import com.developcollect.web.security.oauth2.TokenRequest;
import lombok.Data;
import org.springframework.security.core.CredentialsContainer;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Zhu KaiXiao
 * @version 1.0
 * @date 2021/3/9 11:04
 */
@Data
public class UsernamePasswordTokenRequest implements TokenRequest, CredentialsContainer {

    private String grantType;
    private String clientId;
    private String username;
    private String password;

    private HttpServletRequest request;


    public static UsernamePasswordTokenRequest of(HttpServletRequest request) {
        UsernamePasswordTokenRequest accessTokenRequest = new UsernamePasswordTokenRequest();
        accessTokenRequest.setClientId(request.getParameter("clientId"));
        accessTokenRequest.setGrantType(request.getParameter("grantType"));
        accessTokenRequest.setUsername(request.getParameter("username"));
        accessTokenRequest.setPassword(request.getParameter("password"));
        accessTokenRequest.setRequest(request);
        return accessTokenRequest;
    }


    @Override
    public void eraseCredentials() {
        this.password = null;
    }

    @Override
    public HttpServletRequest getServletRequest() {
        return request;
    }
}
