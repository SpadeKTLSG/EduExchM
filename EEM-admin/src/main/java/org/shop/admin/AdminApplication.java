package org.shop.admin;


import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.shop.admin.config.FeignConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;

/**
 * 管理项目启动类
 */
@Slf4j
@SpringBootApplication
@ComponentScan(basePackages = "org.shop.admin.*") //扫描组件
@MapperScan("org.shop.admin.mapper") //扫描Mapper接口
@EnableScheduling //开启定时任务
@EnableAspectJAutoProxy()
@EnableDiscoveryClient //开启服务发现
@EnableFeignClients(basePackages = "org.shop.admin.client", defaultConfiguration = FeignConfig.class) //开启Feign客户端
public class AdminApplication {

    public static void main(String[] args) {
        SpringApplication.run(AdminApplication.class, args);
    }

    @Bean
    @LoadBalanced
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
