package com.developcollect.web.config;


import com.developcollect.web.utils.WebUtil;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DcWebAutoConfig {

    @Bean
    @ConditionalOnMissingBean
    WebUtil webUtil() {
        return new WebUtil();
    }
}
