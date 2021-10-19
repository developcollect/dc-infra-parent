package com.developcollect.web.security.oauth2.auth;

import cn.hutool.core.codec.Base64;
import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTUtil;
import cn.hutool.jwt.signers.JWTSigner;
import cn.hutool.jwt.signers.JWTSignerUtil;
import com.developcollect.web.security.oauth2.Token;
import com.developcollect.web.security.oauth2.TokenRequest;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Zhu KaiXiao
 * @version 1.0
 * @date 2021/3/9 10:45
 */
public class TokenProcessor {

    private JWTSigner jwtSigner;
    private long expires;
    private long refreshExpires;


    public TokenProcessor() {
        JWTSigner jwtSigner = JWTSignerUtil.hs256(Base64.decode("KNJIHQWESNXHHOIWHE98273NH98Y823H98HJKFAH8SDY1"));
        this.expires = 2 * 60 * 60 * 1000;
        this.refreshExpires = 3 * 60 * 60 * 1000;
    }

    public Token grantToken(TokenRequest tokenRequest, UserDetails userDetails) {
        Token token = new Token();
        Object id = null;
        if (userDetails instanceof IdUserDetails) {
            id = ((IdUserDetails<?>) userDetails).getId();
        }

        String accessToken = JWT.create()
                .setPayload("clientId", tokenRequest.getClientId())
                .setPayload("username", userDetails.getUsername())
                .setPayload("userId", String.valueOf(id))
                .setPayload("roles", userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.joining(",")))
                .setPayload(JWT.EXPIRES_AT, new Date(System.currentTimeMillis() + expires))
                .setSigner(jwtSigner)
                .sign();

        String refreshToken = JWT.create()
                .setPayload("clientId", tokenRequest.getClientId())
                .setPayload("username", userDetails.getUsername())
                .setPayload("userId", String.valueOf(id))
                .setPayload(JWT.EXPIRES_AT, new Date(System.currentTimeMillis() + refreshExpires))
                .setSigner(jwtSigner)
                .sign();

        token.setAccessToken(accessToken);
        token.setRefreshToken(refreshToken);
        token.setExpires(expires);
        token.setRefreshExpires(refreshExpires);

        return token;
    }

    public boolean verify(String token) {
        return JWTUtil.verify(token, jwtSigner);
    }


    public Authentication loadAuthentication(String token) {
        // 从jwt中拿出userid，username，权限
        try {
            JWT jwt = JWTUtil.parseToken(token);
            String username = jwt.getPayload("username").toString();
            String roles = jwt.getPayload("roles").toString();

            List<SimpleGrantedAuthority> authorities;
            if (roles != null && !roles.isEmpty()) {
                authorities = Arrays.stream(roles.split(",")).map(SimpleGrantedAuthority::new).collect(Collectors.toList());
            } else {
                authorities = Collections.emptyList();
            }

            JwtAuthenticationToken authenticationToken = new JwtAuthenticationToken(username, "", authorities);
            return authenticationToken;
        } catch (Exception e) {
            throw new BadCredentialsException("invalid token");
        }
    }


}
