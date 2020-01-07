package com.github.linkeer8802.octopus.core;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

/**
 * 领域事件基类
 * @author wrd
 */
@JsonIgnoreProperties(value = {"target"}, ignoreUnknown = true)
public class DomainEvent {
    /**
     * 事件ID
     */
    String eventId;
    /**
     * 时间戳
     */
    private Long timestamp;
    /**
     * 聚合根版本
     */
    @SuppressWarnings("unused")
    private Long aggregateRootVersion;
    /**
     * 聚合根ID
     */
    @SuppressWarnings("unused")
    private Serializable aggregateRootId;
    /**
     * 聚合根类型名
     */
    @SuppressWarnings("unused")
    private String aggregateRootType;
    /**
     * 事件源{@link AggregateRoot}
     */
    transient AggregateRoot<? extends Serializable> target;

    public String getEventId() {
        return eventId;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    AggregateRoot<? extends Serializable> getTarget() {
        return target;
    }

    void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public Long getAggregateRootVersion() {
        return target instanceof EventSourcingAggregateRoot
                ? ((EventSourcingAggregateRoot) target).getVersion() : null;
    }

    public String getAggregateRootType() {
        return target == null ? null : target.getClass().getName();
    }

    @SuppressWarnings("unchecked")
    public <ID extends Serializable> ID getAggregateRootId() {
        return target == null ? null : (ID) target.getId();
    }
}
