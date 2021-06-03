//package com.developcollect.web.security.config;
//
//
//import com.alibaba.fastjson.JSON;
//import com.developcollect.core.web.common.R;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.http.HttpMethod;
//import org.springframework.security.authentication.AuthenticationProvider;
//import org.springframework.security.authentication.BadCredentialsException;
//import org.springframework.security.authentication.DisabledException;
//import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
//import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.builders.WebSecurity;
//import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//
//import javax.servlet.http.HttpServletResponse;
//
//import java.io.PrintWriter;
//
//public class SecurityConfig extends WebSecurityConfigurerAdapter {
//
//    @Override
//    public void configure(WebSecurity web) throws Exception {
//        web.ignoring()
//                .antMatchers(HttpMethod.POST, "/pc/members/register");
//    }
//
//    @Override
//    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//        auth.userDetailsService(userDetailsService());
//    }
//
//    @Override
//    // @formatter:off
//    protected void configure(HttpSecurity http) throws Exception {
//        http
////                .authenticationProvider(authenticationProvider())
//                .httpBasic()
//                .and()
//                .authorizeRequests()
//                .anyRequest().authenticated() //必须授权才能范围
//                .and()
//                .formLogin() //使用自带的登录
//                .permitAll()
//                //登录失败，返回json
//                .failureHandler((request,response,ex) -> {
//                    response.setContentType("application/json;charset=utf-8");
//                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//                    PrintWriter out = response.getWriter();
//                    R<Object> r = R.build(401, "");
//                    if (ex instanceof UsernameNotFoundException || ex instanceof BadCredentialsException) {
//                        r.setMessage("用户名或密码错误");
//                    } else if (ex instanceof DisabledException) {
//                        r.setMessage("账户被禁用");
//                    } else {
//                        r.setMessage("登录失败!");
//                    }
//                    out.write(JSON.toJSONString(r));
//                    out.flush();
//                    out.close();
//                })
//                //登录成功，返回json
//                .successHandler((request,response,authentication) -> {
//                    response.setContentType("application/json;charset=utf-8");
//                    PrintWriter out = response.getWriter();
//                    out.write(JSON.toJSONString(R.build(200, "登录成功", authentication)));
//                    out.flush();
//                    out.close();
//                })
//                .and()
//                .exceptionHandling()
//                //没有权限，返回json
//                .accessDeniedHandler((request,response,ex) -> {
//                    response.setContentType("application/json;charset=utf-8");
//                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
//                    PrintWriter out = response.getWriter();
//                    out.write(JSON.toJSONString(R.build(403, "权限不足")));
//                    out.flush();
//                    out.close();
//                })
//                .and()
//                .logout()
//                //退出成功，返回json
//                .logoutSuccessHandler((request,response,authentication) -> {
//                    response.setContentType("application/json;charset=utf-8");
//                    PrintWriter out = response.getWriter();
//                    out.write(JSON.toJSONString(R.build(200, "退出成功", authentication)));
//                    out.flush();
//                    out.close();
//                })
//                .permitAll()
//                .and()
//                .exceptionHandling()
//                //未登录时，进行json格式的提示
//                .authenticationEntryPoint((request,response,authException) -> {
//                    response.setContentType("application/json;charset=utf-8");
//                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
//                    PrintWriter out = response.getWriter();
//                    out.write(JSON.toJSONString(R.build(403, "未登录")));
//                    out.flush();
//                    out.close();
//                });
//
//
//        http.cors().disable();
//        http.csrf().disable();
//    }
//
//    @Bean
//    public AuthenticationProvider authenticationProvider() {
//        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
//        //对默认的UserDetailsService进行覆盖
//        authenticationProvider.setUserDetailsService(userDetailsService());
//        authenticationProvider.setPasswordEncoder(passwordEncoder());
//        return authenticationProvider;
//    }
//    // @formatter:on
//
//    // @formatter:off
//    @Bean
//    @Override
//    public UserDetailsService userDetailsService() {
//        return new UserDetailServiceImpl();
//    }
//    // @formatter:on
//
//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        // 设置默认的加密方式（强hash方式加密）
//        return new BCryptPasswordEncoder();
//    }
//}
