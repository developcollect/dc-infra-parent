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
    private static final String CACHE_CODE_FAIL_COUNT_KEY_PREFIX = "DC_SECURITY_SMS_CODE_FAIL_COUNT:";

    @Setter
    private UserDetailsService userDetailsService;

    @Setter
    private TokenProcessor tokenProcessor;


    @Override
    public Token grant(TokenRequest tokenRequest) {
        String mobile = tokenRequest.getParameter("mobile");
        String code = tokenRequest.getParameter("code");
        validSmsCode(mobile, code);


        UserDetails userDetails = userDetailsService.loadUserByUsername(mobile);

        // 验证通过后颁发token
        Token token = tokenProcessor.grantToken(tokenRequest, userDetails);

        return token;
    }

    private void validSmsCode(String mobile, String code) {
        try {
            // 超过5次在清除缓存，避免穷举
            if (!CaptchaUtil.match(CACHE_CODE_KEY_PREFIX + mobile, code, 5, true)) {
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
    public String getGrantType() {
        return GRANT_TYPE;
    }

}
