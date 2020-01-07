package com.github.linkeer8802.octopus.core;

import com.github.linkeer8802.octopus.core.eventbus.OnEvent;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 基于内存缓存的聚合根的AggregateRootFactory装饰器，可实现脱离数据库的AggregateRootFactory实现，
 * 该AggregateRootFactory装饰器应仅用于单元测试中，用于测试聚合根的业务逻辑。
 * @author weird
 * @see AggregateRootFactory
 * @see EventSourcingAggregateRoot
 */
public class InMemoryFactoryDecorator<T extends EventSourcingAggregateRoot<T, ? extends Serializable>, U>
                                            implements AggregateRootFactory<T, U>, AggregateRootRepository<T>  {

    private Map<Object, List<DomainEvent>> eventContainer;
    private AbstractAggregateRootFactory<T, U> delegate;

    public InMemoryFactoryDecorator(AbstractAggregateRootFactory<T, U> aggregateRootFactory) {
        this.delegate = aggregateRootFactory;
        aggregateRootFactory.setUpdateVersionToRepository(false);
        aggregateRootFactory.setVersionConflictingCheck(false);
        this.eventContainer = new ConcurrentHashMap<>();
    }

    @Override
    public List<Object> getEventSubscribers() {
        return delegate.getEventSubscribers();
    }

    @Override
    public T create(U entity, Class<T> clz) {
        addThisToEventSubscriber();
        return delegate.create(entity, clz);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <ID extends Serializable> AggregateRootContainer<T> load(ID id, Class<T> clz) {
        List<DomainEvent> domainEvents = eventContainer.get(id);
        if (domainEvents == null || domainEvents.isEmpty()) {
            return null;
        }
        T aggregateRoot = AbstractAggregateRootFactory.createAggregateRoot(clz);
        domainEvents.forEach(event -> aggregateRoot.replayEvent(event).incrementVersion());
        addThisToEventSubscriber();
        aggregateRoot.register(getEventSubscribers().toArray());
        return new AggregateRootContainer<>(aggregateRoot);
    }

    private void addThisToEventSubscriber() {
        getEventSubscribers().add(this);
    }

    @SuppressWarnings("unused")
    @OnEvent(order = Integer.MAX_VALUE)
    private void onDomainEvent(DomainEvent event) {
        if (event instanceof DomainExceptionEvent) {
            return;
        }
        Object aggregateRootId = event.getAggregateRootId();
        if (eventContainer.containsKey(aggregateRootId)) {
            eventContainer.get(aggregateRootId).add(event);
        } else {
            List<DomainEvent> events = new CopyOnWriteArrayList<>();
            events.add(event);
            eventContainer.put(aggregateRootId, events);
        }
    }
}
