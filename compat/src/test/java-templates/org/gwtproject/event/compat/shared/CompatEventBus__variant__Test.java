/*
 * Copyright © 2018 The GWT Project Authors
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

import com.google.web.bindery.event.shared.HandlerRegistration;
import com.google.web.bindery.event.shared.UmbrellaException;
import java.util.HashSet;
import java.util.Set;
import junit.framework.AssertionFailedError;
import junit.framework.TestCase;
import org.gwtproject.event.shared.SimpleEventBus;
import org.gwtproject.event.shared.testing.CountingEventBus;

/**
 * Redundant with {@link org.gwtproject.event.shared.SimpleEventBusTest}, here to ensure legacy
 * compatibility.
 */
public class CompatEventBus__variant__Test extends TestCase {
  /** Handler implementation to allow for easy testing of whether the handler is being called. */
  protected class Adaptor implements FooEvent.Handler, BarEvent.Handler {

    @Override
    public void onFoo(FooEvent event) {
      add(this);
    }

    @Override
    public void onBar(BarEvent event) {
      add(this);
    }

    @Override
    public String toString() {
      return "adaptor 1";
    }
  }

  protected Adaptor adaptor1 = new Adaptor();

  private HashSet<Object> active = new HashSet<>();

  private FooEvent.Handler fooHandler1 =
      new FooEvent.Handler() {
        @Override
        public void onFoo(FooEvent event) {
          add(fooHandler1);
        }

        @Override
        public String toString() {
          return "fooHandler 1";
        }
      };

  private FooEvent.Handler fooHandler2 =
      new FooEvent.Handler() {
        @Override
        public void onFoo(FooEvent event) {
          add(fooHandler2);
        }

        @Override
        public String toString() {
          return "fooHandler 2";
        }
      };

  private FooEvent.Handler fooHandler3 =
      new FooEvent.Handler() {
        @Override
        public void onFoo(FooEvent event) {
          add(fooHandler3);
        }

        @Override
        public String toString() {
          return "fooHandler 3";
        }
      };

  private BarEvent.Handler barHandler1 =
      new BarEvent.Handler() {

        @Override
        public void onBar(BarEvent event) {
          add(barHandler1);
        }

        @Override
        public String toString() {
          return "barHandler 1";
        }
      };

  private BarEvent.Handler barHandler2 =
      new BarEvent.Handler() {

        @Override
        public void onBar(BarEvent event) {
          add(barHandler2);
        }

        @Override
        public String toString() {
          return "barHandler 2";
        }
      };

  private BarEvent.Handler barHandler3 =
      new BarEvent.Handler() {

        @Override
        public void onBar(BarEvent event) {
          add(barHandler3);
        }

        @Override
        public String toString() {
          return "barHandler 3";
        }
      };

  private void add(Object handler) {
    active.add(handler);
  }

  private void assertFired(Object... handler) {
    for (Object aHandler : handler) {
      assertTrue(aHandler + " should have fired", active.contains(aHandler));
    }
  }

  private void assertNotFired(Object... handler) {
    for (Object aHandler : handler) {
      assertFalse(aHandler + " should not have fired", active.contains(aHandler));
    }
  }

  private void reset() {
    active.clear();
  }

  private interface Command {
    void execute();
  }

  private class ShyHandler implements FooEvent.Handler {
    HandlerRegistration r;

    @Override
    public void onFoo(FooEvent event) {
      add(this);
      r.removeHandler();
    }
  }

  private class SourcedHandler implements FooEvent.Handler {
    final String expectedSource;

    SourcedHandler(String source) {
      this.expectedSource = source;
    }

    @Override
    public void onFoo(FooEvent event) {
      add(this);
      assertEquals(expectedSource, event.getSource());
    }
  }

  private static class ThrowingHandler implements FooEvent.Handler {
    private final RuntimeException e;

    public ThrowingHandler(RuntimeException e) {
      this.e = e;
    }

    @Override
    public void onFoo(FooEvent event) {
      throw e;
    }
  }

  public void testAddAndRemoveHandlers() {
    CountingEventBus countingEventBus = new CountingEventBus(new SimpleEventBus());
    CompatEventBus eventBus = new CompatEventBus(countingEventBus);
    FooEvent.register(eventBus, fooHandler1);
    FooEvent.register(eventBus, fooHandler2);
    HandlerRegistration reg1 = FooEvent.register(eventBus, adaptor1);
    eventBus.fireEvent(new FooEvent());
    assertEquals(3, countingEventBus.getHandlerCount(CompatEventBus.getNewType(FooEvent.TYPE)));
    assertFired(fooHandler1, fooHandler2, adaptor1);
    FooEvent.register(eventBus, fooHandler3);
    assertEquals(4, countingEventBus.getHandlerCount(CompatEventBus.getNewType(FooEvent.TYPE)));

    FooEvent.register(eventBus, fooHandler1);
    FooEvent.register(eventBus, fooHandler2);
    HandlerRegistration reg2 = FooEvent.register(eventBus, adaptor1);

    /*
     * You can indeed add handlers twice, they will only be removed one at a
     * time though.
     */
    assertEquals(7, countingEventBus.getHandlerCount(CompatEventBus.getNewType(FooEvent.TYPE)));
    eventBus.addHandler(BarEvent.TYPE, adaptor1);
    eventBus.addHandler(BarEvent.TYPE, barHandler1);
    eventBus.addHandler(BarEvent.TYPE, barHandler2);

    assertEquals(7, countingEventBus.getHandlerCount(CompatEventBus.getNewType(FooEvent.TYPE)));
    assertEquals(3, countingEventBus.getHandlerCount(CompatEventBus.getNewType(BarEvent.TYPE)));

    reset();
    eventBus.fireEvent(new FooEvent());
    assertFired(fooHandler1, fooHandler2, fooHandler3, adaptor1);
    assertNotFired(barHandler1, barHandler2);

    // Gets rid of first instance.
    reg1.removeHandler();
    eventBus.fireEvent(new FooEvent());
    assertFired(fooHandler1, fooHandler2, fooHandler3, adaptor1);
    assertNotFired(barHandler1, barHandler2);

    // Gets rid of second instance.
    reg2.removeHandler();
    reset();
    eventBus.fireEvent(new FooEvent());

    assertFired(fooHandler1, fooHandler2, fooHandler3);
    assertNotFired(adaptor1, barHandler1, barHandler2);

    // Checks to see if barHandler events are still working.
    reset();
    eventBus.fireEvent(new BarEvent());

    assertNotFired(fooHandler1, fooHandler2, fooHandler3);
    assertFired(barHandler1, barHandler2, adaptor1);
  }

  public void testAssertThrowsNpe() {
    final CompatEventBus eventBus = new CompatEventBus(new SimpleEventBus());

    try {
      assertThrowsNpe(() -> FooEvent.register(eventBus, fooHandler1));
      throw new Error("expected AssertionFailedError");
    } catch (AssertionFailedError e) {
      /* pass */
    }
  }

  public void testConcurrentAdd() {
    final CompatEventBus eventBus = new CompatEventBus(new SimpleEventBus());
    final FooEvent.Handler two =
        new FooEvent.Handler() {
          @Override
          public void onFoo(FooEvent event) {
            add(this);
          }
        };
    FooEvent.Handler one =
        new FooEvent.Handler() {
          @Override
          public void onFoo(FooEvent event) {
            FooEvent.register(eventBus, two);
            add(this);
          }
        };
    FooEvent.register(eventBus, one);
    FooEvent.register(eventBus, fooHandler1);
    FooEvent.register(eventBus, fooHandler2);
    FooEvent.register(eventBus, fooHandler3);
    eventBus.fireEvent(new FooEvent());
    assertFired(one, fooHandler1, fooHandler2, fooHandler3);
    assertNotFired(two);

    reset();
    eventBus.fireEvent(new FooEvent());
    assertFired(one, two, fooHandler1, fooHandler2, fooHandler3);
  }

  public void testConcurrentAddAfterRemoveIsNotClobbered() {
    final CompatEventBus eventBus = new CompatEventBus(new SimpleEventBus());

    FooEvent.Handler one =
        new FooEvent.Handler() {
          HandlerRegistration reg = addIt();

          @Override
          public void onFoo(FooEvent event) {
            reg.removeHandler();
            addIt();
            add(this);
          }

          private HandlerRegistration addIt() {
            return FooEvent.register(eventBus, fooHandler1);
          }
        };

    FooEvent.register(eventBus, one);

    eventBus.fireEvent(new FooEvent());
    assertFired(one);

    reset();

    eventBus.fireEvent(new FooEvent());
    assertFired(one, fooHandler1);
  }

  public void testConcurrentAddAndRemoveByNastyUsersTryingToHurtUs() {
    final CompatEventBus eventBus = new CompatEventBus(new SimpleEventBus());
    final FooEvent.Handler two =
        new FooEvent.Handler() {
          @Override
          public void onFoo(FooEvent event) {
            add(this);
          }

          @Override
          public String toString() {
            return "two";
          }
        };
    FooEvent.Handler one =
        new FooEvent.Handler() {
          @Override
          public void onFoo(FooEvent event) {
            FooEvent.register(eventBus, two).removeHandler();
            add(this);
          }

          @Override
          public String toString() {
            return "one";
          }
        };
    FooEvent.register(eventBus, one);
    FooEvent.register(eventBus, fooHandler1);
    FooEvent.register(eventBus, fooHandler2);
    FooEvent.register(eventBus, fooHandler3);
    eventBus.fireEvent(new FooEvent());
    assertFired(one, fooHandler1, fooHandler2, fooHandler3);
    assertNotFired(two);

    reset();
    eventBus.fireEvent(new FooEvent());
    assertFired(one, fooHandler1, fooHandler2, fooHandler3);
    assertNotFired(two);
  }

  public void testConcurrentRemove() {
    final CompatEventBus eventBus = new CompatEventBus(new SimpleEventBus());

    ShyHandler h = new ShyHandler();

    FooEvent.register(eventBus, fooHandler1);
    h.r = FooEvent.register(eventBus, h);
    FooEvent.register(eventBus, fooHandler2);
    FooEvent.register(eventBus, fooHandler3);

    eventBus.fireEvent(new FooEvent());
    assertFired(h, fooHandler1, fooHandler2, fooHandler3);
    reset();
    eventBus.fireEvent(new FooEvent());
    assertFired(fooHandler1, fooHandler2, fooHandler3);
    assertNotFired(h);
  }

  public void testFromSource() {
    final CompatEventBus eventBus = new CompatEventBus(new SimpleEventBus());

    SourcedHandler global = new SourcedHandler("able");
    SourcedHandler able = new SourcedHandler("able");
    SourcedHandler baker = new SourcedHandler("baker");

    FooEvent.register(eventBus, global);
    FooEvent.register(eventBus, "able", able);
    FooEvent.register(eventBus, "baker", baker);

    eventBus.fireEventFromSource(new FooEvent(), "able");
    assertFired(global, able);
    assertNotFired(baker);
  }

  public void testHandlersThrow() {
    RuntimeException exception1 = new RuntimeException("first exception");
    RuntimeException exception2 = new RuntimeException("second exception");

    final CompatEventBus eventBus = new CompatEventBus(new SimpleEventBus());

    FooEvent.register(eventBus, fooHandler1);
    FooEvent.register(eventBus, new ThrowingHandler(exception1));
    FooEvent.register(eventBus, fooHandler2);
    FooEvent.register(eventBus, new ThrowingHandler(exception2));
    FooEvent.register(eventBus, fooHandler3);

    FooEvent event = new FooEvent();

    try {
      eventBus.fireEvent(event);
      fail("eventBus should have thrown");
    } catch (UmbrellaException e) {
      Set<Throwable> causes = e.getCauses();
      assertEquals("Exception should wrap the two thrown exceptions", 2, causes.size());
      assertTrue("First exception should be under the umbrella", causes.contains(exception1));
      assertTrue("Second exception should be under the umbrella", causes.contains(exception2));
    }

    /*
     * Exception should not have prevented all three mouse handlers from getting
     * the event.
     */
    assertFired(fooHandler1, fooHandler2, fooHandler3);
  }

  public void testNoDoubleRemove() {
    final CompatEventBus eventBus = new CompatEventBus(new SimpleEventBus());
    HandlerRegistration reg = FooEvent.register(eventBus, fooHandler1);
    reg.removeHandler();
    reg.removeHandler(); // should not throw
  }

  public void testNoSource() {
    final CompatEventBus eventBus = new CompatEventBus(new SimpleEventBus());

    SourcedHandler global = new SourcedHandler(null);
    SourcedHandler able = new SourcedHandler("able");
    SourcedHandler baker = new SourcedHandler("baker");

    FooEvent.register(eventBus, global);
    FooEvent.register(eventBus, "able", able);
    FooEvent.register(eventBus, "baker", baker);

    eventBus.fireEvent(new FooEvent());
    assertFired(global);
    assertNotFired(able, baker);
  }

  public void testNullChecks() {
    final CompatEventBus eventBus = new CompatEventBus(new SimpleEventBus());
    assertThrowsNpe(() -> eventBus.addHandler(null, fooHandler1));

    assertThrowsNpe(() -> FooEvent.register(eventBus, "foo", null));
    assertThrowsNpe(() -> FooEvent.register(eventBus, null, fooHandler1));
    assertThrowsNpe(() -> eventBus.addHandlerToSource(null, "foo", fooHandler1));

    assertThrowsNpe(() -> eventBus.fireEvent(null));

    assertThrowsNpe(() -> eventBus.fireEventFromSource(null, ""));

    assertThrowsNpe(() -> eventBus.fireEventFromSource(new FooEvent() {}, null));
    assertThrowsNpe(() -> eventBus.fireEventFromSource(null, "baker"));
  }

  public void testNullSourceOkay() {
    CompatEventBus reg = new CompatEventBus(new SimpleEventBus());

    FooEvent.Handler handler =
        new FooEvent.Handler() {
          @Override
          public void onFoo(FooEvent event) {
            add(this);
            assertNull(event.getSource());
          }
        };
    reg.addHandler(FooEvent.TYPE, handler);
    reg.fireEvent(new FooEvent());
    assertFired(handler);
  }

  public void testRemoveSelf() {
    final CompatEventBus eventBus = new CompatEventBus(new SimpleEventBus());

    FooEvent.Handler h =
        new FooEvent.Handler() {
          HandlerRegistration reg = FooEvent.register(eventBus, this);

          @Override
          public void onFoo(FooEvent event) {
            add(this);
            reg.removeHandler();
          }
        };

    eventBus.fireEvent(new FooEvent());
    assertFired(h);

    reset();

    eventBus.fireEvent(new FooEvent());
    assertNotFired(h);
  }

  private void assertThrowsNpe(Command command) {
    try {
      command.execute();
      fail("expected NullPointerException");
    } catch (NullPointerException e) {
      /* pass */
    }
  }
}
