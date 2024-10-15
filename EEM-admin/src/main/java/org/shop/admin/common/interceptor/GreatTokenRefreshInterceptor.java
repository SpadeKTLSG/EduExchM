package org.shop.admin.common.interceptor;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.shop.admin.common.constant.MessageConstant;
import org.shop.admin.common.constant.RedisConstant;
import org.shop.admin.common.context.EmployeeHolder;
import org.shop.admin.common.context.UserHolder;
import org.shop.admin.common.exception.BaseException;
import org.shop.admin.common.exception.NetWorkException;
import org.shop.admin.common.exception.SthNotFoundException;
import org.shop.admin.entity.dto.EmployeeLocalDTO;
import org.shop.admin.entity.remote.UserLocalDTO;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.security.auth.login.AccountNotFoundException;
import java.util.concurrent.TimeUnit;


@Slf4j
public class GreatTokenRefreshInterceptor implements HandlerInterceptor {


    private final StringRedisTemplate stringRedisTemplate;

    public GreatTokenRefreshInterceptor(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }


    @Override
    @SneakyThrows
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {

        boolean isAdmin = false; //是否是管理员对象

        // 注意, 已经上了网关, 这里不需要再次校验token, 只需要TL + 刷新token即可
        String saved_info = request.getHeader("saved_info");// 获取请求头中存储的TL
        if (StrUtil.isBlank(saved_info)) throw new NetWorkException(MessageConstant.USER_NOT_LOGIN);

        // 判断是管理员还是用户
        Object user = BeanUtil.toBean(saved_info, Object.class);
        if (user == null) throw new BaseException("用户存储信息转换失败");

        if (user instanceof UserLocalDTO) {
            log.debug("用户对象访问EEM-admin");
            // log: 正常用户不能访问管理员端, 但是这里先放开了
            //throw new AccountNotFoundException("用户对象访问EEM-admin");
        } else if (user instanceof EmployeeLocalDTO) {
            log.debug("管理员对象访问EEM-admin");
            isAdmin = true;
        } else {
            log.debug("未知对象访问EEM-admin");
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }

        String token = request.getHeader("authorization");
        if (StrUtil.isBlank(token)) throw new SthNotFoundException("对象访问EEM-admin, 但是token走丢了?!");
        if (token.startsWith("Bearer ")) { //去除Postman产生的Bearer前缀
            token = token.substring(7);
        }

        if (isAdmin) {
            return handleAdmin(token, (EmployeeLocalDTO) user);
        } else {
            return handleUser(token, (UserLocalDTO) user);
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
