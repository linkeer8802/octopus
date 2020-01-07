package com.github.linkeer8802.octopus.core.serializer;

import com.github.linkeer8802.octopus.core.message.Message;

/**
 * 事件消息的Serializer
 * @author wrd
 */
public class EventMessageSerializer implements Serializer<Message> {

    private final Serializer<Object> delegate;

    public EventMessageSerializer(Serializer<Object> delegate) {
        this.delegate = delegate;
    }

    @Override
    public byte[] serialize(Message message) throws SerializationException {
        return delegate.serialize(message);
    }

    @Override
    public Message deserialize(byte[] bytes) throws SerializationException {
        return (Message) delegate.deserialize(bytes);
    }
}
