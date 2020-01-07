package com.github.linkeer8802.octopus.core.serializer;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.function.Consumer;

/**
 * Jackson2的JsonSerializer
 * @author wrd
 */
public class Jackson2JsonSerializer implements Serializer<Object> {

    private final ObjectMapper mapper;

    private static final byte[] EMPTY_ARRAY = new byte[0];

    private Jackson2JsonSerializer(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public byte[] serialize(Object t) throws SerializationException {
        if (t == null) {
            return EMPTY_ARRAY;
        }
        try {
            return mapper.writeValueAsBytes(t);
        } catch (JsonProcessingException e) {
            throw new SerializationException("Could not write JSON: " + e.getMessage(), e);
        }
    }

    @Override
    public Object deserialize(byte[] bytes) throws SerializationException {
        if (isEmpty(bytes)) {
            return null;
        }

        try {
            return mapper.readValue(bytes, Object.class);
        } catch (Exception ex) {
            throw new SerializationException("Could not read JSON: " + ex.getMessage(), ex);
        }
    }

    private static boolean isEmpty(byte[] data) {
        return (data == null || data.length == 0);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private ObjectMapper objectMapper;

        Builder() {
            this.objectMapper = new ObjectMapper();
            objectMapper.activateDefaultTyping(
                    objectMapper.getPolymorphicTypeValidator(),
                    ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);
        }

        public Builder mapperCustomize(Consumer<ObjectMapper> consumer) {
            consumer.accept(objectMapper);
            return this;
        }

        public Jackson2JsonSerializer build() {
            return new Jackson2JsonSerializer(objectMapper);
        }
    }
}
