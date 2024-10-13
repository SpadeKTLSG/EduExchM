package org.shop.admin.common.annotation;


import org.shop.admin.common.enumeration.OperationType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自动填充注解
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AutoFill {


    /**
     * 数据库操作类型 -> UPDATE INSERT
     */
    OperationType value();
}
