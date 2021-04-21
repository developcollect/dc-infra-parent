package com.developcollect.authorization.social;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author zak
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SocialUserInfo {

    /**
     * 性别: 男
     */
    public static final int GENDER_MALE = 1;
    /**
     * 性别: 女
     */
    public static final int GENDER_FEMALE = 2;
    /**
     * 性别: 未知
     */
    public static final int GENDER_UNKNOWN = 3;

    /**
     * 第三方平台用户唯一id
     */
    private String uid;

    /**
     * 第三方平台
     */
    private SocialPlatform platform;

    /**
     * 第三方平台accessToken
     */
    private String accessToken;

    /**
     * 第三方平台accessToken有效期
     */
    private Integer accessTokenExpireIn;

    /**
     * 第三方平台refreshToken
     */
    private String refreshToken;

    /**
     * 第三方平台refreshToken有效期
     */
    private Integer refreshTokenExpireIn;


    /**
     * 第三方平台Appid
     */
    private String appid;

    /**
     * 第三方平台昵称
     */
    private String socialNickname;

    /**
     * 第三方平台性别
     * 1 男 2 女 3 未知
     */
    private Integer gender;

    /**
     * 第三方平台头像
     */
    private String headImg;


}
