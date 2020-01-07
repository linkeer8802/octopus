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

import java.util.Iterator;
import java.util.Objects;


abstract class Dispatcher {

  static Dispatcher immediate() {
    return ImmediateDispatcher.INSTANCE;
  }

  abstract void dispatch(Object event, Iterator<Subscriber> subscribers);

  private static final class ImmediateDispatcher extends Dispatcher {
    private static final ImmediateDispatcher INSTANCE = new ImmediateDispatcher();

    @Override
    void dispatch(Object event, Iterator<Subscriber> subscribers) {
      Objects.requireNonNull(event);
      while (subscribers.hasNext()) {
        subscribers.next().dispatchEvent(event);
      }
    }
  }
}
