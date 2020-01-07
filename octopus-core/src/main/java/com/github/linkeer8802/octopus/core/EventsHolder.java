package com.github.linkeer8802.octopus.core;

import java.util.ArrayList;
import java.util.List;

/**
 * 领域事件的ThreadLocal容器
 * @author weird
 */
public final class EventsHolder {

    private static final ThreadLocal<List<DomainEvent>> eventsHolder = new ThreadLocal<>();

    /**
     * 初始化eventsHolder
     */
    public static void init() {
        eventsHolder.set(new ArrayList<>());
    }

    /**
     * 清除eventsHolder
     */
    public static void clear() {
        eventsHolder.remove();
    }

    /**
     * 从eventsHolder获取所有{@link DomainEvent}
     * @return 获取所有的领域事件对象集合
     */
    public static List<DomainEvent> get() {
        return eventsHolder.get();
    }

    public static void addEvent(DomainEvent domainEvent) {
        if (eventsHolder.get() != null) {
            eventsHolder.get().add(domainEvent);
        }
    }
}
