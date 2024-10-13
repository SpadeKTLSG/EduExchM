package org.shop.guest.config;

import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Redisson配置
 */
@Slf4j
@Configuration
public class RedissonConfig {


    @Value("${eduexch.redis.host}")
    private String host;

    @Value("${eduexch.redis.port}")
    private String port;

    @Value("${eduexch.redis.password}")
    private String password;


    @Bean
    public RedissonClient redissonClient() {
        log.debug("redissonClient配置完成");
        Config config = new Config();
        //单机模式
        config.useSingleServer().setAddress("redis://" + host + ":" + port).setPassword(password);
        return Redisson.create(config);
    }
}
