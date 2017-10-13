package org.gwtproject.event.compat.shared;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/** Tests of compat Event code. */
@RunWith(Suite.class)
@SuiteClasses({CompatEventBusBinderyTest.class, CompatEventBusGwtTest.class})
public class EventCompatSuite {}
