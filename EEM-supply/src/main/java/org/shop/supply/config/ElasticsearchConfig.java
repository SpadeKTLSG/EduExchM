package org.shop.supply.config;


import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Elasticsearch配置类
 */
@Configuration
public class ElasticsearchConfig {

    @Value("${eduexch.es.host}")
    private String esHost;

    @Value("${eduexch.es.port}")
    private int esPort;

    @Value("${eduexch.es.scheme}")
    private String esScheme;

    @Bean
    public RestHighLevelClient restHighLevelClient() {
        return new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost(esHost, esPort, esScheme)
                )
        );
    }
}
