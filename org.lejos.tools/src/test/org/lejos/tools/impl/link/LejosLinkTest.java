package org.lejos.tools.impl.link;

import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.lejos.tools.api.IRuntimeToolset;

/**
 * Tests for <code>LejosLink</code> class.
 * 
 * <p>
 * We can test the class without any problems, as we are in same package scope.
 * </p>
 * 
 * @author <a href="mailto:jochen.hiller@t-online.de">Jochen Hiller</a>
 */
public class LejosLinkTest extends TestCase {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(new TestSuite(LejosLinkTest.class));
	}

	public LejosLinkTest(String name) {
		super(name);
	}

	// test methods

	public void testConstructor() {
		new LejosLink();
	}

	public void testSetLinkMethod() {
		LejosLink link = new LejosLink();
		link.setLinkMethod(IRuntimeToolset.LINK_METHOD_ALL);
		link.setLinkMethod(IRuntimeToolset.LINK_METHOD_OPTIMIZING);
		try {
			link.setLinkMethod(IRuntimeToolset.LINK_METHOD_UNKNOWN);
			fail("Oops, exception expected");
		} catch (IllegalArgumentException ex) {
			// ignore, will be expected
		}
		try {
			link.setLinkMethod(999);
			fail("Oops, exception expected");
		} catch (IllegalArgumentException ex) {
			// ignore, will be expected
		}
	}

	public void testSetByteOrder() {
		LejosLink link = new LejosLink();
		link.setByteOrder(IRuntimeToolset.BYTE_ORDER_BIG_ENDIAN);
		link.setByteOrder(IRuntimeToolset.BYTE_ORDER_LITTLE_ENDIAN);
		try {
			link.setByteOrder(IRuntimeToolset.BYTE_ORDER_UNKNOWN);
			fail("Oops, exception expected");
		} catch (IllegalArgumentException ex) {
			// ignore, will be expected
		}
		try {
			link.setByteOrder(999);
			fail("Oops, exception expected");
		} catch (IllegalArgumentException ex) {
			// ignore, will be expected
		}
	}
}
