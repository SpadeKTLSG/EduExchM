package org.shop.admin.config;


import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;


/**
 * 切面配置(废案)
 */
@Getter
@Deprecated
@Configuration
public class AspectConfig {

    @Value("${aspect.pointcut-expression-dateAutoFill}")
    private String pointcutExpression;

}
