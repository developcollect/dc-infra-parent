package com.developcollect.web.security.oauth2;

import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTUtil;
import com.developcollect.core.utils.StrUtil;
import com.developcollect.web.security.oauth2.auth.BearerTokenExtractor;
import com.developcollect.web.security.oauth2.auth.TokenProcessor;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

public class TokenUtil {


    public static String extractToken(HttpServletRequest servletRequest) {
        return new BearerTokenExtractor().extractHeaderToken(servletRequest);
    }

    public static JWT parseToken(String token) {
        return JWTUtil.parseToken(token);
    }

    public static String getClientId(String token) {
        return getClientId(parseToken(token));
    }

    public static String getUserId(String token) {
        return getUserId(parseToken(token));
    }

    public static String getUserName(String token) {
        return getUserName(parseToken(token));
    }

    public static Set<String> getAuthorities(String token) {
        return getAuthorities(parseToken(token));
    }


    public static String getClientId(JWT jwt) {
        return (String) jwt.getPayload(TokenProcessor.CLIENT_ID_PAYLOAD_NAME);
    }

    public static String getUserId(JWT jwt) {
        return (String) jwt.getPayload(TokenProcessor.USER_ID_PAYLOAD_NAME);
    }

    public static String getUserName(JWT jwt) {
        return (String) jwt.getPayload(TokenProcessor.USERNAME_PAYLOAD_NAME);
    }

    public static Set<String> getAuthorities(JWT jwt) {
        String authoritiesStr = (String) jwt.getPayload(TokenProcessor.AUTHORITIES_PAYLOAD_NAME);
        if (StrUtil.isBlank(authoritiesStr)) {
            return Collections.emptySet();
        }
        Set<String> authorities = Arrays.stream(authoritiesStr.split(",")).map(String::trim).collect(Collectors.toSet());
        return authorities;
    }
}
