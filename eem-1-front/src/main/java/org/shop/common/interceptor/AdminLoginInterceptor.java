package org.shop.common.interceptor;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.shop.common.context.EmployeeHolder;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 管理端登录拦截器
 */
public class AdminLoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (EmployeeHolder.getEmployee() == null) {// 判断是否需要拦截
            response.setStatus(401);
            return false;
        }
        return true;
    }
}
