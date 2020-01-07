package com.github.linkeer8802.octopus.core.message.impl;

import com.github.linkeer8802.octopus.core.DomainEvent;
import com.github.linkeer8802.octopus.core.DomainExceptionEvent;
import com.github.linkeer8802.octopus.core.eventbus.EventBus;
import com.github.linkeer8802.octopus.core.eventbus.EventBusImpl;
import com.github.linkeer8802.octopus.core.eventbus.OnReceived;
import com.github.linkeer8802.octopus.core.eventbus.Subscriber;
import com.github.linkeer8802.octopus.core.message.Message;
import com.github.linkeer8802.octopus.core.message.MessageConsumer;
import com.github.linkeer8802.octopus.core.message.MessageSubscriber;
import com.github.linkeer8802.octopus.core.serializer.EventSerializer;
import com.github.linkeer8802.octopus.core.serializer.Serializer;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.Executor;

/**
 * 消息订阅器实现类
 * @author wrd
 */
public class MessageSubscriberImpl implements MessageSubscriber {

    private EventBus eventBus;
    private EventSerializer eventSerializer;
    private MessageConsumer messageConsumer;

//    private Executor messageHandlerExecutor;
    private Set<String> hasSubscribedChannels;
//    private Map<String, Executor> messageDispatchExecutors;

    public MessageSubscriberImpl(MessageConsumer messageConsumer, EventSerializer eventSerializer) {
        this.eventBus = new EventBusImpl("MessageSubscriberImpl EventBus", OnReceived.class);
        this.messageConsumer = messageConsumer;
        this.eventSerializer = eventSerializer;
        this.hasSubscribedChannels = new CopyOnWriteArraySet<>();
//        this.messageDispatchExecutors = new ConcurrentHashMap<>();
//        this.messageHandlerExecutor = Executors.newCachedThreadPool();
    }

    @Override
    public void subscribe(Object subscriber) {
        Collection<Subscriber> subscribers = eventBus.register(subscriber);
        getSubscriberChannels(subscribers).stream().forEach(channel -> {
            messageConsumer.handle(channel, (message, onSuccess, onError) -> {
                try {
                    doHandle(message);
                    onSuccess.accept(message);
                } catch (Exception e) {
                    onError.accept(e);
                }
            });
        });
    }

    private void doHandle(Message message) {
        Object payload = message.getPayload();
        DomainEvent domainEvent;
        Optional<Object> payloadType = message.getHeader(Message.PAYLOAD_TYPE);
        if (payloadType.isPresent() && payloadType.get().equals(Message.TYPE_JSON_PAYLOAD)) {
            domainEvent = eventSerializer.deserialize(payload.toString().getBytes(Serializer.CHARSET_UTF8));
        } else {
            domainEvent = (DomainEvent) payload;
        }

        if (payload instanceof DomainExceptionEvent) {
            eventBus.publishEvent(((DomainExceptionEvent) domainEvent).getException());
        } else {
            eventBus.publishEvent(domainEvent);
        }

//                Integer partitionId = null;
//                Optional<Object> headerPartitionId = message.getHeader(Message.PARTITION_ID);
//                if (headerPartitionId.isPresent()) {
//                    partitionId = (Integer) headerPartitionId.get();
//                }
//                Executor handlerExecutor = getHandlerExecutors(channel + ":" + partitionId);
//
//                handlerExecutor.execute(() -> eventBus.publishEvent(message.getPayload()));
    }

//    private Executor getHandlerExecutors(String key) {
//        Executor executor = messageDispatchExecutors.get(key);
//        if (executor == null) {
//            SwimlaneBasedMessageDispatchExecutor dispatchExecutor = new SwimlaneBasedMessageDispatchExecutor(messageHandlerExecutor);
//            Executor newExecutor = messageDispatchExecutors.putIfAbsent(key, dispatchExecutor);
//            if (newExecutor == null) {
//                executor = dispatchExecutor;
//            }
//        }
//        return executor;
//    }

    private Collection<String> getSubscriberChannels(Collection<Subscriber> subscribers) {
        Set<String> subscriberChannels = new HashSet<>();
        subscribers.forEach(subscriber -> {
            OnReceived annotation = getOnReceivedAnnotation(subscriber.annotation);
            if (hasSubscribedChannels.add(annotation.channel())) {
                subscriberChannels.add(annotation.channel());
            }
        });
        return subscriberChannels;
    }

    private OnReceived getOnReceivedAnnotation(Annotation annotation) {
        if (annotation instanceof OnReceived) {
            return (OnReceived) annotation;
        } else {
            throw new IllegalStateException(String.format("[annotation:%s]不是OnReceived的实例", annotation));
        }
    }
}
