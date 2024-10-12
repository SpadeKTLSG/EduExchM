package org.shop.common.handler;


import lombok.extern.slf4j.Slf4j;
import org.shop.common.exception.BaseException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


/**
 * 全局自定义异常处理
 */
@Slf4j
@RestControllerAdvice
public class WebExceptionAdvice {


    @ExceptionHandler(RuntimeException.class)
    public Result handleRuntimeException(RuntimeException e) {

        if (e instanceof BaseException) { // 自定义异常
            log.error("自定义异常 -> ", e);
            return Result.error(e.getMessage());
        }


        if (e.getMessage().contains("Duplicate entry")) { //SQL异常 : 主键重复
//            log.error("主键重复 -> ", e); //需要打印堆栈就打开这个
            return Result.error(OBJECT_HAS_ALIVE);
        }

        log.error(e.toString(), e);
        return Result.error(UNKNOWN_ERROR);
    }

}
