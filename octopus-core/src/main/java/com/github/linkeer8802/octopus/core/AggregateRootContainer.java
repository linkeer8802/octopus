package com.github.linkeer8802.octopus.core;

import com.github.linkeer8802.octopus.core.exception.DomainRuntimeException;

import java.io.Serializable;

/**
 * 聚合根容器对象
 * @author weird
 */
public class AggregateRootContainer<T extends EventSourcingAggregateRoot<T, ? extends Serializable>> {
    T aggregateRoot;
    private Boolean isEmpty;

    public AggregateRootContainer(T aggregateRoot) {
        this.aggregateRoot = aggregateRoot;
        isEmpty = false;
    }

    public T get() {
        return isEmpty() ? null : aggregateRoot;
    }

    public Boolean isEmpty() {
        return isEmpty;
    }

    public static <T extends EventSourcingAggregateRoot<T, ? extends Serializable>> AggregateRootContainer<T> empty(T aggregateRoot) {
        AggregateRootContainer<T> result = new AggregateRootContainer<>(aggregateRoot);
        result.isEmpty = true;
        return result;
    }

    public void throwException(DomainRuntimeException exception) {
        aggregateRoot.publishEvent(new DomainExceptionEvent(exception));
    }
}
