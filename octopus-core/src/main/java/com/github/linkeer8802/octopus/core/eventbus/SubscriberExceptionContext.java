/*
 * Copyright (C) 2013 The Guava Authors
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

import java.lang.reflect.Method;
import java.util.Objects;

public class SubscriberExceptionContext {
  private final EventBusImpl eventBus;
  private final Object event;
  private final Object subscriber;
  private final Method subscriberMethod;

  SubscriberExceptionContext(
          EventBusImpl eventBus, Object event, Object subscriber, Method subscriberMethod) {
    this.eventBus = Objects.requireNonNull(eventBus);
    this.event = Objects.requireNonNull(event);
    this.subscriber = Objects.requireNonNull(subscriber);
    this.subscriberMethod = Objects.requireNonNull(subscriberMethod);
  }

  public EventBusImpl getEventBus() {
    return eventBus;
  }

  /** @return The event object that caused the subscriber to throw. */
  public Object getEvent() {
    return event;
  }

  /** @return The object context that the subscriber was called on. */
  public Object getSubscriber() {
    return subscriber;
  }

  /** @return The subscribed method that threw the exception. */
  public Method getSubscriberMethod() {
    return subscriberMethod;
  }
}
