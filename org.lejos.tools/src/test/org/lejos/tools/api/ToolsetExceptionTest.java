package org.lejos.tools.api;

import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Tests for <code>ToolsetException</code> class.
 * 
 * @author <a href="mailto:jochen.hiller@t-online.de">Jochen Hiller</a>
 */
public class ToolsetExceptionTest extends TestCase {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(
				new TestSuite (ToolsetExceptionTest.class));
	}

	public ToolsetExceptionTest(String name) {
		super(name);
	}
	
	// test methods

	public void testConstructorsValid() {
		new ToolsetException ("abc");
		new ToolsetException ("");
		new ToolsetException ("def", new RuntimeException ());
		new ToolsetException ("", new RuntimeException ());
	}

	public void testConstructorsInvalid() {
		try {
			new ToolsetException (null);
			fail ("Oops, exception expected");
		} catch (IllegalArgumentException ex) {
			// will be expected here
		}
		try {
			new ToolsetException (null, new RuntimeException ());
			fail ("Oops, exception expected");
		} catch (IllegalArgumentException ex) {
			// will be expected here
		}
		try {
			new ToolsetException ("some text", null);
			fail ("Oops, exception expected");
		} catch (IllegalArgumentException ex) {
			// will be expected here
		}
	}

	public void testGetters() {
		ToolsetException ex = new ToolsetException ("abc");
		assertEquals ("abc", ex.getMessage());
		assertNull (ex.getOriginationException());

		Exception rtex = new RuntimeException ();
		ex = new ToolsetException ("def", rtex);
		assertEquals ("def", ex.getMessage());
		assertTrue (rtex == ex.getOriginationException());
	}
}
