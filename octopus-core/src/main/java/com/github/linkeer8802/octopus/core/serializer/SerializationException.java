package com.github.linkeer8802.octopus.core.serializer;

/**
 * 序列化异常
 * @author wrd
 */
public class SerializationException extends RuntimeException {

    public SerializationException(String msg) {
        super(msg);
    }

    public SerializationException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
