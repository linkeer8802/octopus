package com.github.linkeer8802.octopus.example.transfer.infrastructure.common.model.exception;

/**
 * 余额不足异常
 */
public class BalanceNotEnoughException extends RuntimeException {

    public BalanceNotEnoughException() {}

    public BalanceNotEnoughException(String message) {
        super(message);
    }

    public BalanceNotEnoughException(String message, Throwable e) {
        super(message, e);
    }
}
