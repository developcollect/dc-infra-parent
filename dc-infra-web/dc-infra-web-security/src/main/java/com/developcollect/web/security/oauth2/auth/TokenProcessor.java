package com.developcollect.web.security.oauth2.auth;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.exceptions.ValidateException;
import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTUtil;
import cn.hutool.jwt.JWTValidator;
import cn.hutool.jwt.signers.JWTSigner;
import cn.hutool.jwt.signers.JWTSignerUtil;
import com.developcollect.core.utils.DateUtil;
import com.developcollect.core.utils.StrUtil;
import com.developcollect.web.security.oauth2.Token;
import com.developcollect.web.security.oauth2.TokenRequest;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class TokenProcessor {

    protected static final String CLIENT_ID_PAYLOAD_NAME = "cid";
    protected static final String USERNAME_PAYLOAD_NAME = "u";
    protected static final String AUTHORITIES_PAYLOAD_NAME = "a";
    protected static final String USER_ID_PAYLOAD_NAME = "uid";

    protected final JWTSigner jwtSigner;
    protected final int expires;
    protected final int refreshExpires;


    /**
     * @param key 加密和验签的秘钥
     */
    public TokenProcessor(String key) {
        this(key, 2 * 60 * 60, 3 * 60 * 60);
    }

    public TokenProcessor(String key, int expires, int refreshExpires) {
        this(JWTSignerUtil.hs256(Base64.decode(key)), expires, refreshExpires);
    }

    public TokenProcessor(JWTSigner jwtSigner, int expires, int refreshExpires) {
        this.jwtSigner = jwtSigner;
        this.expires = expires;
        this.refreshExpires = refreshExpires;
    }

    public Token grantToken(TokenRequest tokenRequest, UserDetails userDetails) {
        Date now = new Date();

        JWT accessTokenJwt = JWT.create()
                .setPayload(CLIENT_ID_PAYLOAD_NAME, tokenRequest.getClientId())
                .setPayload(USERNAME_PAYLOAD_NAME, userDetails.getUsername())
                .setPayload(AUTHORITIES_PAYLOAD_NAME, userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.joining(",")))
                .setExpiresAt(DateUtil.offsetSecond(now, expires))
                .setSigner(jwtSigner);

        JWT refreshTokenJwt = JWT.create()
                .setPayload(CLIENT_ID_PAYLOAD_NAME, tokenRequest.getClientId())
                .setPayload(USERNAME_PAYLOAD_NAME, userDetails.getUsername())
                .setExpiresAt(DateUtil.offsetSecond(now, refreshExpires))
                .setSigner(jwtSigner);


        if (userDetails instanceof IdUserDetails) {
            Object id = ((IdUserDetails<?>) userDetails).getId();
            accessTokenJwt.setPayload(USER_ID_PAYLOAD_NAME, String.valueOf(id));
            refreshTokenJwt.setPayload(USER_ID_PAYLOAD_NAME, String.valueOf(id));
        }


        Token token = new Token();
        token.setAccessToken(accessTokenJwt.sign());
        token.setRefreshToken(refreshTokenJwt.sign());
        token.setExpires((long) expires);
        token.setRefreshExpires((long) refreshExpires);

        return token;
    }

    /**
     * 验证token，首先对token验签，然后验证有效期
     *
     * @param token token
     */
    public boolean verify(String token) {
        try {
            JWTValidator.of(token)
                    .validateAlgorithm(jwtSigner)
                    .validateDate(DateUtil.date());

            return true;
        } catch (Exception e) {
            if (!(e instanceof ValidateException)) {
                log.error("验证token时出现异常", e);
            }
            return false;
        }
    }


    public Authentication loadAuthentication(String token) {
        // 从jwt中拿出userid，username，权限
        try {
            JWT jwt = JWTUtil.parseToken(token);
            String username = jwt.getPayload(USERNAME_PAYLOAD_NAME).toString();
            String authoritiesStr = jwt.getPayload(AUTHORITIES_PAYLOAD_NAME).toString();

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
