/*
 * Copyright 2008 The GWT Project Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.gwtproject.event.logical.shared;

import junit.framework.TestCase;
import org.gwtproject.event.shared.Event;
import org.gwtproject.event.shared.EventBus;
import org.gwtproject.event.shared.SimpleEventBus;

/** Tests of logical events. */
public class LogicalEventsTest extends TestCase {

  static class Fire
      implements SelectionHandler<String>,
          BeforeSelectionHandler<String>,
          CloseHandler<String>,
          OpenHandler<String>,
          ResizeHandler,
          ValueChangeHandler<String> {
    public boolean flag = false;

    @Override
    public void onBeforeSelection(BeforeSelectionEvent<String> event) {
      flag = true;
    }

    @Override
    public void onClose(CloseEvent<String> event) {
      flag = true;
    }

    @Override
    public void onOpen(OpenEvent<String> event) {
      flag = true;
    }

    @Override
    public void onResize(ResizeEvent event) {
      flag = true;
    }

    @Override
    public void onSelection(SelectionEvent<String> event) {
      flag = true;
    }

    @Override
    public void onValueChange(ValueChangeEvent<String> event) {
      flag = true;
    }
  }

  private EventBus manager;

  @Override
  protected void setUp() {
    manager = new SimpleEventBus();
  }

  public void testSimpleFire() {
    simpleFire(BeforeSelectionEvent.getType(), new BeforeSelectionEvent<String>());
    simpleFire(SelectionEvent.getType(), new SelectionEvent<String>(null));
    simpleFire(CloseEvent.getType(), new CloseEvent<String>(null, false));
    simpleFire(OpenEvent.getType(), new OpenEvent<String>(null));
    simpleFire(ResizeEvent.getType(), new ResizeEvent(0, 0));
    simpleFire(ValueChangeEvent.getType(), new ValueChangeEvent<String>(null));
  }

  @SuppressWarnings("unchecked")
  private <H> void simpleFire(Event.Type<H> type, Event<? extends H> instance) {
    Fire f = new Fire();
    manager.addHandler(type, (H) f);
    manager.fireEvent(instance);
    assertTrue(f.flag);
  }
}
