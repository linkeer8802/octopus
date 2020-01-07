package com.github.linkeer8802.octopus.core;

import lombok.ToString;

import java.io.Serializable;

/**
 * 领域对象实例创建事件
 * @author weird
 */
@ToString(callSuper = true)
public abstract class DomainCreatedEvent<ID extends Serializable> extends DomainEvent {
    /**
     * 获取ID
     * @return ID
     */
    public abstract ID getId();

    @Override
    @SuppressWarnings("unchecked")
    public ID getAggregateRootId() {
        return getId();
    }
}
