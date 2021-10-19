package com.developcollect.web.security.oauth2.auth;

import cn.hutool.core.codec.Base64;
import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTUtil;
import cn.hutool.jwt.JWTValidator;
import cn.hutool.jwt.signers.JWTSigner;
import cn.hutool.jwt.signers.JWTSignerUtil;
import com.developcollect.core.utils.StrUtil;
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


    /**
     * @param key 加密和验签的秘钥
     */
    public TokenProcessor(String key) {
        this.jwtSigner = JWTSignerUtil.hs256(Base64.decode(key));
        this.expires = 2 * 60 * 60 * 1000;
        this.refreshExpires = 3 * 60 * 60 * 1000;
    }

    public Token grantToken(TokenRequest tokenRequest, UserDetails userDetails) {
        JWT accessTokenJwt = JWT.create()
                .setPayload("clientId", tokenRequest.getClientId())
                .setPayload("username", userDetails.getUsername())
                .setPayload("authorities", userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.joining(",")))
                .setExpiresAt(new Date(System.currentTimeMillis() + expires))
                .setSigner(jwtSigner);

        JWT refreshTokenJwt = JWT.create()
                .setPayload("clientId", tokenRequest.getClientId())
                .setPayload("username", userDetails.getUsername())
                .setExpiresAt(new Date(System.currentTimeMillis() + refreshExpires))
                .setSigner(jwtSigner);


        if (userDetails instanceof IdUserDetails) {
            Object id = ((IdUserDetails<?>) userDetails).getId();
            accessTokenJwt.setPayload("userId", String.valueOf(id));
            refreshTokenJwt.setPayload("userId", String.valueOf(id));
        }


        Token token = new Token();
        token.setAccessToken(accessTokenJwt.sign());
        token.setRefreshToken(refreshTokenJwt.sign());
        token.setExpires(expires);
        token.setRefreshExpires(refreshExpires);

        return token;
    }

    /**
     * 这里只对token进行验签，不对有效期做验证
     *
     * @param token token
     * @return
     */
    public boolean verify(String token) {
        JWTValidator.of(token).validateAlgorithm(jwtSigner);
        return JWTUtil.verify(token, jwtSigner);
    }


    public Authentication loadAuthentication(String token) {
        // 从jwt中拿出userid，username，权限
        try {
            JWT jwt = JWTUtil.parseToken(token);
            String username = jwt.getPayload("username").toString();
            String authoritiesStr = jwt.getPayload("authorities").toString();

            List<SimpleGrantedAuthority> authorities;
            if (StrUtil.isNotBlank(authoritiesStr)) {
                authorities = Arrays.stream(authoritiesStr.split(",")).map(SimpleGrantedAuthority::new).collect(Collectors.toList());
            } else {
                authorities = Collections.emptyList();
            }

            JwtAuthenticationToken authenticationToken = new JwtAuthenticationToken(username, null, authorities);
            return authenticationToken;
        } catch (Exception e) {
            throw new BadCredentialsException("invalid token");
        }
    }


}
