package com.github.linkeer8802.octopus.core.exception;

/**
 * 领域业务运行时异常
 * @author wrd
 */
public class DomainRuntimeException extends SerializableRuntimeException {

    public DomainRuntimeException(String message) {
        super(message);
    }
}
