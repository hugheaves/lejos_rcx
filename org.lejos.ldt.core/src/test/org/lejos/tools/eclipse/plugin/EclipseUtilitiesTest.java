package org.lejos.tools.eclipse.plugin;

import java.io.File;
import java.io.IOException;

import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaModelException;
import org.lejos.tools.eclipse.plugin.util.SimpleProjectCreator;

/**
 * Tests for <code>EclipseUtilities</code> class.
 * 
 * @author <a href="mailto:jochen.hiller@t-online.de">Jochen Hiller</a>
 */
public class EclipseUtilitiesTest extends TestCase {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(new TestSuite(EclipseUtilitiesTest.class));
	}

	public EclipseUtilitiesTest(String name) {
		super(name);
	}

	private SimpleProjectCreator creator;
	protected void setUp() throws Exception {
		creator = new SimpleProjectCreator();
		creator.createSimpleProject();
	}
	protected void tearDown() throws Exception {
		// cleanup project
		creator.deleteProject(creator.getJavaProject());
		creator = null;
	}

	// test methods

	public void testGetOutputFolder() throws CoreException, IOException {
		// check, whether .class will be correct
		ICompilationUnit cu = creator.getCUPackage2Class1();
		File f = EclipseUtilities.getOutputFolder(cu);
		assertNotNull(f);
		File required =
			new File(
				"junit-workbench-workspace/simpleproject1/" + "build/classes/");
		// compare the canonical files
		assertEquals(required.getCanonicalFile(), f.getCanonicalFile());
	}

	public void testGetOutputFile() throws CoreException, IOException {
		// check, whether .class will be correct
		ICompilationUnit cu = creator.getCUPackage2Class1();
		File f = EclipseUtilities.getOutputFile(cu, ".class");
		assertNotNull(f);
		File required =
			new File(
				"junit-workbench-workspace/simpleproject1/"
					+ "build/classes/"
					+ "package1/package2/Class1.class");
		// compare the canonical files
		assertEquals(required.getCanonicalFile(), f.getCanonicalFile());

		f = EclipseUtilities.getOutputFile(cu, ".leJOS");
		assertNotNull(f);
		required =
			new File(
				"junit-workbench-workspace/simpleproject1/"
					+ "build/classes/"
					+ "package1/package2/Class1.leJOS");
		// compare the canonical files
		assertEquals(required.getCanonicalFile(), f.getCanonicalFile());

		f = EclipseUtilities.getOutputFile(cu, ".leJOS.signature");
		assertNotNull(f);
		required =
			new File(
				"junit-workbench-workspace/simpleproject1/"
					+ "build/classes/"
					+ "package1/package2/Class1.leJOS.signature");
		// compare the canonical files
		assertEquals(required.getCanonicalFile(), f.getCanonicalFile());
	}

	public void testGetAbsoluteLocationForResource()
		throws CoreException, IOException {
		IProject project = creator.getJavaProject().getProject();
		// the resource is named including the project name
		IPath path =
			new Path("/simpleproject1/src/package1/package2/Class1.java");

		File f = EclipseUtilities.getAbsoluteLocationForResource(project, path);
		File required =
			new File(
				"junit-workbench-workspace/simpleproject1/"
					+ "src/"
					+ "package1/package2/Class1.java");
		// compare the canonical files
		assertEquals(required.getCanonicalFile(), f.getCanonicalFile());
	}

	public void testGetFQCN() throws JavaModelException {
		String fqcn = EclipseUtilities.getFQCN(creator.getCUPackage2Class1());
		assertEquals("package1.package2.Class1", fqcn);
	}

	public void testHasMain() throws JavaModelException {
		boolean hasMain =
			EclipseUtilities.hasMain(creator.getCUPackage2Class1());
		assertTrue(hasMain);
		// Class2.java does NOT have a main
		hasMain = EclipseUtilities.hasMain(creator.getCUPackage2Class2());
		assertFalse(hasMain);
	}

	public void testCollectLinkClasses() throws JavaModelException {
		ICompilationUnit[] cus = null;
		// for Class1, we expect 1 CU
		cus =
			EclipseUtilities.collectLinkClasses(creator.getCUPackage2Class1());
		assertNotNull(cus);
		assertEquals(1, cus.length);
		// for Class2, we expect 0 CU
		cus =
			EclipseUtilities.collectLinkClasses(creator.getCUPackage2Class2());
		assertNotNull(cus);
		assertEquals(0, cus.length);
		// for package2, we expect 1 CU
		cus = EclipseUtilities.collectLinkClasses(creator.getPackage2());
		assertNotNull(cus);
		assertEquals(1, cus.length);
		// for package1, we expect 2 CU
		cus = EclipseUtilities.collectLinkClasses(creator.getPackage1());
		assertNotNull(cus);
		assertEquals(2, cus.length);
	}
}
