package org.lejos.tools.impl;

import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.lejos.tools.api.IProgressMonitorToolset;

/**
 * Tests for <code>NullProgressMonitorToolsetImpl</code> class.
 * 
 * @author <a href="mailto:jochen.hiller@t-online.de">Jochen Hiller </a>
 */
public class NullProgressMonitorToolsetImplTest extends TestCase
{

   public static void main (String[] args)
   {
      junit.textui.TestRunner.run(new TestSuite(
         NullProgressMonitorToolsetImplTest.class));
   }

   public NullProgressMonitorToolsetImplTest (String name)
   {
      super(name);
   }

   // test methods

   public void testConstructor ()
   {
      new NullProgressMonitorToolsetImpl();
   }

   public void testTasks ()
   {
      IProgressMonitorToolset progress = new NullProgressMonitorToolsetImpl();
      progress.beginTask("Doing something", 100);
      progress.worked(25);
      progress.worked(50);
      progress.worked(75);
      progress.worked(100);
      progress.done();
   }

   public void testCancel ()
   {
      IProgressMonitorToolset progress = new NullProgressMonitorToolsetImpl();
      assertFalse(progress.isCanceled());
      progress.setCanceled(true);
      // remains false
      assertFalse(progress.isCanceled());
   }
}