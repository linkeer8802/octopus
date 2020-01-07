package com.github.linkeer8802.octopus.core;

import com.github.linkeer8802.octopus.core.eventbus.OnEvent;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.List;

/**
 * 持久化领域事件到数据库的AggregateRootFactory装饰器
 * @author weird
 * @see AggregateRootFactory
 * @see EventSourcingAggregateRoot
 */
@Slf4j
public class PersistentEventSupportFactoryDecorator<T extends EventSourcingAggregateRoot<T, ? extends Serializable>, M>
                                            implements AggregateRootFactory<T, M>, AggregateRootRepository<T> {

    private EventRepository eventRepository;
    private AggregateRootFactory<T, M> delegate;

    public PersistentEventSupportFactoryDecorator(AggregateRootFactory<T, M> aggregateRootFactory, EventRepository eventRepository) {
        this.delegate = aggregateRootFactory;
        this.eventRepository = eventRepository;
        //把自身添加到事件订阅器中
        getEventSubscribers().add(this);
    }

    @Override
    public T create(M model, Class<T> clz) {
        return delegate.create(model, clz);
    }

    @Override
    public <ID extends Serializable> AggregateRootContainer<T> load(ID id, Class<T> clz) {
        return delegate.load(id, clz);
    }

    @Override
    public List<Object> getEventSubscribers() {
        return delegate.getEventSubscribers();
    }

    @OnEvent
    @SuppressWarnings("unused")
    private void onDomainEvent(DomainEvent event) {
        if (event.getTarget() instanceof EventSourcingAggregateRoot) {
            eventRepository.save(event);
        }
    }
}
