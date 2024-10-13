package org.shop.admin.common.aspect;


import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.shop.admin.common.annotation.AutoFill;
import org.shop.admin.common.constant.AutoFillConstant;
import org.shop.admin.common.enumeration.OperationType;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.LocalDateTime;


/**
 * 公共字段自动填充处理自定义切面
 */
@Aspect
@Component
@Slf4j
public class AutoFillAspect {

    /**
     * 切入点
     */
    @Pointcut("execution(* org.shop.admin.service.*.*(..)) && @annotation(org.shop.admin.common.annotation.AutoFill)")
    public void autoFillPointCut() {
    }


    /**
     * 前置通知中进行公共字段赋值
     */
    @Before("autoFillPointCut()")
    public void autoFill(JoinPoint joinPoint) {

        log.info("进行公共字段自动填充");

        //获取到当前被拦截的方法上的数据库操作类型
        //反射处理
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();//方法签名对象
        AutoFill autoFill = signature.getMethod().getAnnotation(AutoFill.class);//获得方法上的注解对象
        OperationType operationType = autoFill.value();//获得数据库操作类型

        //获取到当前被拦截的方法的参数--实体对象
        Object[] args = joinPoint.getArgs();

        if (args == null || args.length == 0) {
            return;
        }

        Object entity = args[0];

        LocalDateTime now = LocalDateTime.now();//准备赋值的数据

        //根据当前不同的操作类型为对应的属性通过反射赋值
        if (operationType == OperationType.INSERT) {
            try {
                Method setCreateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.NOTICE_SET_PUBLISH_TIME, LocalDateTime.class);
                Method setUpdateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.NOTICE_SET_UPDATE_TIME, LocalDateTime.class);
                setCreateTime.invoke(entity, now);
                setUpdateTime.invoke(entity, now);

            } catch (Exception e) {
                log.error(e.getMessage());
            }

        } else if (operationType == OperationType.UPDATE) {
            try {
                Method setUpdateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.NOTICE_SET_UPDATE_TIME, LocalDateTime.class);
                setUpdateTime.invoke(entity, now);

            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
    }
}
