/*
 * Copyright (C) 2007 The Guava Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.github.linkeer8802.octopus.core.eventbus;

import com.github.linkeer8802.octopus.core.exception.EventHandleException;
import com.google.common.base.MoreObjects;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;
import java.util.logging.Logger;

public class EventBusImpl implements EventBus {

  private static final Logger logger = Logger.getLogger(EventBusImpl.class.getName());

  private final String identifier;
  final Class<? extends Annotation> subscribeAnnotation;
  private final SubscriberExceptionHandler exceptionHandler;

  private final SubscriberRegistry subscribers = new SubscriberRegistry(this);
  private final Dispatcher dispatcher;

  /** Creates a new EventBus named "default". */
  public EventBusImpl() {
    this("default");
  }

  /**
   * Creates a new EventBus with the given {@code identifier}.
   *
   * @param identifier a brief name for this bus, for logging purposes. Should be a valid Java
   *     identifier.
   */
  public EventBusImpl(String identifier) {
    this(
        identifier,
        OnEvent.class,
        Dispatcher.immediate(),
        LoggingHandler.INSTANCE);
  }

  public EventBusImpl(String identifier, Class<? extends Annotation> subscribeAnnotation) {
    this(
            identifier,
            subscribeAnnotation,
            Dispatcher.immediate(),
            LoggingHandler.INSTANCE);
  }

  /**
   * Creates a new EventBus with the given {@link SubscriberExceptionHandler}.
   *
   * @param exceptionHandler Handler for subscriber exceptions.
   * @since 16.0
   */
  public EventBusImpl(SubscriberExceptionHandler exceptionHandler) {
    this(
        "default",
            OnEvent.class,
            Dispatcher.immediate(),
        exceptionHandler);
  }

  EventBusImpl(
      String identifier,
      Class<? extends Annotation> subscribeAnnotation,
      Dispatcher dispatcher,
      SubscriberExceptionHandler exceptionHandler) {
    this.identifier = Objects.requireNonNull(identifier);
    this.subscribeAnnotation = subscribeAnnotation;
    this.dispatcher = Objects.requireNonNull(dispatcher);
    this.exceptionHandler = Objects.requireNonNull(exceptionHandler);
  }

  /**
   * Returns the identifier for this event bus.
   *
   * @since 19.0
   */
  public final String identifier() {
    return identifier;
  }

  /** Handles the given exception thrown by a subscriber with the given context. */
  void handleSubscriberException(Throwable e, SubscriberExceptionContext context) {
    Objects.requireNonNull(e);
    Objects.requireNonNull(context);
    exceptionHandler.handleException(e, context);
  }

  /**
   * Registers all subscriber methods on {@code object} to receive events.
   *
   * @param object object whose subscriber methods should be registered.
   */
  public Collection<Subscriber> register(Object object) {
    return subscribers.register(object);
  }

  /**
   * Unregisters all subscriber methods on a registered {@code object}.
   *
   * @param object object whose subscriber methods should be unregistered.
   * @throws IllegalArgumentException if the object was not previously registered.
   */
  public void unregister(Object object) {
    subscribers.unregister(object);
  }

  /**
   * Posts an event to all registered subscribers. This method will return successfully after the
   * event has been posted to all subscribers, and regardless of any exceptions thrown by
   * subscribers.
   *
   * <p>If no subscribers have been subscribed for {@code event}'s class, and {@code event} is not
   * already a {@link DeadEvent}, it will be wrapped in a DeadEvent and reposted.
   *
   * @param event event to post.
   */
  public void publishEvent(Object event) {
    Iterator<Subscriber> eventSubscribers = subscribers.getSubscribers(event);

    if (eventSubscribers.hasNext()) {
      dispatcher.dispatch(event, eventSubscribers);
    } else if (!(event instanceof DeadEvent)) {
      // the event had no subscribers and was not itself a DeadEvent
      publishEvent(new DeadEvent(this, event));
    }
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this).addValue(identifier).toString();
  }

  /** Simple logging handler for subscriber exceptions. */
  static final class LoggingHandler implements SubscriberExceptionHandler {
    static final LoggingHandler INSTANCE = new LoggingHandler();

    @Override
    public void handleException(Throwable exception, SubscriberExceptionContext context) {
      throw new EventHandleException(message(context), exception);
    }

    private static String message(SubscriberExceptionContext context) {
      Method method = context.getSubscriberMethod();
      return "Exception thrown by subscriber method "
          + method.getName()
          + '('
          + method.getParameterTypes()[0].getName()
          + ')'
          + " on subscriber "
          + context.getSubscriber()
          + " when dispatching event: "
          + context.getEvent();
    }
  }
}
