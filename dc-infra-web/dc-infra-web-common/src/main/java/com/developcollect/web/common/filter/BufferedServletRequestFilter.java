package com.developcollect.web.common.filter;

import com.developcollect.extra.servlet.ServletUtil;
import com.developcollect.web.common.http.wrapper.MutableBufferedServletRequestWrapper;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Slf4j
public class BufferedServletRequestFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest requestRef = (HttpServletRequest) request;

        if (!"GET".equals(requestRef.getMethod())
                && !"HEAD".equals(requestRef.getMethod())
                && !ServletUtil.isMultipart(requestRef)) {
            try {
                requestRef = new MutableBufferedServletRequestWrapper((HttpServletRequest) request);
            } catch (Exception e) {
                log.warn("无法缓存请求体：{}", requestRef.getRequestURL());
            }
        }

        chain.doFilter(requestRef, response);
    }

    @Override
    public void destroy() {

    }
}
