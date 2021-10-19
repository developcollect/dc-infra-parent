package com.developcollect.web.security.oauth2.auth;

import org.springframework.security.core.userdetails.UserDetails;

public interface IdUserDetails<Id> extends UserDetails {

    Id getId();
}
