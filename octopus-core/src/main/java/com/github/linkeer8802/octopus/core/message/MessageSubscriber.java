package com.github.linkeer8802.octopus.core.message;

/**
 * 消息订阅器
 * @author wrd
 */
public interface MessageSubscriber {
    /**
     * 指定的对象订阅消息
     * @param subscriber 待订阅消息的对象
     */
    void subscribe(Object subscriber);
}
