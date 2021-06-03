package com.developcollect.web.security.dckit;

import com.developcollect.web.common.security.IdUserDetail;
import com.developcollect.web.common.security.SecurityDelegate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

public class DefaultSecurityDelegate implements SecurityDelegate {
    @Override
    public IdUserDetail getUserDetail() {
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();
        Object principal = authentication.getPrincipal();
        return (IdUserDetail) principal;
    }
}
