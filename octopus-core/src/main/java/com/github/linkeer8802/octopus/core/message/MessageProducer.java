package com.github.linkeer8802.octopus.core.message;

import java.util.function.Consumer;

/**
 * 消息队列生产者接口
 * @author weird
 */
public interface MessageProducer {
    /**
     * 向消息队列发送消息
     * @param channel 消息通道
     * @param message 待发送的消息
     * @param onSuccess 成功回调
     * @param onError 错误回调
     */
    void send(String channel, Message message, Consumer<Object> onSuccess, Consumer<Throwable> onError);
}
