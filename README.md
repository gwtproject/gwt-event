GWT Events
==========

A future-proof port of the `com.google.web.bindery.event.Event` GWT module,
with no dependency on `gwt-user` (besides the Java Runtime Emulation),
to prepare for GWT 3 / J2Cl.

Also features a port of `com.google.gwt.event.logical.LogicalEvent`,
and compatibility layers, in the form of a partial port of `com.google.gwt.event.Event`
as well as a bridge between this new library and the `gwt-user` modules.

All modules can be used on client-side, server-side, or Android.

Migrating from `com.google.web.bindery.event.Event`
-------------------------------------------------------

1. Add the dependency to your build.

   For Maven:

   ```xml
   <dependency>
     <groupId>org.gwtproject.event</groupId>
     <artifactId>gwt-event</artifactId>
     <version>${gwtEventVersion}</version>
   </dependency>
   ```

   For Gradle:

   ```gradle
   implementation("org.gwtproject.event:gwt-event:${gwtEventVersion}")
   ```

2. Update your GWT module to use

   ```xml
   <inherits name="org.gwtproject.event.Event" />
   ```

   (either change from `com.google.web.bindery.event.Event`,
   or add it if you inherited it transitively from another GWT module)

3. Change your `import`s in your Java source files:

   ```java
   import org.gwtproject.event.shared.Event;
   import org.gwtproject.event.shared.EventBus;
   import org.gwtproject.event.shared.HandlerRegistration;
   ```

Use the compatibility bridge
----------------------------

The `org.gwtproject.event.compat.EventCompat` module provides a `CompatEventBus` class
that wraps an `org.gwtproject.event.EventBus` as a `com.google.gwt.event.shared.EventBus`.
This can be especially useful to start migrating to `org.gwtproject.event.Event`
without being blocked by libraries still using `com.google.web.bindery.event.Event`
or `com.google.gwt.event.Event`.

1. Add the dependency to your build.

   For Maven:

   ```xml
   <dependency>
     <groupId>org.gwtproject.event</groupId>
     <artifactId>gwt-event-compat</artifactId>
     <version>${gwtEventVersion}</version>
   </dependency>
   ```

   For Gradle:

   ```gradle
   implementation("org.gwtproject.event:gwt-event-compat:${gwtEventVersion}")
   ```

2. Update your GWT module to use

   ```xml
   <inherits name="org.gwtproject.event.compat.EventCompat" />
   ```

3. Wrap an `EventBus` within a `CompatEventBus` whenever you need it;
   a `CompatEventBus` is very lightweight, create as many as you need.

While this is mostly equivalent to having two separate event buses,
given that events aren't compatible with each others,
the fact `CompatEventBus` can wrap any `org.gwtproject.event.shared.EventBus`
means that, for instance, you only need one `CountingEventBus`,
and a `ResettableEventBus`'s `removeHandler` will remove all handlers
added to either the wrapped bus or the `CompatEventBus`
(and supports any other use-case where you have a specific `EventBus`).

Internally, `CompatEventBus` records a mapping between the
`com.google.web.bindery.event.shared.Event.Type`s and (internal)
`org.gwtproject.event.shared.Event.Type`s.
This mapping is shared by all `CompatEventBus` instances,
meaning that you can wrap your `EventBus`es as close as where you need it,
and two `CompatEventBus`es wrapping the same `EventBus` will
happily dispatch events to each other (through the shared bus they wrap).

Migrating from `com.google.gwt.event.logical.LogicalEvent`
----------------------------------------------------------

1. Add the dependency to your build.

   For Maven:

   ```xml
   <dependency>
     <groupId>org.gwtproject.event</groupId>
     <artifactId>gwt-logical-event</artifactId>
     <version>${gwtEventVersion}</version>
   </dependency>
   ```

   For Gradle:

   ```gradle
   implementation("org.gwtproject.event:gwt-logical-event:${gwtEventVersion}")
   ```

2. Update your GWT module to use

   ```xml
   <inherits name="org.gwtproject.event.logical.LogicalEvent" />
   ```

   (either change from `com.google.gwt.event.logical.LogicalEvent`,
   or add it if you inherited it transitively from another GWT module)

3. Change your `import`s in your Java source files
   from `com.google.gwt.event.logical.*` to `org.gwtproject.event.logical.*`

Migrating from `com.google.gwt.event.Event`
-------------------------------------------

You should first migrate to `com.google.web.bindery.event.Event`
then follow the steps above.
However, `GwtEvent` and `EventHandler` are provided to make the migration easier
(i.e. so you can first migrate away from `gwt-user`
and then refactor your code from `GwtEvent` to `Event`).

1. Add the dependency to your build.

   For Maven:

   ```xml
   <dependency>
     <groupId>org.gwtproject.event</groupId>
     <artifactId>gwt-event-legacy</artifactId>
     <version>${gwtEventVersion}</version>
   </dependency>
   ```

   For Gradle:

   ```gradle
   implementation("org.gwtproject.event:gwt-event-legacy:${gwtEventVersion}")
   ```

2. Update your GWT module to use

   ```xml
   <inherits name="org.gwtproject.event.legacy.EventLegacy" />
   ```

   (either change from `com.google.gwt.event.Event`,
   or add it if you inherited it transitively from another GWT module)

3. Change your `import`s in your Java source files:

   ```java
   import org.gwtproject.event.legacy.shared.EventHandler;
   import org.gwtproject.event.legacy.shared.GwtEvent;
   ```
