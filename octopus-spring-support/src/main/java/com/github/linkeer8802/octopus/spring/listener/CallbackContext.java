package com.github.linkeer8802.octopus.spring.listener;

import lombok.Data;
import org.aspectj.lang.Signature;

/**
 * 回调上下文对象
 * @author weird
 */
@Data
public class CallbackContext {
    private Object target;
    private Object[] args;
    private Signature signature;
    private Object result;
    private Throwable throwable;
    private Boolean transactionActive;
    private int transactionStatus;
}
