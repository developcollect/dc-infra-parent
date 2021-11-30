package com.developcollect.web.common.filter;

import com.developcollect.web.common.http.wrapper.MutableBufferedServletResponseWrapper;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
public class BufferedServletResponseFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        MutableBufferedServletResponseWrapper mutableBufferedServletResponseWrapper = new MutableBufferedServletResponseWrapper(httpServletResponse);
        chain.doFilter(request, mutableBufferedServletResponseWrapper);
        mutableBufferedServletResponseWrapper.processOutbound();
    }

    @Override
    public void destroy() {

    }
}
