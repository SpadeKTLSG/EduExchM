package org.shop.trade.config;


import com.alibaba.druid.pool.DruidDataSource;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * Druid配置
 */
@Slf4j
@Configuration
public class DruidConfig {


    @PostConstruct
    public void setProperties() {   //解决druid 日志报错：discard long time none received connection:xxx
        System.setProperty("druid.mysql.usePingMethod", "false");
    }


    @ConfigurationProperties(prefix = "spring.datasource")
    @Bean
    public DataSource druidDataSource() { //自定义的 Druid数据源添加到容器
        return new DruidDataSource();
    }
}
