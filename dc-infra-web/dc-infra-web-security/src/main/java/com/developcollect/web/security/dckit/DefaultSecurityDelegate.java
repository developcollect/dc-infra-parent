package com.developcollect.web.security.dckit;

import com.developcollect.web.common.security.DcSecurityUser;
import com.developcollect.web.common.security.SecurityDelegate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

public class DefaultSecurityDelegate implements SecurityDelegate {


    @Override
    public DcSecurityUser getUserDetail() {
        SecurityContext context = SecurityContextHolder.getContext();
        Object principal = Optional
                .ofNullable(context)
                .map(SecurityContext::getAuthentication)
                .map(Authentication::getPrincipal)
                .orElse(null);
        return (DcSecurityUser) principal;
    }

}
