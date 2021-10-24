package com.developcollect.web.security.oauth2.sms;

import com.developcollect.notify.CaptchaUtil;
import com.developcollect.web.security.oauth2.Token;
import com.developcollect.web.security.oauth2.TokenGranter;
import com.developcollect.web.security.oauth2.TokenRequest;
import com.developcollect.web.security.oauth2.auth.TokenProcessor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

@Slf4j
public class SmsTokenGranter implements TokenGranter {

    private static final String GRANT_TYPE = "sms";
    private static final String CACHE_CODE_KEY_PREFIX = "DC_SECURITY_SMS_CODE:";

    @Setter
    private UserDetailsService userDetailsService;

    @Setter
    private TokenProcessor tokenProcessor;


    @Override
    public Token grant(TokenRequest tokenRequest) {
        SmsTokenRequest accessTokenRequest = (SmsTokenRequest) tokenRequest;
        validSmsCode(accessTokenRequest);

        accessTokenRequest.eraseCredentials();

        UserDetails userDetails = userDetailsService.loadUserByUsername(accessTokenRequest.getMobile());

        // 验证通过后颁发token
        Token token = tokenProcessor.grantToken(accessTokenRequest, userDetails);

        return token;
    }

    private void validSmsCode(SmsTokenRequest accessTokenRequest) {
        try {
            String mobile = accessTokenRequest.getMobile();
            // todo 记录匹配错误次数，超过5次在清除缓存，避免穷举
            if (CaptchaUtil.match(CACHE_CODE_KEY_PREFIX + mobile, accessTokenRequest.getCode(), true)) {
                throw new BadCredentialsException("验证码错误");
            }
        } catch (Exception e) {
            if (e instanceof BadCredentialsException) {
                throw e;
            }
            log.error("验证短信验证码时出现异常", e);
            // 如果出现了其他异常，那么也认为是密码错误
            throw new BadCredentialsException("验证码错误");
        }
    }


    @Override
    public boolean support(TokenRequest tokenRequest) {
        return tokenRequest instanceof SmsTokenRequest
                && getGrantType().equals(tokenRequest.getGrantType());
    }

    @Override
    public String getGrantType() {
        return GRANT_TYPE;
    }

}
