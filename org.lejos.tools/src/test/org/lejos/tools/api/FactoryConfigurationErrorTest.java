package org.lejos.tools.api;

import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Tests for <code>FactoryConfigurationError</code> class.
 * 
 * @author <a href="mailto:jochen.hiller@t-online.de">Jochen Hiller </a>
 */
public class FactoryConfigurationErrorTest extends TestCase {

    public static void main(String[] args) {
        junit.textui.TestRunner.run(new TestSuite(
                FactoryConfigurationErrorTest.class));
    }

    public FactoryConfigurationErrorTest(String name) {
        super(name);
    }

    // test methods

    public void testConstructorsValid() {
        new FactoryConfigurationError(null);
        new FactoryConfigurationError("");
        new FactoryConfigurationError("abc");
        new FactoryConfigurationError(null, new RuntimeException());
        new FactoryConfigurationError("", new RuntimeException());
        new FactoryConfigurationError("abc", new RuntimeException());
    }

    public void testGetMessage() {
        FactoryConfigurationError err;
        err = new FactoryConfigurationError(null);
        assertEquals(FactoryConfigurationError.class.getName(), err
                .getMessage());

        err = new FactoryConfigurationError("");
        assertEquals("", err.getMessage());

        err = new FactoryConfigurationError("def");
        assertEquals("def", err.getMessage());

        err = new FactoryConfigurationError(null, new RuntimeException());
        assertEquals(RuntimeException.class.getName(), err.getMessage());

        err = new FactoryConfigurationError("", new RuntimeException());
        assertEquals("", err.getMessage());

        err = new FactoryConfigurationError("ghi", new RuntimeException());
        assertEquals("ghi", err.getMessage());

        err = new FactoryConfigurationError(null, new RuntimeException("bla"));
        assertEquals("bla", err.getMessage());
    }

    public void testGetException() {
        FactoryConfigurationError err;
        err = new FactoryConfigurationError(null);
        assertNull(err.getException());

        Exception ex = new RuntimeException();
        err = new FactoryConfigurationError(null, ex);
        assertEquals(ex, err.getException());
    }
}