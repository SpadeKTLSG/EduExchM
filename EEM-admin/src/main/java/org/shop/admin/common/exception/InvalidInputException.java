package org.shop.admin.common.exception;

/**
 * 不合法输入异常
 */
public class InvalidInputException extends BaseException {

    public InvalidInputException() {
    }

    public InvalidInputException(String msg) {
        super(msg);
    }

}