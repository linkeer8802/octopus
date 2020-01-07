package com.github.linkeer8802.octopus.core;

import com.github.linkeer8802.octopus.core.eventbus.EventBus;
import com.github.linkeer8802.octopus.core.eventbus.EventBusFactory;
import com.github.linkeer8802.octopus.core.util.Identifiers;

import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 基于事件溯源的聚合根实现类
 * @author wrd
 * @param <T> 聚合根类型
 * @param <ID> 聚合根ID类型
 */
public class EventSourcingAggregateRoot<T extends AggregateRoot<ID>, ID extends Serializable> implements AggregateRoot<ID> {

    private final EventBus eventBus;
    /**
     * 聚合根的ID
     */
    protected ID id;
    /**
     * 聚合根的版本号
     */
    protected AtomicLong version;

    static final Long INIT_VERSION = 0L;

    protected EventSourcingAggregateRoot() {
        this.version = new AtomicLong(INIT_VERSION);
        this.eventBus = EventBusFactory.get().create(getClass().getName());
        register(this);
    }

    @Override
    public ID getId() {
        return id;
    }

    public Long getVersion() {
        return version.get();
    }

    void setVersion(Long version) {
        this.version.set(version);
    }
    /**
     * 事件发布
     * @param event 待发布的事件
     * @return 聚合根this对象
     */
    protected EventSourcingAggregateRoot<T, ID> publishEvent(DomainEvent event) {
        event.target = this;
        event.eventId = Identifiers.uuid();
        event.setTimestamp(Instant.now().toEpochMilli());
        eventBus.publishEvent(event);
        return this;
    }

    /**
     * 事件发布
     * @param event 待发布的事件
     * @param clazz 聚合根类型
     */
    public static <T extends EventSourcingAggregateRoot<T, ID>, ID extends Serializable> void publishEvent(
            DomainEvent event, Class<T> clazz, ID aggregateRootId) {
        T aggregateRoot = AbstractAggregateRootFactory.createAggregateRoot(clazz);
        aggregateRoot.id = aggregateRootId;
        event.target = aggregateRoot;
        event.eventId = Identifiers.uuid();
        event.setTimestamp(Instant.now().toEpochMilli());
        aggregateRoot.publishEvent(event);
    }

    /**
     * 事件回放
     * @param event 待回放的事件
     * @return 聚合根this对象
     */
    EventSourcingAggregateRoot<T, ID> replayEvent(DomainEvent event) {
        eventBus.publishEvent(event);
        return this;
    }

    Long incrementVersion() {
        return this.version.incrementAndGet();
    }

    private void register(Object subscriber) {
        eventBus.register(subscriber);
    }

    void register(Object... subscribers) {
        List<Object> subscriberList = new ArrayList<>(Arrays.asList(subscribers));
        subscriberList.forEach(this::register);
    }

    @SuppressWarnings("unused")
    void unregister(Object subscriber) {
        eventBus.unregister(subscriber);
    }

    @Override
    public String toString() {
        return "EventSourcingAggregateRoot{" +
                "version=" + version +
                ", id=" + getId() +
                '}';
    }
}
