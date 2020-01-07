package com.github.linkeer8802.octopus.core.message;

import java.util.Map;
import java.util.Optional;

/**
 * 用于发送到消息队列的消息接口
 * @author weird
 */
@SuppressWarnings("unused")
public interface Message {
    String ID = "ID";
    String CHANNEL = "CHANNEL";
    String PARTITION_ID = "PARTITION_ID";
    String  PAYLOAD_TYPE = "PAYLOAD_TYPE";

    int TYPE_JSON_PAYLOAD = 1;
    int TYPE_EVENT_PAYLOAD = 2;

    /**
     * 获取消息的数据body
     * @return 消息的数据body
     */
    Object getPayload();

    /**
     * 获取消息的头部集合
     * @return 消息的头部集合
     */
    Map<String, Object> getHeaders();

    /**
     * 获取指定名称的消息头部
     * @param name 名称
     * @return 指定名称的消息头部
     */
    Optional<Object> getHeader(String name);

    /**
     * 判断是否存在指定名称的消息头部
     * @param name 名称
     * @return 存在返回true，反之返回false
     */
    boolean hasHeader(String name);

    /**
     * 设置消息的头部集合
     * @param headers 消息的头部集合
     */
    void putHeaders(Map<String, Object> headers);

    /**
     * 设置指定名称的消息头部
     * @param name 名称
     * @param value 值
     */
    void setHeader(String name, Object value);

    /**
     * 移除指定名称的消息头部
     * @param name 名称
     */
    void removeHeader(String name);
}
