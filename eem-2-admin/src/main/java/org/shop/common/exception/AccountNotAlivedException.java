package org.shop.common.exception;

/**
 * 账号不存在异常
 */
public class AccountNotAlivedException extends BaseException {

    public AccountNotAlivedException() {
    }

    public AccountNotAlivedException(String msg) {
        super(msg);
    }

}
