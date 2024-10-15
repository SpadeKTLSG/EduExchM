package org.shop.gateway.common.exception;

/**
 * 垃圾数据异常(已使用或过期)
 */
public class TrashException extends BaseException {

    public TrashException() {
    }

    public TrashException(String msg) {
        super(msg);
    }

}
