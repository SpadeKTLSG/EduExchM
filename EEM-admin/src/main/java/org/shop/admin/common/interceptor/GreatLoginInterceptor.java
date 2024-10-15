package org.shop.admin.common.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.shop.admin.common.context.EmployeeHolder;
import org.springframework.web.servlet.HandlerInterceptor;

public class GreatLoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (EmployeeHolder.getEmployee() == null) {// 判断是否需要拦截
            response.setStatus(401);
            return false;
        }
        return true;
    }
}
