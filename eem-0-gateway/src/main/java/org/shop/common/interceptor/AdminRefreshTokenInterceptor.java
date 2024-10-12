package org.shop.common.interceptor;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.shop.common.context.EmployeeHolder;
import org.shop.common.exception.NotLoginException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Map;
import java.util.concurrent.TimeUnit;


/**
 * 管理端刷新token拦截器
 */
@Slf4j
public class AdminRefreshTokenInterceptor implements HandlerInterceptor {

    private final StringRedisTemplate stringRedisTemplate;

    public AdminRefreshTokenInterceptor(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {

        String token = request.getHeader("authorization");// 获取请求头中的token
        if (StrUtil.isBlank(token)) throw new NotLoginException(USER_NOT_LOGIN);

        if (token.startsWith("Bearer ")) { //去除Postman产生的Bearer前缀
            token = token.substring(7);
        }


        String key = LOGIN_USER_KEY_ADMIN + token;     //基于TOKEN获取redis中的管理员
        Map<Object, Object> employeeMap = stringRedisTemplate.opsForHash().entries(key);
        log.info("操作管理员信息: " + employeeMap);

        if (employeeMap.isEmpty()) throw new AccountNotFoundException(ACCOUNT_NOT_FOUND);

        EmployeeLocalDTO employeeLocalDTO = BeanUtil.fillBeanWithMap(employeeMap, new EmployeeLocalDTO(), false);
        EmployeeHolder.saveEmployee(employeeLocalDTO);

        stringRedisTemplate.expire(key, LOGIN_USER_TTL_ADMIN, TimeUnit.MINUTES);      // 一次用户操作, 能够刷新token有效期

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        EmployeeHolder.removeEmployee(); // 移除管理员
    }
}
