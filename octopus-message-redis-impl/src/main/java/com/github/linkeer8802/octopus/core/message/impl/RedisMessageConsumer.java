package com.github.linkeer8802.octopus.core.message.impl;

import com.github.linkeer8802.octopus.core.message.Message;
import com.github.linkeer8802.octopus.core.message.MessageConsumer;
import com.github.linkeer8802.octopus.core.message.MessageHandler;
import com.github.linkeer8802.octopus.core.serializer.EventMessageSerializer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

/**
 * @author weird
 */
@Slf4j
public class RedisMessageConsumer implements MessageConsumer {

    private EventMessageSerializer serializer;
    private RedisMessageListenerContainer redisMessageListenerContainer;

    public RedisMessageConsumer(RedisMessageListenerContainer redisMessageListenerContainer, EventMessageSerializer serializer) {
        this.serializer = serializer;
        this.redisMessageListenerContainer = redisMessageListenerContainer;
    }

    @Override
    public void handle(String channel, MessageHandler handler) {
        this.redisMessageListenerContainer.addMessageListener((msg, pattern) -> {
            Message message = serializer.deserialize(msg.getBody());
            handler.process(message, result-> {}, ex -> log.error("消息处理异常：", ex));
        }, new ChannelTopic(channel));
    }
}
