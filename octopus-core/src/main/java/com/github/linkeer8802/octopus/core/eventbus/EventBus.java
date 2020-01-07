package com.github.linkeer8802.octopus.core.eventbus;

import java.util.Collection;

public interface EventBus {

    void publishEvent(Object event);

    Collection<Subscriber> register(Object subscriber);

    void unregister(Object subscriber);
}
