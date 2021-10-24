package com.developcollect.web.security.oauth2.sms;

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
public class SmsTokenRequest implements TokenRequest, CredentialsContainer {

    private String grantType;
    private String clientId;
    private String mobile;
    private String code;

    private HttpServletRequest servletRequest;


    public static SmsTokenRequest of(HttpServletRequest request) {
        SmsTokenRequest accessTokenRequest = new SmsTokenRequest();
        accessTokenRequest.setClientId(request.getParameter("clientId"));
        accessTokenRequest.setGrantType(request.getParameter("grantType"));
        accessTokenRequest.setMobile(request.getParameter("mobile"));
        accessTokenRequest.setCode(request.getParameter("code"));
        accessTokenRequest.setServletRequest(request);
        return accessTokenRequest;
    }


    @Override
    public void eraseCredentials() {
        this.code = null;
    }

    @Override
    public HttpServletRequest getServletRequest() {
        return servletRequest;
    }
}
