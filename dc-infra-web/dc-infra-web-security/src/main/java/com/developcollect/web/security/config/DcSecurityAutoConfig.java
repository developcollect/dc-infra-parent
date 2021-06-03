package com.developcollect.web.security.config;


import com.developcollect.web.common.security.SecurityDelegate;
import com.developcollect.web.common.security.SecurityUtil;
import com.developcollect.web.security.dckit.DefaultSecurityDelegate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass(SecurityUtil.class)
public class DcSecurityAutoConfig {


    @Bean
    @ConditionalOnMissingBean
    public SecurityUtil securityUtil() {
        return new SecurityUtil();
    }

    @Bean
    @ConditionalOnMissingBean
    public SecurityDelegate securityDelegate() {
        return new DefaultSecurityDelegate();
    }

}
