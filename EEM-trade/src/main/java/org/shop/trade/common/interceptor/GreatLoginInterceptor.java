package org.shop.trade.common.interceptor;

import cn.hutool.json.JSONUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.shop.trade.common.context.EmployeeHolder;
import org.shop.trade.common.context.UserHolder;
import org.shop.trade.entity.remote.EmployeeLocalDTO;
import org.shop.trade.entity.remote.UserLocalDTO;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Objects;

@Slf4j
public class GreatLoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler) {

        //通过OpenFeign调用, 请求头中的TL对象需要被取出
        String feign_info = request.getHeader("user-all-info");
        if (feign_info != null) {
            // 传递了OF, 则需要通过请求头中的TL对象, 重新设置TL
            String user_type = request.getHeader("user_type");
            //保存TL
            if (Objects.equals(user_type, "admin")) {
                log.debug("OpenFeign操作EEM-trade管理员: " + feign_info);
                EmployeeLocalDTO user = JSONUtil.toBean(feign_info, EmployeeLocalDTO.class);
                EmployeeHolder.saveEmployee(user);

            } else if (Objects.equals(user_type, "guest")) {
                log.debug("OpenFeign操作EEM-trade用户: " + feign_info);
                UserLocalDTO user = JSONUtil.toBean(feign_info, UserLocalDTO.class);
                UserHolder.saveUser(user);
            } else {
                log.error("user_type : {} 解析失败", user_type);
                response.setStatus(401);
                return false;
            }
        }

        // 同一操作者不能同时操作管理员端和用户端, 一次只有一种类型的TL
        if (EmployeeHolder.getEmployee() == null && UserHolder.getUser() == null) {
            response.setStatus(401);
            return false;
        }
        // 经过托底后有任一操作者TL内容, 放行
        return true;
    }
}
