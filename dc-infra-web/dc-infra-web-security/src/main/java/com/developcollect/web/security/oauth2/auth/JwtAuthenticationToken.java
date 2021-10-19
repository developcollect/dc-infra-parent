package com.developcollect.web.security.oauth2.auth;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.SpringSecurityCoreVersion;

import java.util.Collection;

/**
 * @author Zhu KaiXiao
 * @version 1.0
 * @date 2021/3/9 15:48
 */
public class JwtAuthenticationToken extends AbstractAuthenticationToken {
    private static final long serialVersionUID = SpringSecurityCoreVersion.SERIAL_VERSION_UID;

    private final Object principal;

    private final Object credentials;

    /**
     * Constructor used for an authentication request. The
     * {@link org.springframework.security.core.Authentication#isAuthenticated()} will
     * return <code>false</code>.
     *
     * @param aPrincipal   The pre-authenticated principal
     * @param aCredentials The pre-authenticated credentials
     */
    public JwtAuthenticationToken(Object aPrincipal, Object aCredentials) {
        super(null);
        this.principal = aPrincipal;
        this.credentials = aCredentials;
    }

    /**
     * Constructor used for an authentication response. The
     * {@link org.springframework.security.core.Authentication#isAuthenticated()} will
     * return <code>true</code>.
     *
     * @param aPrincipal    The authenticated principal
     * @param anAuthorities The granted authorities
     */
    public JwtAuthenticationToken(Object aPrincipal, Object aCredentials,
                                  Collection<? extends GrantedAuthority> anAuthorities) {
        super(anAuthorities);
        this.principal = aPrincipal;
        this.credentials = aCredentials;
        setAuthenticated(true);
    }

    /**
     * Get the credentials
     */
    @Override
    public Object getCredentials() {
        return this.credentials;
    }

    /**
     * Get the principal
     */
    @Override
    public Object getPrincipal() {
        return this.principal;
    }
}
