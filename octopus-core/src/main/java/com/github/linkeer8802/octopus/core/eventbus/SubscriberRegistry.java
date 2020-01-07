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

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.common.base.Throwables;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.*;
import com.google.common.reflect.TypeToken;
import com.google.common.util.concurrent.UncheckedExecutionException;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArraySet;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Registry of subscribers to a single event bus.
 *
 * @author Colin Decker
 */
final class SubscriberRegistry {

  /**
   * All registered subscribers, indexed by event type.
   *
   * <p>The {@link CopyOnWriteArraySet} values make it easy and relatively lightweight to get an
   * immutable snapshot of all current subscribers to an event without any locking.
   */
  private final ConcurrentMap<Class<?>, CopyOnWriteArraySet<Subscriber>> subscribers =
      Maps.newConcurrentMap();

  /** The event bus this registry belongs to. */
  private final EventBusImpl bus;

  SubscriberRegistry(EventBusImpl bus) {
    this.bus = checkNotNull(bus);
  }

  /** Registers all subscriber methods on the given listener object. */
  Collection<Subscriber> register(Object listener) {
    Multimap<Class<?>, Subscriber> listenerMethods = findAllSubscribers(listener);

    for (Entry<Class<?>, Collection<Subscriber>> entry : listenerMethods.asMap().entrySet()) {
      Class<?> eventType = entry.getKey();
      Collection<Subscriber> eventMethodsInListener = entry.getValue();

      CopyOnWriteArraySet<Subscriber> eventSubscribers = subscribers.get(eventType);

      if (eventSubscribers == null) {
        CopyOnWriteArraySet<Subscriber> newSet = new CopyOnWriteArraySet<>();
        eventSubscribers =
            MoreObjects.firstNonNull(subscribers.putIfAbsent(eventType, newSet), newSet);
      }

      eventSubscribers.addAll(eventMethodsInListener);
    }

    return listenerMethods.values();
  }

  /** Unregisters all subscribers on the given listener object. */
  void unregister(Object listener) {
    Multimap<Class<?>, Subscriber> listenerMethods = findAllSubscribers(listener);

    for (Entry<Class<?>, Collection<Subscriber>> entry : listenerMethods.asMap().entrySet()) {
      Class<?> eventType = entry.getKey();
      Collection<Subscriber> listenerMethodsForType = entry.getValue();

      CopyOnWriteArraySet<Subscriber> currentSubscribers = subscribers.get(eventType);
      if (currentSubscribers == null || !currentSubscribers.removeAll(listenerMethodsForType)) {
        // if removeAll returns true, all we really know is that at least one subscriber was
        // removed... however, barring something very strange we can assume that if at least one
        // subscriber was removed, all subscribers on listener for that event type were... after
        // all, the definition of subscribers on a particular class is totally static
        throw new IllegalArgumentException(
            "missing event subscriber for an annotated method. Is " + listener + " registered?");
      }

      // don't try to remove the set if it's empty; that can't be done safely without a lock
      // anyway, if the set is empty it'll just be wrapping an array of length 0
    }
  }

  private static final LoadingCache<MethodAnnotation, Annotation> methodAnnotationsCache =
          CacheBuilder.newBuilder().weakKeys().build(new CacheLoader<MethodAnnotation, Annotation>() {
            @Override
            public Annotation load(MethodAnnotation methodAnnotation) throws Exception {
              return methodAnnotation.method.getAnnotation(methodAnnotation.annotationType);
            }
          });

  /**
   * Gets an iterator representing an immutable snapshot of all subscribers to the given event at
   * the time this method is called.
   */
  Iterator<Subscriber> getSubscribers(Object event) {
    ImmutableSet<Class<?>> eventTypes = flattenHierarchy(event.getClass());

    List<Iterator<Subscriber>> subscriberIterators =
        Lists.newArrayListWithCapacity(eventTypes.size());

    for (Class<?> eventType : eventTypes) {
      CopyOnWriteArraySet<Subscriber> eventSubscribers = subscribers.get(eventType);
      if (eventSubscribers != null) {
        // eager no-copy snapshot
        subscriberIterators.add(eventSubscribers.iterator());
      }
    }

    Iterator<Subscriber> iterators = Iterators.concat(subscriberIterators.iterator());

    //Subscriber按OnEvent声明的order排序
    ArrayList<Subscriber> subscribers = Lists.newArrayList(iterators);
    subscribers.sort((Comparator.comparing(subscriber -> {
      Annotation annotation = methodAnnotationsCache.getUnchecked(new MethodAnnotation(subscriber.method, bus.subscribeAnnotation));
      if (annotation instanceof OnEvent) {
        return Integer.valueOf(((OnEvent) annotation).order());
      }
      return 0;
    })));

    return subscribers.iterator();
  }

  /**
   * A thread-safe cache that contains the mapping from each class to all methods in that class and
   * all super-classes, that are annotated with {@code @Subscribe}. The cache is shared across all
   * instances of this class; this greatly improves performance if multiple EventBus instances are
   * created and objects of the same class are registered on all of them.
   */
  private static final LoadingCache<ClassWrapper, ImmutableList<Method>> subscriberMethodsCache =
      CacheBuilder.newBuilder()
          .weakKeys()
          .build(
              new CacheLoader<ClassWrapper, ImmutableList<Method>>() {
                @Override
                public ImmutableList<Method> load(ClassWrapper classWrapper) throws Exception {
                  return getAnnotatedMethodsNotCached(classWrapper);
                }
              });

  /**
   * Returns all subscribers for the given listener grouped by the type of event they subscribe to.
   */
  private Multimap<Class<?>, Subscriber> findAllSubscribers(Object listener) {
    Multimap<Class<?>, Subscriber> methodsInListener = HashMultimap.create();
    Class<?> clazz = listener.getClass();
    for (Method method : getAnnotatedMethods(new ClassWrapper(clazz, bus.subscribeAnnotation))) {
      Class<?>[] parameterTypes = method.getParameterTypes();
      Class<?> eventType = parameterTypes[0];
      /**
       * modify at 2019-12-22 by linkeer8802
       */
      Annotation annotation = methodAnnotationsCache.getUnchecked(new MethodAnnotation(method, bus.subscribeAnnotation));
      methodsInListener.put(eventType, Subscriber.create(bus, listener, method, annotation));
    }
    return methodsInListener;
  }

  private static ImmutableList<Method> getAnnotatedMethods(ClassWrapper classWrapper) {
    return subscriberMethodsCache.getUnchecked(classWrapper);
  }

  private static ImmutableList<Method> getAnnotatedMethodsNotCached(ClassWrapper classWrapper) {
    Set<? extends Class<?>> supertypes = TypeToken.of(classWrapper.clazz).getTypes().rawTypes();
    Map<MethodIdentifier, Method> identifiers = Maps.newHashMap();
    for (Class<?> supertype : supertypes) {
      for (Method method : supertype.getDeclaredMethods()) {
        if (method.isAnnotationPresent(classWrapper.annotationType) && !method.isSynthetic()) {
          // TODO(cgdecker): Should check for a generic parameter type and error out
          Class<?>[] parameterTypes = method.getParameterTypes();
          checkArgument(
              parameterTypes.length == 1,
              "Method %s has @OnEvent annotation but has %s parameters."
                  + "Subscriber methods must have exactly 1 parameter.",
              method,
              parameterTypes.length);

          MethodIdentifier ident = new MethodIdentifier(method);
          if (!identifiers.containsKey(ident)) {
            identifiers.put(ident, method);
          }
        }
      }
    }
    return ImmutableList.copyOf(identifiers.values());
  }

  /** Global cache of classes to their flattened hierarchy of supertypes. */
  private static final LoadingCache<Class<?>, ImmutableSet<Class<?>>> flattenHierarchyCache =
      CacheBuilder.newBuilder()
          .weakKeys()
          .build(
              new CacheLoader<Class<?>, ImmutableSet<Class<?>>>() {
                // <Class<?>> is actually needed to compile
                @SuppressWarnings("RedundantTypeArguments")
                @Override
                public ImmutableSet<Class<?>> load(Class<?> concreteClass) {
                  return ImmutableSet.<Class<?>>copyOf(
                      TypeToken.of(concreteClass).getTypes().rawTypes());
                }
              });

  /**
   * Flattens a class's type hierarchy into a set of {@code Class} objects including all
   * superclasses (transitively) and all interfaces implemented by these superclasses.
   */
  static ImmutableSet<Class<?>> flattenHierarchy(Class<?> concreteClass) {
    try {
      return flattenHierarchyCache.getUnchecked(concreteClass);
    } catch (UncheckedExecutionException e) {
      throw Throwables.propagate(e.getCause());
    }
  }

  private static final class MethodIdentifier {

    private final String name;
    private final List<Class<?>> parameterTypes;

    MethodIdentifier(Method method) {
      this.name = method.getName();
      this.parameterTypes = Arrays.asList(method.getParameterTypes());
    }

    @Override
    public int hashCode() {
      return Objects.hashCode(name, parameterTypes);
    }

    @Override
    public boolean equals(@Nullable Object o) {
      if (o instanceof MethodIdentifier) {
        MethodIdentifier ident = (MethodIdentifier) o;
        return name.equals(ident.name) && parameterTypes.equals(ident.parameterTypes);
      }
      return false;
    }
  }

  /**
   * 方法注解
   */
  private static final class MethodAnnotation {
    private final Method method;
    private final Class<? extends Annotation> annotationType;


    private MethodAnnotation(Method method, Class<? extends Annotation> annotationType) {
      this.method = method;
      this.annotationType = annotationType;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      MethodAnnotation that = (MethodAnnotation) o;
      return java.util.Objects.equals(method, that.method) &&
              java.util.Objects.equals(annotationType, that.annotationType);
    }

    @Override
    public int hashCode() {
      return java.util.Objects.hash(method, annotationType);
    }
  }

  /**
   * 类包装
   */
  private static final class ClassWrapper {
    private final Class<?> clazz;
    private final Class<? extends Annotation> annotationType;

    private ClassWrapper(Class<?> clazz, Class<? extends Annotation> annotationType) {
      this.clazz = clazz;
      this.annotationType = annotationType;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      ClassWrapper that = (ClassWrapper) o;
      return java.util.Objects.equals(clazz, that.clazz) &&
              java.util.Objects.equals(annotationType, that.annotationType);
    }

    @Override
    public int hashCode() {
      return java.util.Objects.hash(clazz, annotationType);
    }
  }
}
