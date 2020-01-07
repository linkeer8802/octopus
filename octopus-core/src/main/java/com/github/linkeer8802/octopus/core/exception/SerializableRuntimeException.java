package com.github.linkeer8802.octopus.core.exception;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * 序列化友好的运行时异常
 * @author wrd
 */
@JsonIgnoreProperties({"cause", "stackTrace", "suppressed", "localizedMessage"})
public class SerializableRuntimeException extends RuntimeException {

    public SerializableRuntimeException(String message) {
        super(message, null);
    }
}
