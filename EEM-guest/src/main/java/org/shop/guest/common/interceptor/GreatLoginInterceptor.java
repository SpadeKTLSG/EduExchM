package org.shop.guest.common.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.shop.guest.common.context.EmployeeHolder;
import org.shop.guest.common.context.UserHolder;
import org.springframework.web.servlet.HandlerInterceptor;

public class GreatLoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 同一操作者不能同时操作管理员端和用户端, 一次只有一种类型的TL
        if (EmployeeHolder.getEmployee() == null && UserHolder.getUser() == null) {
            response.setStatus(401);
            return false;
        }
        // 有任一操作者TL内容, 放行
        return true;
    }
}
