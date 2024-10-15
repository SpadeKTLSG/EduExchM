package org.shop.gateway.filter;

import cn.hutool.json.JSONUtil;
import lombok.RequiredArgsConstructor;
import org.shop.gateway.entity.remote.EmployeeLocalDTO;
import org.shop.gateway.entity.remote.UserLocalDTO;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;


@Component
@RequiredArgsConstructor
@EnableConfigurationProperties(AuthProperties.class)
public class AuthGlobalFilter implements GlobalFilter, Ordered {


    private final AntPathMatcher antPathMatcher = new AntPathMatcher();

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        boolean isAdmin = false; //是否是管理员类型请求


        // 1.获取Request
        ServerHttpRequest request = exchange.getRequest();
        // 2.判断是否不需要拦截
        if (isExclude(request.getPath().toString())) {
            // 无需拦截，直接放行
            return chain.filter(exchange);
        }
        // 3.获取请求头中的token
        String token = null;
        List<String> headers = request.getHeaders().get("authorization");

        if (!CollUtils.isEmpty(headers)) {
            token = headers.get(0);
        }

        //判断是用户还是管理员: 通过请求路径判断
        String path = request.getPath().toString();
        if (path.contains("/admin")) {
            //管理员
            token = headers.get(0);
        } else {
            //用户
            token = headers.get(0);
        }

        // 4.校验并解析token
        Long userId = null;
        try {
            userId = jwtTool.parseToken(token);
        } catch (UnauthorizedException e) {
            // 如果无效，拦截
            ServerHttpResponse response = exchange.getResponse();
            response.setRawStatusCode(401);
            return response.setComplete();
        }

        // TODO 5.如果有效，传递用户信息
        //传递在请求头的自定义用户信息
        String saved_info = isAdmin ? JSONUtil.toJsonStr(new EmployeeLocalDTO(userId, null)) : JSONUtil.toJsonStr(new UserLocalDTO(userId, null));
        ServerWebExchange ex = exchange.mutate().request(a -> a.header("saved_info", saved_info)).build();

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
