package org.shop.common.interceptor;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.shop.common.context.UserHolder;
import com.shop.common.exception.AccountNotFoundException;
import com.shop.common.exception.NotLoginException;
import com.shop.pojo.dto.UserLocalDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.shop.common.constant.MessageConstant.ACCOUNT_NOT_FOUND;
import static com.shop.common.constant.MessageConstant.USER_NOT_LOGIN;
import static com.shop.common.constant.RedisConstant.LOGIN_USER_KEY_GUEST;
import static com.shop.common.constant.RedisConstant.LOGIN_USER_TTL_GUEST;


/**
 * 用户端刷新token拦截器
 */
@Slf4j
public class GuestRefreshTokenInterceptor implements HandlerInterceptor {

    private final StringRedisTemplate stringRedisTemplate;

    public GuestRefreshTokenInterceptor(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {

        String token = request.getHeader("authorization");// 获取请求头中的token
        if (StrUtil.isBlank(token)) throw new NotLoginException(USER_NOT_LOGIN);

        if (token.startsWith("Bearer ")) { //去除Postman产生的Bearer前缀
            token = token.substring(7);
        }


        String key = LOGIN_USER_KEY_GUEST + token;     //基于TOKEN获取redis中的用户
        Map<Object, Object> userMap = stringRedisTemplate.opsForHash().entries(key);
        log.info("操作用户信息: " + userMap);

        if (userMap.isEmpty()) throw new AccountNotFoundException(ACCOUNT_NOT_FOUND);

        // 转为UserDTO, 保存用户信息到 ThreadLocal
        UserLocalDTO userLocalDTO = BeanUtil.fillBeanWithMap(userMap, new UserLocalDTO(), false);
        UserHolder.saveUser(userLocalDTO);

        stringRedisTemplate.expire(key, LOGIN_USER_TTL_GUEST, TimeUnit.MINUTES);      // 一次用户操作, 能够刷新token有效期

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        UserHolder.removeUser();    // 移除用户
    }
}
