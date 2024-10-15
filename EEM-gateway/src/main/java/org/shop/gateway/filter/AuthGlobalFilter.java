package org.shop.gateway.filter;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.shop.gateway.common.constant.RedisConstant;
import org.shop.gateway.common.exception.BlockActionException;
import org.shop.gateway.common.exception.NetWorkException;
import org.shop.gateway.common.exception.NotLoginException;
import org.shop.gateway.entity.remote.EmployeeLocalDTO;
import org.shop.gateway.entity.remote.UserLocalDTO;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

import static org.apache.commons.lang3.ObjectUtils.isEmpty;

@Slf4j
@Component
@RequiredArgsConstructor
@EnableConfigurationProperties(AuthProperties.class)
public class AuthGlobalFilter implements GlobalFilter, Ordered {

    private final StringRedisTemplate stringRedisTemplate;
    private final AuthProperties authProperties;
    private final AntPathMatcher antPathMatcher = new AntPathMatcher();

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        //网关的过滤不做Redis刷新token操作, 只做token的校验 + TL的传递
        boolean isAdmin; //是否是管理员对象

        ServerHttpRequest request = exchange.getRequest();
        if (isExclude(request.getPath().toString())) { // 判断是否是白名单路径
            return chain.filter(exchange);
        }

        // 获取请求头中自定义token + (Postman相关处理)
        String token = null;
        List<String> headers = request.getHeaders().get("authorization");
        if (isEmpty(headers)) {
            throw new NotLoginException();
        }

        token = headers.get(0);
        if (StrUtil.isBlank(token)) throw new NotLoginException();

        try {
            if (token.startsWith("Bearer ")) { //去除Postman产生的Bearer前缀
                token = token.substring(7);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            ServerHttpResponse response = exchange.getResponse();
            response.setRawStatusCode(401);
            return response.setComplete();
        }

        //判断是用户还是管理员: 通过请求路径判断
        String path = request.getPath().toString();


        //基于TOKEN获取redis中的用户 -> 鉴别是管理还是用户
        String key_user = RedisConstant.LOGIN_USER_KEY_GUEST + token;
        String key_admin = RedisConstant.LOGIN_USER_KEY_ADMIN + token;
        Map<Object, Object> userMap_user = stringRedisTemplate.opsForHash().entries(key_user);
        Map<Object, Object> userMap_admin = stringRedisTemplate.opsForHash().entries(key_admin);
        Map<Object, Object> userMap;

        //哪个不为空, 就是哪个
        if (userMap_user.isEmpty() && !userMap_admin.isEmpty()) { //登陆者是管理员
            isAdmin = true;
            userMap = userMap_admin;
            log.info("操作管理员信息: " + userMap);
        } else {
            isAdmin = false;
            if (!userMap_user.isEmpty() && userMap_admin.isEmpty()) { //登陆者是用户
                userMap = userMap_user;
                log.info("操作用户信息: " + userMap);
            } else {
                log.error("基于TOKEN: {} 获取redis中的用户: {} 失败", token, JSONUtil.toJsonStr(userMap_user));
                throw new NetWorkException("网络异常, 请重新登陆");
            }
        }

        //基于身份权限识别鉴权
        if (path.contains("/admin") && !isAdmin) {
            throw new BlockActionException("用户不能访问管理员端");
        } else if (path.contains("/guest")) {
            throw new BlockActionException("管理员不能访问用户端");
        } else {
            log.debug("正在访问一个通用其他请求路径: " + path);
        }

        //传递在请求头的自定义用户信息, 之后在各个服务中可以通过请求头直接获取用户信息存TL (由于单体设计缺陷, 还需要存储用户类型)
        Long userId = Long.parseLong((String) userMap.get("id"));
        String saved_info = isAdmin ? JSONUtil.toJsonStr(new EmployeeLocalDTO(userId, null)) : JSONUtil.toJsonStr(new UserLocalDTO(userId, null));
        ServerWebExchange ex = exchange.mutate().request(a -> a.header("saved_info", saved_info).header("user_type", isAdmin ? "admin" : "guest"))
                .build();
        return chain.filter(ex);
    }

    private boolean isExclude(String antPath) {
        for (String pathPattern : authProperties.getExcludePaths()) {
            if (antPathMatcher.match(pathPattern, antPath)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
