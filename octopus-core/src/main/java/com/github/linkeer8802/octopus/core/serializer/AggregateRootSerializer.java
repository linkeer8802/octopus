package com.github.linkeer8802.octopus.core.serializer;

import com.github.linkeer8802.octopus.core.AggregateRoot;

/**
 * 聚合根的Serializer
 * @author wrd
 */
public class AggregateRootSerializer implements Serializer<AggregateRoot> {

    private final Serializer<Object> delegate;

    public AggregateRootSerializer(Serializer<Object> delegate) {
        this.delegate = delegate;
    }

    @Override
    public byte[] serialize(AggregateRoot aggregateRoot) throws SerializationException {
        return delegate.serialize(aggregateRoot);
    }

    @Override
    public AggregateRoot deserialize(byte[] bytes) throws SerializationException {
        return (AggregateRoot) delegate.deserialize(bytes);
    }
}
