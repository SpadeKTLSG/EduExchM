package org.shop.gateway.config;


import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class NacosConfig {

    @Value("${eduexch.nacos.host}")
    private String nacosHost;

    @Value("${eduexch.nacos.port}")
    private String nacosPort;

    @Bean
    public NacosDiscoveryProperties nacosDiscoveryProperties() {
        NacosDiscoveryProperties properties = new NacosDiscoveryProperties();
        properties.setServerAddr(nacosHost + ":" + nacosPort);
        return properties;
    }
}
