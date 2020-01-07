package com.github.linkeer8802.octopus.core.message;

import java.util.function.Consumer;

/**
 * 消息处理器
 * @author weird
 */
public interface MessageHandler {
    /**
     * 消息处理函数
     * @param message
     * @param onSuccess
     * @param onError
     */
    void process(Message message, Consumer<Object> onSuccess, Consumer<Throwable> onError);
}
