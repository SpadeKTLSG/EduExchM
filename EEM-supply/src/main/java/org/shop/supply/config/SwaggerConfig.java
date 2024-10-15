package org.shop.supply.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger配置
 */
@Configuration
@ComponentScan(basePackages = "org.shop.supply.controller")
public class SwaggerConfig {


    @Bean
    public OpenAPI springOpenAPI() {
        return new OpenAPI()
                .openapi("3.0.0")
                .info(new Info().title("Supply API文档")
                        .contact(new Contact())
                        .description("物资端API文档")
                        .version("1.0.0"));

    }
}
