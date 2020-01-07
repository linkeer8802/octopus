package com.github.linkeer8802.octopus.core.eventbus;

public class EventBusFactory {

    private static EventBusFactory instance = new EventBusFactory();

    private EventBusFactory(){}

    public static EventBusFactory get() {
        return instance;
    }

    public EventBus create(String name) {
        return new EventBusImpl(name);
    }
}
