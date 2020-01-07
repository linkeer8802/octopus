package com.github.linkeer8802.octopus.core.message.impl;

import com.github.linkeer8802.octopus.core.message.Message;
import com.github.linkeer8802.octopus.core.message.MessageProducer;
import com.github.linkeer8802.octopus.core.serializer.EventMessageSerializer;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.function.Consumer;

/**
 * @author weird
 * @date 2019/12/20
 */
public class RedisMessageProducer implements MessageProducer {

    private EventMessageSerializer serializer;
    private RedisTemplate<?, ?> messageRedisTemplate;

    public RedisMessageProducer(RedisTemplate<?, ?> messageRedisTemplate, EventMessageSerializer serializer) {
        this.serializer = serializer;
        this.messageRedisTemplate = messageRedisTemplate;
    }

    @Override
    public void send(String channel, Message message, Consumer<Object> onSuccess, Consumer<Throwable> onError) {
        byte[] rawChannel = messageRedisTemplate.getStringSerializer().serialize(channel);
        byte[] rawMessage = serializer.serialize(message);
        messageRedisTemplate.execute(connection -> {
            connection.publish(rawChannel, rawMessage);
            return null;
        }, true);
    }
}
