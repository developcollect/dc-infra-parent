package com.developcollect.web.security.oauth2;

import com.developcollect.web.security.oauth2.auth.JwtTokenAuthProvider;
import com.developcollect.web.security.oauth2.auth.TokenAuthenticationProcessingFilter;
import com.developcollect.web.security.oauth2.auth.TokenProcessor;
import com.developcollect.web.security.oauth2.refresh.RefreshTokenGranter;
import com.developcollect.web.security.oauth2.usernaem.UsernameTokenGranter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;


@Configuration
@Order
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder())
                .and()
                .authenticationProvider(jwtTokenAuthProvider());
    }

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        TokenAuthenticationProcessingFilter tokenAuthenticationProcessingFilter = new TokenAuthenticationProcessingFilter();
        tokenAuthenticationProcessingFilter.setAuthenticationManager(authenticationManager());
        tokenAuthenticationProcessingFilter.setAuthenticationEntryPoint(authenticationEntryPoint());

        // 禁用缓存
        httpSecurity.headers().cacheControl();

        httpSecurity
                .csrf().disable()
                // 基于token，所以不需要session
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                .authorizeRequests()
                // 对于获取token的rest api要允许匿名访问
                .antMatchers("/oauth/token", "/oauth/refresh").permitAll()
                // 除上面外的所有请求全部需要鉴权认证
                .anyRequest().authenticated();
        httpSecurity
                .addFilterBefore(tokenAuthenticationProcessingFilter, AbstractPreAuthenticatedProcessingFilter.class);

    }


    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Bean
    TokenProcessor tokenProcessor() {
        return new TokenProcessor("KNJIHQWESNXHHOIWHE98273NH98Y823H98HJKFAH8SDY1");
    }

    @Bean
    JwtTokenAuthProvider jwtTokenAuthProvider() {
        JwtTokenAuthProvider jwtTokenAuthProvider = new JwtTokenAuthProvider();
        jwtTokenAuthProvider.setTokenProcessor(tokenProcessor());
        return jwtTokenAuthProvider;
    }


    @Bean
    TokenGranter tokenGranter() {
        UsernameTokenGranter usernameTokenGranter = new UsernameTokenGranter();
        usernameTokenGranter.setPasswordEncoder(passwordEncoder());
        usernameTokenGranter.setTokenProcessor(tokenProcessor());
        usernameTokenGranter.setUserDetailsService(userDetailsService);

        RefreshTokenGranter refreshTokenGranter = new RefreshTokenGranter();
        refreshTokenGranter.setTokenProcessor(tokenProcessor());
        refreshTokenGranter.setUserDetailsService(userDetailsService);


        return new CompressTokenGranter(Arrays.asList(usernameTokenGranter, refreshTokenGranter));
    }

    @Bean
    AuthenticationEntryPoint authenticationEntryPoint() {
        return new AuthenticationEntryPoint() {
            @Override
            public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
                response.setStatus(401);
                response.setContentType("application/json;charset=utf8");
                response.getWriter().println("{\"code\": 401, \"message\": \"" + authException.getMessage() + "\"}");
            }
        };
    }
    // 通过Override其他方法实现对web安全的定制
}
