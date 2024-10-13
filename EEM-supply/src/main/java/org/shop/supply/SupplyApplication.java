package org.shop.supply;


import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.shop.supply.config.FeignConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 物资项目启动类
 */
@Slf4j
@SpringBootApplication
@ComponentScan(basePackages = "org.shop.supply.*") //扫描组件
@MapperScan("org.shop.supply.mapper") //扫描Mapper接口
@EnableScheduling //开启定时任务
@EnableAspectJAutoProxy()
@EnableDiscoveryClient //开启服务发现
@EnableFeignClients(basePackages = "org.shop.supply.client", defaultConfiguration = FeignConfig.class) //开启Feign客户端
public class SupplyApplication {

    public static void main(String[] args) {
        SpringApplication.run(SupplyApplication.class, args);
    }

}
