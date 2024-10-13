package org.shop.supply.common.exception;

/**
 * 禁止操作异常
 */
public class BlockActionException extends BaseException {

    public BlockActionException() {
    }

    public BlockActionException(String msg) {
        super(msg);
    }

}
