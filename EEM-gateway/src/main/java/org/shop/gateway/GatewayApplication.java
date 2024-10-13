package org.shop.gateway;


import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * 网关启动类
 */
@Slf4j
@SpringBootApplication
@ComponentScan(basePackages = "org.shop.gateway.*")
@EnableAspectJAutoProxy()
@EnableDiscoveryClient //开启服务发现
public class GatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }
}
