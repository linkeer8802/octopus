package com.github.linkeer8802.octopus.core.message.impl;

import com.github.linkeer8802.octopus.core.message.Message;

import java.util.Map;
import java.util.Optional;

/**
 * 消息实现类
 * @author weird
 */
public class MessageImpl implements Message {

    private Map<String, Object> headers;
    private Object payload;

    private MessageImpl() {}

    public MessageImpl(Map<String, Object> headers, Object payload) {
        this.headers = headers;
        this.payload = payload;
    }

    @Override
    public Map<String, Object> getHeaders() {
        return headers;
    }

    @Override
    public Object getPayload() {
        return payload;
    }

    @Override
    public Optional<Object> getHeader(String name) {
        return Optional.ofNullable(headers.get(name));
    }

    @Override
    public boolean hasHeader(String name) {
        return headers.containsKey(name);
    }

    @Override
    public void putHeaders(Map<String, Object> headers) {
        this.headers.putAll(headers);
    }

    @Override
    public void setHeader(String name, Object value) {
        headers.put(name, value);
    }

    @Override
    public void removeHeader(String name) {
        headers.remove(name);
    }
}
