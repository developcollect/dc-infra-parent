package com.developcollect.web.common.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class SecurityUtil {

    private static final long SYSTEM_USER_ID = 1;
    private static final String SYSTEM_USER_NAME = "SYSTEM";

    private static SecurityDelegate delegate;

    @Autowired
    private void init(SecurityDelegate delegate) {
        SecurityUtil.delegate = delegate;
    }


    /**
     * 获取当前登录的用户名, 如果当前没有登录，则返回null
     */
    public static String getUsername() {
        return Optional.ofNullable(getUserDetail()).map(User::getUsername).orElse(null);
    }

    /**
     * 获取当前的用户id, 如果当前没有登录，则返回null
     */
    public static Long getUserId() {
        return Optional.ofNullable(getUserDetail()).map(IdUserDetail::getId).orElse(null);
    }

    /**
     * 获取当前登录的用户名
     * 如果当前没有登录，则返回系统用户名
     */
    public static String getUsernameOrSystem() {
        return Optional.ofNullable(getUserDetail()).map(User::getUsername).orElse(SYSTEM_USER_NAME);
    }

    /**
     * 获取当前登录的用户id
     * 如果当前没有登录，则返回系统用户id(1)
     */
    public static long getUserIdOrSystem() {
        return Optional.ofNullable(getUserDetail()).map(IdUserDetail::getId).orElse(SYSTEM_USER_ID);
    }

    /**
     * 获取当前的用户的角色
     * 以ROLE_为前缀的表示是角色
     * 注意：返回null才是没有登录，如果返回的是空集合，那表示当前用户没有权限
     */
    public static List<String> getRoles() {
        return Optional.ofNullable(getUserDetail())
                .map(user ->
                        user.getAuthorities().stream()
                                .map(GrantedAuthority::getAuthority)
                                .filter(authority -> authority.startsWith("ROLE_"))
                                .collect(Collectors.toList())
                )
                .orElse(null);
    }

    /**
     * 获取权限, 如果当前没有登录，则返回null
     * 注意：返回null才是没有登录，如果返回的是空集合，那表示当前用户没有权限
     */
    public static List<String> getAuthorities() {
        return Optional.ofNullable(getUserDetail())
                .map(user ->
                        user.getAuthorities().stream()
                                .map(GrantedAuthority::getAuthority)
                                .filter(authority -> !authority.startsWith("ROLE_"))
                                .collect(Collectors.toList())
                )
                .orElse(null);
    }


    public static IdUserDetail getUserDetail() {
        return Optional.ofNullable(getDelegate()).map(SecurityDelegate::getUserDetail).orElse(null);
    }

    protected static SecurityDelegate getDelegate() {
        return delegate;
    }
}
