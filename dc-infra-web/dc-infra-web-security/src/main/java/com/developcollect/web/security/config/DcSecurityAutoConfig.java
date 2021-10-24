package com.developcollect.web.security.config;


import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({DcSecurityUtilConfig.class})
public class DcSecurityAutoConfig {

}
