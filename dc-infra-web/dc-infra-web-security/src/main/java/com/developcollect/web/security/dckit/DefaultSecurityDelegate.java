package com.developcollect.web.security.dckit;

import com.developcollect.web.common.security.DcSecurityUser;
import com.developcollect.web.common.security.SecurityDelegate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;

import java.util.Optional;

@Slf4j
public class DefaultSecurityDelegate implements SecurityDelegate {


    @Override
    public DcSecurityUser getUserDetail() {
        SecurityContext context = SecurityContextHolder.getContext();
        if (context == null) {
            return null;
        }
        Authentication authentication = context.getAuthentication();
        Object principal = authentication.getPrincipal();
        if (principal instanceof DcSecurityUser) {
            return (DcSecurityUser) principal;
        } else {
            log.debug("Authentication：[{}]", authentication);
        }

        return null;
    }

}
