package org.shop.admin.config;

import com.github.yulichang.autoconfigure.consumer.MybatisPlusJoinPropertiesConsumer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MybatisPlusConfig {

    /**
     * MPJ配置: 关闭逻辑删除, 关闭输出横幅
     */
    @Bean
    public MybatisPlusJoinPropertiesConsumer mybatisPlusJoinPropertiesConsumer() {
        return prop -> prop
                .setBanner(false)
                .setSubTableLogic(false);
    }
}
