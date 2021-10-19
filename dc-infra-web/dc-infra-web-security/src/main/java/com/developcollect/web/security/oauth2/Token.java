package com.developcollect.web.security.oauth2;

import lombok.Data;

/**
 * @author Zhu KaiXiao
 * @version 1.0
 * @date 2021/3/9 10:54
 */
@Data
public class Token {


    private String accessToken;
    private String refreshToken;

    private Long expires;
    private Long refreshExpires;
}
