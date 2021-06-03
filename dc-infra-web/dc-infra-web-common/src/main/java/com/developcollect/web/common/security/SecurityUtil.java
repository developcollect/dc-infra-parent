package com.developcollect.web.common.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class SecurityUtil {

    private static SecurityDelegate delegate;

    @PostConstruct
    private void init(SecurityDelegate delegate) {
        SecurityUtil.delegate = delegate;
    }


    /**
     * 获取当前登录的用户名, 如果当前没有登录，则返回null
     */
    public static String getUsername() {
        return Optional.of(getUserDetail()).map(User::getUsername).orElse(null);
    }

    /**
     * 获取当前的用户id, 如果当前没有登录，则返回null
     */
    public static Long getUserId() {
        return Optional.of(getUserDetail()).map(IdUserDetail::getId).orElse(null);
    }

    /**
     * 获取当前的用户的角色
     * 以ROLE_为前缀的表示是角色
     */
    public static List<String> getRoles() {
        return Optional.of(getUserDetail())
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
        return Optional.of(getUserDetail())
                .map(user ->
                        user.getAuthorities().stream()
                                .map(GrantedAuthority::getAuthority)
                                .filter(authority -> !authority.startsWith("ROLE_"))
                                .collect(Collectors.toList())
                )
                .orElse(null);
    }


    public static IdUserDetail getUserDetail() {
        return getDelegate().getUserDetail();
    }

    protected static SecurityDelegate getDelegate() {
        return delegate;
    }
}
