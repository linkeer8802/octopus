package com.github.linkeer8802.octopus.core.exception;

/**
 * 聚合根版本冲突异常
 * @author weird
 */
public class ConflictingAggregateVersionException extends RuntimeException {

    public ConflictingAggregateVersionException() {}

    public ConflictingAggregateVersionException(String message) {
        super(message);
    }

    public ConflictingAggregateVersionException(String message, Throwable e) {
        super(message, e);
    }
}
