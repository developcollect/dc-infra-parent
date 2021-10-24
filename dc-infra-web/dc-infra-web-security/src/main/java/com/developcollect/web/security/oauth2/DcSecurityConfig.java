package com.developcollect.web.security.oauth2;


import com.developcollect.core.web.common.R;
import com.developcollect.extra.servlet.ServletUtil;
import com.developcollect.web.security.oauth2.auth.JwtTokenAuthProvider;
import com.developcollect.web.security.oauth2.auth.TokenAuthenticationProcessingFilter;
import com.developcollect.web.security.oauth2.auth.TokenProcessor;
import com.developcollect.web.security.oauth2.refresh.RefreshTokenGranter;
import com.developcollect.web.security.oauth2.usernaem.UsernameTokenGranter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;

import java.util.Arrays;


/**
 * 不走自动配置，只是个配置示例
 */
public class DcSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private JwtTokenAuthProvider jwtTokenAuthProvider;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AuthenticationEntryPoint authenticationEntryPoint;
    @Autowired
    private AccessDeniedHandler accessDeniedHandler;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder)
                .and()
                .authenticationProvider(jwtTokenAuthProvider);
    }

//    @Override
//    public void configure(WebSecurity web) throws Exception {
//        web.ignoring()
//                .antMatchers(HttpMethod.POST, "/pc/members/register");
//    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // 使用自定义的过滤器，拦截Token
        TokenAuthenticationProcessingFilter tokenAuthenticationProcessingFilter = new TokenAuthenticationProcessingFilter();
        tokenAuthenticationProcessingFilter.setAuthenticationManager(authenticationManager());
        tokenAuthenticationProcessingFilter.setAuthenticationEntryPoint(authenticationEntryPoint);
        tokenAuthenticationProcessingFilter.setAuthenticationDetailsSource(new WebAuthenticationDetailsSource());

        http
                .csrf().disable()
                // 基于token，所以不需要session
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                .authorizeRequests()
                // 对于获取token的rest api要允许匿名访问
                .antMatchers("/oauth/token", "/oauth/refresh").permitAll()
                // 除上面外的所有请求全部需要鉴权认证
                .anyRequest().authenticated();

        // 加入自定义过滤器
        http
                .addFilterBefore(tokenAuthenticationProcessingFilter, AbstractPreAuthenticatedProcessingFilter.class);

        // 异常处理
        http.exceptionHandling()
                .authenticationEntryPoint(authenticationEntryPoint)
                .accessDeniedHandler(accessDeniedHandler);

    }


    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Bean
    TokenProcessor tokenProcessor(@Autowired(required = false) @Qualifier("dcSecurityTokenKey") String tokenKey) {
        if (tokenKey == null) {
            tokenKey = "ulxzsy0f6zhlfv3454sg3pwmcd42jm8x";
        }
        return new TokenProcessor(tokenKey);
    }

    @Bean
    JwtTokenAuthProvider jwtTokenAuthProvider(TokenProcessor tokenProcessor) {
        JwtTokenAuthProvider jwtTokenAuthProvider = new JwtTokenAuthProvider();
        jwtTokenAuthProvider.setTokenProcessor(tokenProcessor);
        return jwtTokenAuthProvider;
    }


    @Bean
    TokenGranter tokenGranter(TokenProcessor tokenProcessor) {
        UsernameTokenGranter usernameTokenGranter = new UsernameTokenGranter();
        usernameTokenGranter.setPasswordEncoder(passwordEncoder);
        usernameTokenGranter.setTokenProcessor(tokenProcessor);
        usernameTokenGranter.setUserDetailsService(userDetailsService);

        RefreshTokenGranter refreshTokenGranter = new RefreshTokenGranter();
        refreshTokenGranter.setTokenProcessor(tokenProcessor);
        refreshTokenGranter.setUserDetailsService(userDetailsService);

        return new CompressTokenGranter(Arrays.asList(usernameTokenGranter, refreshTokenGranter));
    }

    @Bean
    AuthenticationEntryPoint authenticationEntryPoint() {
        return (request, response, authException) -> {
            response.setStatus(401);
            ServletUtil.writeJson(response, R.build(401, authException.getMessage()));
        };
    }

    @Bean
    AccessDeniedHandler accessDeniedHandler() {
        return (request, response, accessDeniedException) -> {
            response.setStatus(403);
            ServletUtil.writeJson(response, R.build(403, accessDeniedException.getMessage()));
        };
    }

}

