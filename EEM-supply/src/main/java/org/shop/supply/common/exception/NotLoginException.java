package org.shop.common.exception;

/**
 * 用户未登录异常
 */
public class NotLoginException extends BaseException {

    public NotLoginException() {
    }

    public NotLoginException(String msg) {
        super(msg);
    }

}
