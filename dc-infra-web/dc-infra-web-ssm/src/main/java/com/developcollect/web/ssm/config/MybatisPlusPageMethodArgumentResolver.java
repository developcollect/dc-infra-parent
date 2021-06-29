package com.developcollect.web.ssm.config;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDto;
import com.developcollect.core.utils.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


@Slf4j
public class MybatisPlusPageMethodArgumentResolver implements HandlerMethodArgumentResolver {
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        Class<?> parameterType = parameter.getParameterType();
        return IPage.class.isAssignableFrom(parameterType);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer modelAndViewContainer, NativeWebRequest nativeWebRequest, WebDataBinderFactory webDataBinderFactory) throws Exception {
        // 页码和页面大小
        int pageNo = resolvePageNo(nativeWebRequest);
        int pageSize = resolvePageSize(nativeWebRequest);
        List<OrderItem> orderItems = resolveSort(nativeWebRequest);
        PageDto<Object> page = new PageDto<>(pageNo, pageSize);
        page.setOrders(orderItems);
        return page;
    }


    private int resolvePageNo(NativeWebRequest webRequest) {
        String pageNoParam = webRequest.getParameter("pageNo");
        if (StrUtil.isNotBlank(pageNoParam)) {
            try {
                int pageNo = Integer.parseInt(pageNoParam);
                return pageNo <= 0 ? 1 : pageNo;
            } catch (NumberFormatException ignore) {
            }
        }
        return 1;
    }

    private int resolvePageSize(NativeWebRequest webRequest) {
        String pageSizeParam = webRequest.getParameter("pageSize");
        if (StrUtil.isNotBlank(pageSizeParam)) {
            try {
                int pageSize = Integer.parseInt(pageSizeParam);
                return pageSize <= 0 ? 10 : pageSize;
            } catch (NumberFormatException ignore) {
            }
        }
        return 10;
    }

    private List<OrderItem> resolveSort(NativeWebRequest webRequest) {
        String pageSortParams = webRequest.getParameter("pageSort");
        if (StrUtil.isNotBlank(pageSortParams)) {
            try {
                String[] sortStrArr = pageSortParams.split(",");
                List<OrderItem> orders = new ArrayList<>(sortStrArr.length);
                for (String sortParam : sortStrArr) {
                    // column and order
                    String[] co = sortParam.trim().split(" +");
                    if (co.length != 2) {
                        throw new IllegalArgumentException("分页排序参数解析失败：" + sortParam);
                    }
                    OrderItem orderItem = new OrderItem(co[0], "asc".equals(co[1].toLowerCase()));
                    orders.add(orderItem);
                }
                return orders;
            } catch (Exception e) {
                log.warn("分页参数自动绑定时解析排序参数失败", e);
            }
        }

        return Collections.emptyList();
    }
}
