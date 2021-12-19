package com.developcollect.web.security.oauth2.auth;

import cn.hutool.jwt.JWT;
import com.developcollect.core.utils.DateUtil;
import org.junit.Test;

import java.util.Date;

public class JwtTokenProcessorTest {

    private static JwtTokenProcessor tokenProcessor = new JwtTokenProcessor("KNJIHQWESNXHHOIWHE98273NH98Y823H98HJKFAH8SDY1");

    @Test
    public void test_token() {
        JWT accessTokenJwt = JWT.create()
                .setExpiresAt(DateUtil.offsetSecond(new Date(), 3 * 60 * 60));
        Date exp = (Date) accessTokenJwt.getPayload("exp");
        System.out.println(exp.getTime() / 1000);
    }

}