package com.developcollect.web.common.security;

/**
 * 委托
 */
public interface SecurityDelegate<T extends DcSecurityUser> {

    T getUserDetail();

}
