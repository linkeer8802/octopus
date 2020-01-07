package com.github.linkeer8802.octopus.example.customer.exception;

import com.github.linkeer8802.octopus.core.exception.DomainRuntimeException;

/**
 * @author weird
 * @date 2019/12/24
 */
public class CustomerValidationException extends DomainRuntimeException {
    private String orderId;

    private CustomerValidationException() {
        super(null);
    }

    public CustomerValidationException(String message) {
        super(message);
    }

    public CustomerValidationException(String message, String orderId) {
        super(message);
        this.orderId = orderId;
    }

    public String getOrderId() {
        return orderId;
    }
}
