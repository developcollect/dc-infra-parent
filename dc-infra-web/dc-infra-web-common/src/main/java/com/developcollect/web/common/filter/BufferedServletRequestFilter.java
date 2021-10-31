package com.developcollect.web.common.filter;

import com.developcollect.web.common.http.wrapper.MutableBufferedServletRequestWrapper;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class BufferedServletRequestFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        ServletRequest requestRef = request;
        // 如果是application/json，则缓存
        if (support(request)) {
            requestRef = new MutableBufferedServletRequestWrapper((HttpServletRequest) request);
        }
        chain.doFilter(requestRef, response);
    }

    private boolean support(ServletRequest request) {
        String contentType = request.getContentType();
        if (contentType == null) {
            return false;
        }

        if (contentType.toLowerCase().startsWith("application/json")) {
            return true;
        }
        return false;
    }

    @Override
    public void destroy() {

    }
}
