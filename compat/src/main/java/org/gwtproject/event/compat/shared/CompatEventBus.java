/*
 * Copyright © 2017 The GWT Project Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.gwtproject.event.compat.shared;

import static java.util.Objects.requireNonNull;

import java.util.HashMap;
import org.gwtproject.event.shared.Event;
import org.gwtproject.event.shared.EventBus;
import org.gwtproject.event.shared.HandlerRegistration;
import org.gwtproject.event.shared.UmbrellaException;

/** Wraps an {@link EventBus} as a legacy {@link com.google.gwt.event.shared.EventBus}. */
public class CompatEventBus extends com.google.gwt.event.shared.EventBus {
  private static final HashMap<com.google.web.bindery.event.shared.Event.Type<?>, Event.Type<?>>
      eventMap = new HashMap<>();

  @SuppressWarnings("unchecked")
  // @VisibleForTesting
  static <H> Event.Type<H> getNewType(com.google.web.bindery.event.shared.Event.Type<H> type) {
    return (Event.Type<H>)
        eventMap.computeIfAbsent(requireNonNull(type), ignored -> new Event.Type<H>());
  }

  private final EventBus real;

  public CompatEventBus(EventBus real) {
    this.real = real;
  }

  @Override
  public <H> com.google.web.bindery.event.shared.HandlerRegistration addHandler(
      com.google.web.bindery.event.shared.Event.Type<H> type, H handler) {
    return new CompatHandlerRegistration(real.addHandler(getNewType(type), handler));
  }

  @Override
  public <H extends com.google.gwt.event.shared.EventHandler>
      com.google.gwt.event.shared.HandlerRegistration addHandler(
          com.google.gwt.event.shared.GwtEvent.Type<H> type, H handler) {
    return wrap(addHandler((com.google.web.bindery.event.shared.Event.Type<H>) type, handler));
  }

  @Override
  public <H> com.google.web.bindery.event.shared.HandlerRegistration addHandlerToSource(
      com.google.web.bindery.event.shared.Event.Type<H> type, Object source, H handler) {
    return new CompatHandlerRegistration(
        real.addHandlerToSource(getNewType(type), source, handler));
  }

  @Override
  public <H extends com.google.gwt.event.shared.EventHandler>
      com.google.gwt.event.shared.HandlerRegistration addHandlerToSource(
          com.google.gwt.event.shared.GwtEvent.Type<H> type, Object source, H handler) {
    return wrap(
        addHandlerToSource(
            (com.google.web.bindery.event.shared.Event.Type<H>) type, source, handler));
  }

  @Override
  public void fireEvent(com.google.web.bindery.event.shared.Event<?> event) {
    try {
      real.fireEvent(new CompatEvent<>(event));
    } catch (UmbrellaException e) {
      throw new com.google.web.bindery.event.shared.UmbrellaException(e.getCauses());
    }
  }

  @Override
  public void fireEvent(com.google.gwt.event.shared.GwtEvent<?> event) {
    castFireEvent(event);
  }

  @Override
  public void fireEventFromSource(
      com.google.web.bindery.event.shared.Event<?> event, Object source) {
    try {
      real.fireEventFromSource(new CompatEvent<>(event), source);
    } catch (UmbrellaException e) {
      throw new com.google.web.bindery.event.shared.UmbrellaException(e.getCauses());
    }
  }

  @Override
  public void fireEventFromSource(com.google.gwt.event.shared.GwtEvent<?> event, Object source) {
    castFireEventFromSource(event, source);
  }

  private static class CompatHandlerRegistration
      implements com.google.web.bindery.event.shared.HandlerRegistration {
    private final HandlerRegistration real;

    private CompatHandlerRegistration(HandlerRegistration real) {
      this.real = real;
    }

    @Override
    public void removeHandler() {
      real.removeHandler();
    }
  }

  private static class CompatEvent<H> extends Event<H> {
    private final com.google.web.bindery.event.shared.Event<H> real;

    private CompatEvent(com.google.web.bindery.event.shared.Event<H> real) {
      this.real = requireNonNull(real);
    }

    @Override
    public Type<H> getAssociatedType() {
      return getNewType(real.getAssociatedType());
    }

    @Override
    public Object getSource() {
      return real.getSource();
    }

    @Override
    protected void setSource(Object source) {
      setSourceOfEvent(real, source);
    }

    @Override
    protected void dispatch(H handler) {
      dispatchEvent(real, handler);
    }

    @Override
    public String toDebugString() {
      return real.toDebugString();
    }

    @Override
    public String toString() {
      return real.toString();
    }
  }
}
