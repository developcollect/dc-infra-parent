package com.developcollect.web.common.filter;

import com.developcollect.web.common.filter.wrapper.BufferedServletRequestWrapper;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class BufferedServletRequestFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        chain.doFilter(new BufferedServletRequestWrapper((HttpServletRequest) request), response);
    }

    @Override
    public void destroy() {

    }
}
