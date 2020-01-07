package com.github.linkeer8802.octopus.spring.event;

import com.github.linkeer8802.octopus.core.DomainEvent;
import com.github.linkeer8802.octopus.core.EventsHolder;
import com.github.linkeer8802.octopus.core.message.Message;
import com.github.linkeer8802.octopus.core.message.MessageProducer;
import com.github.linkeer8802.octopus.core.message.impl.MessageImpl;
import com.github.linkeer8802.octopus.spring.listener.CallbackContext;
import com.github.linkeer8802.octopus.spring.listener.DomainServiceTransactionListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 领域事件发布监听器，在领域服务的方法执行成功后发布事件对象到消息队列中
 * @author weird
 */
@Slf4j
public class DomainEventPublishListener implements DomainServiceTransactionListener {

    private MessageProducer messageProducer;
    private RedisTemplate<String, String> redisTemplate;

    public DomainEventPublishListener(MessageProducer messageProducer, RedisTemplate<String, String> redisTemplate) {
        this.messageProducer = messageProducer;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void onBefore(CallbackContext context) {
        /**
         * 如果领域服务的方法以嵌套事务的方式执行，可能之前已经开启了事务，
         * 但未提交，此时EventsHolder已经初始化，不需要再执行初始化。
         */
        if (EventsHolder.get() == null) {
            EventsHolder.init();
        }
    }

    @Override
    public void onAfterCommit(CallbackContext context) {
        publishEvents(context);
    }

    @Override
    public void onAfterCompletion(CallbackContext context) {
        EventsHolder.clear();
    }

    @Override
    public void onAfter(CallbackContext context) {
        /**
         * 事务未激活，不会执行onAfterCommit方法，如果有事件产生，需重新发布事件
         */
        if (context.getThrowable() != null && !context.getTransactionActive()) {
            publishEvents(context);
            EventsHolder.clear();
        }
    }

    private void publishEvents(CallbackContext context) {
        List<DomainEvent> domainEvents = EventsHolder.get();
        domainEvents.forEach(domainEvent -> {

            String channel = domainEvent.getAggregateRootType();
            Map<String, Object> headers = new HashMap<>();
            headers.put(Message.CHANNEL, channel);
            String id = domainEvent.getEventId();
            headers.put(Message.ID, id);

            MessageImpl message = new MessageImpl(headers, domainEvent);
            messageProducer.send(channel, message, result ->  {
                try {
                    redisTemplate.opsForValue().set(id, "1");
                } catch (Exception e) {
                    log.error("", e);
                }
            }, ex -> {});
        });
    }
}
