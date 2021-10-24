package com.developcollect.web.ssm.autoconfig;

import com.developcollect.web.ssm.config.MybatisPlusPageMethodArgumentResolver;
import com.developcollect.web.ssm.export.ExportAspect;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Configuration
public class DcSsmAutoConfig {

    /**
     * 分页参数自动解析
     */
    @Bean
    WebMvcConfigurer ssmWebConfig() {
        return new WebMvcConfigurer() {
            @Override
            public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
                resolvers.add(new MybatisPlusPageMethodArgumentResolver());
            }
        };
    }

    @Bean
    ExportAspect exportAspect(HttpServletResponse response) {
        return new ExportAspect(response);
    }
}
