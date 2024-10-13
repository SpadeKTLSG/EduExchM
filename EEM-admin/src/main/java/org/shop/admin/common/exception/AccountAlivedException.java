package org.shop.admin.common.exception;

/**
 * 账号已存在异常
 */
public class AccountAlivedException extends BaseException {

    public AccountAlivedException() {
    }

    public AccountAlivedException(String msg) {
        super(msg);
    }

}