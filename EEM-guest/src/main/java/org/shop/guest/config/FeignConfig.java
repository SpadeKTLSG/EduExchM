package org.shop.guest.config;

import cn.hutool.json.JSONUtil;
import feign.Logger;
import feign.RequestInterceptor;
import feign.okhttp.OkHttpClient;
import lombok.extern.slf4j.Slf4j;
import org.shop.guest.common.context.EmployeeHolder;
import org.shop.guest.common.context.UserHolder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Feign配置
 */
@Configuration
@Slf4j
public class FeignConfig {

    @Bean
    public OkHttpClient client() {
        return new OkHttpClient();
    }

    @Bean
    public Logger.Level logLevel() {
        return Logger.Level.BASIC;
    }

    /**
     * 传递all用户信息(管理/顾客)到下游微服务
     */
    @Bean
    public RequestInterceptor allHolderRequestInterceptor() {
        return template -> {
            // 获取登录用户(员工/顾客)信息
            String userAllInfo = null;
            if ((EmployeeHolder.getEmployee() == null || EmployeeHolder.getEmployee().getId() == null) && (UserHolder.getUser() == null || UserHolder.getUser().getId() == null)) {
                log.warn("哦哦, RPC请求头中没有任何家伙的Context信息...");
                return;
            }
            // 如果不为空则放入请求头中，传递给下游微服务
            // note: 同时仅仅存在一种用户信息: 管理员/顾客, 打成JSON字符串放入请求头中后面解析出来判断是哪种类型用户
            if (EmployeeHolder.getEmployee() != null && EmployeeHolder.getEmployee().getId() != null) {
                userAllInfo = JSONUtil.toJsonStr(EmployeeHolder.getEmployee());
            }
            if (UserHolder.getUser() != null && UserHolder.getUser().getId() != null) {
                userAllInfo = JSONUtil.toJsonStr(UserHolder.getUser());
            }

            template.header("user-all-info", userAllInfo);
        };
    }
}
