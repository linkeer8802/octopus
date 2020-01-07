package com.github.linkeer8802.octopus.core.exception;

/**
 * 事件处理异常
 * @author weird
 */
public class EventHandleException extends RuntimeException {

    public EventHandleException() {}

    public EventHandleException(String message) {
        super(message);
    }

    public EventHandleException(String message, Throwable e) {
        super(message, e);
    }
}
