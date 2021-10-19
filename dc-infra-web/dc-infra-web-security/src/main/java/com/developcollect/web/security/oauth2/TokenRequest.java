package com.developcollect.web.security.oauth2;

import javax.servlet.http.HttpServletRequest;

public interface TokenRequest {

    String getGrantType();

    String getClientId();

    HttpServletRequest getServletRequest();
}
