package org.shop;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 客户项目启动类
 */
@Slf4j
@SpringBootApplication
@EnableScheduling //开启定时任务
@EnableAspectJAutoProxy()
@EnableFeignClients(basePackages = "org.shop.client.guest", defaultConfiguration = FeignConfig.class)
public class GuestApplication {

    public static void main(String[] args) {
        SpringApplication.run(GuestApplication.class, args);
    }

}
