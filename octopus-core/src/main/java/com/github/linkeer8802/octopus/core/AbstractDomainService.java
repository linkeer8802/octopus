package com.github.linkeer8802.octopus.core;

import com.github.linkeer8802.octopus.core.message.MessageSubscriber;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;

/**
 * 领域服务的抽象实现
 * @author weird
 */
public abstract class AbstractDomainService<T extends EventSourcingAggregateRoot<T, ? extends Serializable>, U> {

    private Class<T> aggregateRootType;
    private final AggregateRootFactory<T, U> delegateFactory;

    public AbstractDomainService(AggregateRootFactory<T, U> factory, Object... eventSubscribers) {
        this(factory, null, eventSubscribers);
    }

    public AbstractDomainService(AggregateRootFactory<T, U> factory, MessageSubscriber messageSubscriber, Object... eventSubscribers) {
        this.delegateFactory = factory;
        if (messageSubscriber != null) {
            messageSubscriber.subscribe(this);
        }
        List<Object> factoryEventSubscribers = factory.getEventSubscribers();
        factoryEventSubscribers.addAll(Arrays.asList(eventSubscribers));
        factoryEventSubscribers.add(this);
        aggregateRootType = getAggregateRootType(getClass());
    }

    protected T create(U entity) {
        return delegateFactory.create(entity, aggregateRootType);
    }

    protected AggregateRootContainer<T> load(Serializable id) {
        return delegateFactory.load(id, aggregateRootType);
    }

    @SuppressWarnings("unchecked")
    private Class<T> getAggregateRootType(final Class clazz) {
        Type genType = clazz.getGenericSuperclass();
        Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
        return (Class<T>) params[0];
    }
}
