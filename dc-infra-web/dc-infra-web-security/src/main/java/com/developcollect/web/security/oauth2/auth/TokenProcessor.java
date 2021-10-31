package com.developcollect.web.security.oauth2.auth;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.exceptions.ValidateException;
import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTUtil;
import cn.hutool.jwt.JWTValidator;
import cn.hutool.jwt.signers.JWTSigner;
import cn.hutool.jwt.signers.JWTSignerUtil;
import com.developcollect.core.utils.DateUtil;
import com.developcollect.core.utils.LambdaUtil;
import com.developcollect.core.utils.StrUtil;
import com.developcollect.web.common.security.DcSecurityUser;
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
import java.util.function.Consumer;
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
    protected static final int DEFAULT_EXPIRES = 2 * 60 * 60;
    protected static final int DEFAULT_REFRESH_EXPIRES = 3 * 60 * 60;

    private final Consumer<JWT> accessTokenJwtConsumer;
    private final Consumer<JWT> refreshTokenJwtConsumer;


    /**
     * @param key 加密和验签的秘钥
     */
    public TokenProcessor(String key) {
        this(key, LambdaUtil::nop, LambdaUtil::nop);
    }

    public TokenProcessor(String key, Consumer<JWT> accessTokenJwtConsumer, Consumer<JWT> refreshTokenJwtConsumer) {
        this(JWTSignerUtil.hs256(Base64.decode(key)), accessTokenJwtConsumer, refreshTokenJwtConsumer);
    }

    public TokenProcessor(JWTSigner jwtSigner, Consumer<JWT> accessTokenJwtConsumer, Consumer<JWT> refreshTokenJwtConsumer) {
        this.jwtSigner = jwtSigner;
        this.accessTokenJwtConsumer = accessTokenJwtConsumer;
        this.refreshTokenJwtConsumer = refreshTokenJwtConsumer;
    }

    public Token grantToken(TokenRequest tokenRequest, UserDetails userDetails) {
        Date now = new Date();

        JWT accessTokenJwt = JWT.create()
                .setPayload(CLIENT_ID_PAYLOAD_NAME, tokenRequest.getClientId())
                .setPayload(USERNAME_PAYLOAD_NAME, userDetails.getUsername())
                .setPayload(AUTHORITIES_PAYLOAD_NAME, userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.joining(",")))
                .setExpiresAt(DateUtil.offsetSecond(now, DEFAULT_EXPIRES))
                .setSigner(jwtSigner);

        accessTokenJwtConsumer.accept(accessTokenJwt);

        JWT refreshTokenJwt = JWT.create()
                .setPayload(CLIENT_ID_PAYLOAD_NAME, tokenRequest.getClientId())
                .setPayload(USERNAME_PAYLOAD_NAME, userDetails.getUsername())
                .setExpiresAt(DateUtil.offsetSecond(now, DEFAULT_REFRESH_EXPIRES))
                .setSigner(jwtSigner);

        refreshTokenJwtConsumer.accept(refreshTokenJwt);


        if (userDetails instanceof DcSecurityUser) {
            Long id = ((DcSecurityUser) userDetails).getUserId();
            accessTokenJwt.setPayload(USER_ID_PAYLOAD_NAME, String.valueOf(id));
            refreshTokenJwt.setPayload(USER_ID_PAYLOAD_NAME, String.valueOf(id));
        }


        Token token = new Token();
        token.setAccessToken(accessTokenJwt.sign());
        token.setRefreshToken(refreshTokenJwt.sign());
        token.setExpires(((Date) accessTokenJwt.getPayload("exp")).getTime() / 1000);
        token.setRefreshExpires(((Date) refreshTokenJwt.getPayload("exp")).getTime() / 1000);

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
            Long userId = null;
            String clientId = null;
            Object userIdPayload = jwt.getPayload(USER_ID_PAYLOAD_NAME);
            if (userIdPayload != null) {
                if (userIdPayload instanceof Number) {
                    userId = ((Number) userIdPayload).longValue();
                } else {
                    try {
                        userId = Long.parseLong(userIdPayload.toString());
                    } catch (NumberFormatException ignore) {
                    }
                }
            }
            Object clientIdPayload = jwt.getPayload(CLIENT_ID_PAYLOAD_NAME);
            if (clientIdPayload != null) {
                clientId = clientIdPayload.toString();
            }
            String username = jwt.getPayload(USERNAME_PAYLOAD_NAME).toString();
            String authoritiesStr = jwt.getPayload(AUTHORITIES_PAYLOAD_NAME).toString();

            List<SimpleGrantedAuthority> authorities;
            if (StrUtil.isNotBlank(authoritiesStr)) {
                authorities = Arrays.stream(authoritiesStr.split(",")).map(SimpleGrantedAuthority::new).collect(Collectors.toList());
            } else {
                authorities = Collections.emptyList();
            }

            DcSecurityUser principal = new DcSecurityUser(userId, clientId, username, "", authorities);
            JwtAuthenticationToken authenticationToken = new JwtAuthenticationToken(principal, null, authorities);
            return authenticationToken;
        } catch (Exception e) {
            throw new BadCredentialsException("无效的token");
        }
    }


}
