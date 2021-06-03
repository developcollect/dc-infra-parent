package com.developcollect.spring;

import com.developcollect.spring.webmvc.DefaultGlobalExceptionHandler;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * @author Zhu KaiXiao
 * @version 1.0
 * @date 2021/3/12 10:28
 */
@Import({SpringUtil.class, WebUtil.class})
@Configuration
class DcSpringUtilsAutoConfig {


    @Bean
    @ConditionalOnMissingBean(name = "globalExceptionHandler")
    public DefaultGlobalExceptionHandler globalExceptionHandler() {
        return new DefaultGlobalExceptionHandler();
    }

    /**
     * 配置一个能够通过服务名调用接口的 RestTemplate
     * 注意：
     * 方法名不能变动，否则其他地方无法通过bean name获取bean
     */
    @Bean
    @LoadBalanced
    public RestTemplate loadBalancedRestTemplate() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        //单位为ms
        factory.setReadTimeout(600000);
        //单位为ms
        factory.setConnectTimeout(30000);
        //设置异步任务（线程不会重用，每次调用时都会重新启动一个新的线程）
        factory.setTaskExecutor(new SimpleAsyncTaskExecutor());
        RestTemplate restTemplate = new RestTemplate(factory);
        return restTemplate;
    }

}
