package org.shop.admin.common.interceptor;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.shop.admin.common.context.UserHolder;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 用户端登录拦截器
 *
 * @author SK
 * @date 2024/06/06
 */
public class GuestLoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (UserHolder.getUser() == null) {
            response.setStatus(401);
            return false;
        }
        return true;
    }
}
