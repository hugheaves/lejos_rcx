package org.lejos.tools.eclipse.plugin;

import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaProject;
import org.lejos.tools.api.ToolsetException;
import org.lejos.tools.eclipse.plugin.util.SimpleProjectCreator;
import org.lejos.tools.impl.NullProgressMonitorToolsetImpl;

/**
 * Tests for <code>EclipseToolsetFactory</code> class.
 * 
 * @author <a href="mailto:jochen.hiller@t-online.de">Jochen Hiller</a>
 */
public class EclipseToolsetFacadeTest extends TestCase {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(
			new TestSuite(EclipseToolsetFacadeTest.class));
	}

	public EclipseToolsetFacadeTest(String name) {
		super(name);
	}

	// test methods

	public void testLinkOneCU()
		throws ToolsetException, CoreException, InterruptedException {
		SimpleProjectCreator creator = new SimpleProjectCreator();
		creator.createSimpleProject();
		IJavaProject javaProject = creator.getJavaProject();

		EclipseToolsetFacade facade = new EclipseToolsetFacade();
		// disbable output for tests
		facade.setProgressMonitor(new NullProgressMonitorToolsetImpl());
		facade.linkCU(
			creator.getCUPackage2Class1(),
			LejosPlugin.getPreferences());

		creator.deleteProject(javaProject);
	}

	public void testLinkJavaElement()
		throws ToolsetException, CoreException, InterruptedException {
		SimpleProjectCreator creator = new SimpleProjectCreator();
		creator.createSimpleProject();
		IJavaProject javaProject = creator.getJavaProject();

		EclipseToolsetFacade facade = new EclipseToolsetFacade();
		// disbable output for tests
		facade.setProgressMonitor(new NullProgressMonitorToolsetImpl());
		facade.linkJavaElement(
			creator.getPackage1(),
			LejosPlugin.getPreferences());

		creator.deleteProject(javaProject);
	}
}
