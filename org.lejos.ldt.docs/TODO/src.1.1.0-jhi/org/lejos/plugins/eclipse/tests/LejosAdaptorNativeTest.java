package org.lejos.plugins.eclipse.tests;

import junit.framework.TestCase;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.lejos.plugins.eclipse.tests.util.JavaProjectHelper;

/**
 * Tests for the class LejosAdaptorNative.
 * 
 * @author <a href="mailto:jochen.hiller@t-online.de">Jochen Hiller</a>
 */
public class LejosAdaptorNativeTest extends TestCase {

	// attributes

	private IJavaProject fProject;
	private IPackageFragment fPackage;
	private ICompilationUnit fCompilationUnit;

	// standard methods

	public LejosAdaptorNativeTest(String name) {
		super(name);
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(LejosAdaptorNativeTest.class);
	}

	protected void setUp() throws Exception {
		fProject = JavaProjectHelper.createJavaProject("project1", "bin");
		assertTrue(
			"rt not found",
			JavaProjectHelper.addRTJar(fProject) != null);

		IPackageFragmentRoot sourceFolder =
			JavaProjectHelper.addSourceContainer(fProject, "src");
		fPackage = sourceFolder.createPackageFragment("package1", false, null);
		StringBuffer buf = new StringBuffer();
		buf.append("package package1;\n");
		buf.append("public class Class1 {\n");
		buf.append("    public static void main(String[] args)\n");
		buf.append("            throws InterruptedException {\n");
		buf.append("        TextLCD.print(\"Hello\");\n");
		buf.append("        Thread.sleep (1000);\n");
		buf.append("        TextLCD.print(\"World\");\n");
		buf.append("        Thread.sleep (1000);\n");
		buf.append("    }\n");
		buf.append("}\n");

		fCompilationUnit =
			fPackage.createCompilationUnit(
				"Class1.java",
				buf.toString(),
				false,
				null);
	}

	protected void tearDown() throws Exception {
		JavaProjectHelper.delete(fProject);
		fProject = null;
		fPackage = null;
		fCompilationUnit = null;
	}

	// test methods

	public void testLinkApplication() {
		assertTrue (false);
		// @TODO
	}
}
