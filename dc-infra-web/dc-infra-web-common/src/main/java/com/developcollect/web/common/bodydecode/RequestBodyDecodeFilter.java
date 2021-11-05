package com.developcollect.web.common.bodydecode;

import com.developcollect.core.web.common.R;
import com.developcollect.extra.servlet.ServletUtil;
import com.developcollect.web.common.http.MutableRequest;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 请求body解码过滤器
 */
public class RequestBodyDecodeFilter implements Filter {

    public static final String DECODE_TYPE_KEY = "_ENCODE_TYPE";

    private Map<String, BodyDecoder> decoderMap = new HashMap<>();

    public RequestBodyDecodeFilter(List<BodyDecoder> decoders) {
        if (decoders != null) {
            for (BodyDecoder decoder : decoders) {
                decoderMap.put(decoder.decodeType(), decoder);
            }
        }
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        if (servletRequest instanceof MutableRequest) {
            HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
            MutableRequest mutableRequest = (MutableRequest) servletRequest;
            String encodeType = getEncodeType(httpServletRequest);
            if (encodeType != null) {
                try {
                    BodyDecoder bodyDecoder = decoderMap.get(encodeType.toLowerCase());
                    if (bodyDecoder != null) {
                        bodyDecoder.decode(httpServletRequest, mutableRequest);
                    }
                } catch (Exception e) {
                    sendError((HttpServletResponse) servletResponse, e);
                    return;
                }
            }
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {

    }

    private String getEncodeType(HttpServletRequest request) {
        return ServletUtil.getHeader(request, DECODE_TYPE_KEY, StandardCharsets.UTF_8);
    }

    private void sendError(HttpServletResponse response, Exception e) {
        response.setStatus(408);
        ServletUtil.writeJson(response, R.build("408", "解码错误"));
    }
}
