package com.github.linkeer8802.octopus.core.message;

/**
 * 消息队列的消费者接口
 * @author weird
 */
public interface MessageConsumer {
    /**
     * 处理指定channel的消息
     * @param channel 消息通道
     * @param handler 消息处理器
     */
    void handle(String channel, MessageHandler handler);
}
