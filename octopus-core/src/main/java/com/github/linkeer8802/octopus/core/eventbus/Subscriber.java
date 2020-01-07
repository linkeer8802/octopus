/*
 * Copyright (C) 2014 The Guava Authors
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

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;

public class Subscriber {

  /** Creates a {@code Subscriber} for {@code method} on {@code listener}. */
  static Subscriber create(EventBusImpl bus, Object listener, Method method, Annotation annotation) {
    return new Subscriber(bus, listener, method, annotation);
  }

  /** The event bus this subscriber belongs to. */
  private EventBusImpl bus;

  /** The object with the subscriber method. */
  final Object target;

  /** Subscriber method. */
  final Method method;
  /**
   * modify at 2019-12-22 by linkeer8802
   */
  public final Annotation annotation;

  private Subscriber(EventBusImpl bus, Object target, Method method, Annotation annotation) {
    this.bus = bus;
    this.target = Objects.requireNonNull(target);
    this.method = method;
    this.annotation = annotation;
    method.setAccessible(true);
  }

  /** Dispatches {@code event} to this subscriber using the proper executor. */
  final void dispatchEvent(final Object event) {
      try {
        invokeSubscriberMethod(event);
      } catch (InvocationTargetException e) {
        bus.handleSubscriberException(e.getCause(), context(event));
      }
  }

  /**
   * Invokes the subscriber method. This method can be overridden to make the invocation
   * synchronized.
   */
  void invokeSubscriberMethod(Object event) throws InvocationTargetException {
    try {
      method.invoke(target, Objects.requireNonNull(event));
    } catch (IllegalArgumentException e) {
      throw new Error("Method rejected target/argument: " + event, e);
    } catch (IllegalAccessException e) {
      throw new Error("Method became inaccessible: " + event, e);
    } catch (InvocationTargetException e) {
      if (e.getCause() instanceof Error) {
        throw (Error) e.getCause();
      }
      throw e;
    }
  }

  /** Gets the context for the given event. */
  private SubscriberExceptionContext context(Object event) {
    return new SubscriberExceptionContext(bus, event, target, method);
  }

  @Override
  public final int hashCode() {
    return (31 + method.hashCode()) * 31 + System.identityHashCode(target);
  }

  @Override
  public final boolean equals(Object obj) {
    if (obj instanceof Subscriber) {
      Subscriber that = (Subscriber) obj;
      // Use == so that different equal instances will still receive events.
      // We only guard against the case that the same object is registered
      // multiple times
      return target == that.target && method.equals(that.method);
    }
    return false;
  }
}
