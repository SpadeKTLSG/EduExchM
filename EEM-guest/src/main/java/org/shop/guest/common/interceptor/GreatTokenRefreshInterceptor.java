package org.shop.guest.common.interceptor;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.shop.guest.common.constant.MessageConstant;
import org.shop.guest.common.constant.RedisConstant;
import org.shop.guest.common.context.EmployeeHolder;
import org.shop.guest.common.context.UserHolder;
import org.shop.guest.common.exception.BaseException;
import org.shop.guest.common.exception.NetWorkException;
import org.shop.guest.entity.dto.UserLocalDTO;
import org.shop.guest.entity.remote.EmployeeLocalDTO;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Objects;
import java.util.concurrent.TimeUnit;


@Slf4j
public class GreatTokenRefreshInterceptor implements HandlerInterceptor {


    private final StringRedisTemplate stringRedisTemplate;

    public GreatTokenRefreshInterceptor(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }


    @Override
    @SneakyThrows
    public boolean preHandle(HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler) {

        // 获取请求头中存储的TL, UT, token
        String saved_info = request.getHeader("saved_info");
        String user_type = request.getHeader("user_type");
        String token = request.getHeader("authorization");

        if (StrUtil.isBlank(saved_info) || StrUtil.isBlank(user_type) || StrUtil.isBlank(token)) throw new NetWorkException(MessageConstant.USER_NOT_LOGIN);

        boolean isAdmin = Objects.equals(user_type, "admin"); //是否是管理员对象

        if (token.startsWith("Bearer ")) { //去除Postman产生的Bearer前缀
            token = token.substring(7);
        }

        // 对象转换
        try {
            if (isAdmin) {
                log.debug("操作EEM-admin管理员: " + saved_info);
                EmployeeLocalDTO user = JSONUtil.toBean(saved_info, EmployeeLocalDTO.class);
                return handleAdmin(token, user);
            } else {
                log.debug("操作EEM-admin用户: " + saved_info);
                UserLocalDTO user = JSONUtil.toBean(saved_info, UserLocalDTO.class);
                return handleUser(token, user);
            }
        } catch (Exception e) {
            throw new BaseException("用户存储信息转换失败");
        }
    }


    @SneakyThrows
    private boolean handleUser(String token, UserLocalDTO user) {

        if (user == null) throw new BaseException("用户存储信息转换失败");
        log.debug("操作EEM-admin用户: " + JSONUtil.toJsonStr(user));

        //刷新token有效期
        String key = RedisConstant.LOGIN_USER_KEY_GUEST + token;
        stringRedisTemplate.expire(key, RedisConstant.LOGIN_USER_TTL_GUEST, TimeUnit.MINUTES);

        //保存TL
        UserHolder.saveUser(user);

        return true;
    }

    @SneakyThrows
    private boolean handleAdmin(String token, EmployeeLocalDTO user) {

        if (user == null) throw new BaseException("用户存储信息转换失败");
        log.debug("操作EEM-admin管理员: " + JSONUtil.toJsonStr(user));

        //刷新token有效期
        String key = RedisConstant.LOGIN_USER_KEY_ADMIN + token;
        stringRedisTemplate.expire(key, RedisConstant.LOGIN_USER_TTL_ADMIN, TimeUnit.MINUTES);

        //保存TL
        EmployeeHolder.saveEmployee(user);

        return true;
    }

    /**
     * 请求结束后移除管理员/用户信息
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        EmployeeHolder.removeEmployee();
        UserHolder.removeUser();
    }
}
