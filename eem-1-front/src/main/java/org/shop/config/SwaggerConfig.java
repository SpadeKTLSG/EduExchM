package org.shop.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Guest的Swagger配置类
 */
@Configuration
@ComponentScan(basePackages = "com.shop.guest.controller")
public class SwaggerConfig {


    @Bean
    public OpenAPI springOpenAPI() {
        return new OpenAPI()
                .openapi("3.0.0")
                .info(new Info().title("Guest API文档")
                        .contact(new Contact())
                        .description("客户端API文档")
                        .version("1.0.0"));

    }
}
