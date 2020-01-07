package com.github.linkeer8802.octopus.core.message;

import java.util.function.Consumer;

/**
 * @author weird
 */
public interface CDCServer {

    void start();

    void addMessageListener(Consumer<Message> listener);
}
