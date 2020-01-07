package com.github.linkeer8802.octopus.core.serializer;

import com.github.linkeer8802.octopus.core.DomainEvent;

/**
 * 事件的Serializer
 * @author wrd
 */
public class EventSerializer implements Serializer<DomainEvent> {

    private final Serializer<Object> delegate;

    public EventSerializer(Serializer<Object> delegate) {
        this.delegate = delegate;
    }

    @Override
    public byte[] serialize(DomainEvent domainEvent) throws SerializationException {
        return delegate.serialize(domainEvent);
    }

    @Override
    public DomainEvent deserialize(byte[] bytes) throws SerializationException {
        return (DomainEvent) delegate.deserialize(bytes);
    }
}
