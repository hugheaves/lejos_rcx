package org.lejos.tools.eclipse.plugin.util;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.lejos.tools.eclipse.plugin.EclipseUtilities;

/**
 * A <code>SimpleJavaProject</code> for testing purposes.
 * 
 * @author <a href="mailto:jochen.hiller@t-online.de">Jochen Hiller</a>
 */
public class SimpleJavaProject extends AbstractJavaTestProject {

	// attributes

	private IPackageFragment package1;
	private IType package1Class1;
	private IPackageFragment package1package2;
	private IType package1package2Class1;
	private IType package1package2Class2;

	// public methods

	public void createProject()
		throws CoreException, IOException, MalformedURLException {
		
		// enable auto building after creation of project
		setAutoBuilding(false);
		
		createProject("simpleproject");
		
		this.sourceFolder = createSourceFolder("src");

		createFolder("build");
		IFolder binFolder = createFolder("build/classes");

		createOutputFolder(binFolder);
		// do NOT add system libraries, use leJOS classes instead
		addJar("org.lejos", "lib/classes.jar");
		addJar("org.lejos", "lib/rcxcomm.jar");

		// specific for leJOS
		// enforce compile with 1.1 class format
		this.javaProject.setOption(
			JavaCore.COMPILER_CODEGEN_TARGET_PLATFORM,
			JavaCore.VERSION_1_1);

		this.package1 = createPackage("package1");
		StringBuffer buf;
		buf = new StringBuffer();
		buf.append("import josx.platform.rcx.TextLCD;\n");
		buf.append("\n");
		buf.append("public class Class1 {\n");
		buf.append("  public static void main(String[] args)\n");
		buf.append("      throws InterruptedException {\n");
		buf.append("    TextLCD.print(\"Hello\");\n");
		buf.append("    Thread.sleep (1000);\n");
		buf.append("    TextLCD.print(\"World\");\n");
		buf.append("    Thread.sleep (1000);\n");
		buf.append("  }\n");
		buf.append("}\n");
		this.package1Class1 =
			createType(this.package1, "Class1.java", buf.toString());

		this.package1package2 = createPackage("package1.package2");
		buf = new StringBuffer();
		buf.append("import josx.platform.rcx.TextLCD;\n");
		buf.append("\n");
		buf.append("public class Class1 {\n");
		buf.append("  public static void main(String[] args)\n");
		buf.append("      throws InterruptedException {\n");
		buf.append("    TextLCD.print(\"Hello\");\n");
		buf.append("    Thread.sleep (1000);\n");
		buf.append("    TextLCD.print(\"World\");\n");
		buf.append("    Thread.sleep (1000);\n");
		buf.append("  }\n");
		buf.append("}\n");
		this.package1package2Class1 =
			createType(this.package1package2, "Class1.java", buf.toString());

		buf = new StringBuffer();
		buf.append("public class Class2 {\n");
		buf.append("}\n");

		this.package1package2Class2 =
			createType(this.package1package2, "Class2.java", buf.toString());
		// now enforce building
		setAutoBuilding(true);
		
        // now check, if Class1.java really exists
        File sourceFile = EclipseUtilities.getAbsoluteLocationForResource(
                this.getPackage1Package2Class1CU().getJavaProject().getProject(),
                this.getPackage1Package2Class1CU().getPath());
        if (!sourceFile.exists()) {
            IStatus status =   new Status(
                        Status.ERROR,
                        "org.lejos.tools.eclipse.plugin",
                        1,
                        "source file " + sourceFile + " does not exist",
                        null);
            throw new CoreException(status);
        }
        
		try {
			// give auto build a chance to run
			Thread.sleep(1000);
		} catch (InterruptedException ex) {
			// ignore
		}

		buildProject();

		// now check, if Class1.class now really exists
		File classFile =
			EclipseUtilities.getOutputFile(
				this.getPackage1Package2Class1CU(),
				".class");
		if (!classFile.exists()) {
			IStatus status =
				new Status(
					Status.ERROR,
					"org.lejos.tools.eclipse.plugin",
					1,
					"Compile failed",
					null);
			throw new CoreException(status);
		}
	}

	// public methods

	public IPackageFragment getPackage1() {
		return this.package1;
	}
	public IType getPackage1Class1() {
		return this.package1Class1;
	}
	public ICompilationUnit getPackage1Class1CU() {
		return this.package1Class1.getCompilationUnit();
	}
	public IPackageFragment getPackage1Package2() {
		return this.package1package2;
	}
	public IType getPackage1Package2Class1() {
		return this.package1package2Class1;
	}
	public ICompilationUnit getPackage1Package2Class1CU() {
		return this.package1package2Class1.getCompilationUnit();
	}
	public IType getPackage1Package2Class2() {
		return this.package1package2Class2;
	}
	public ICompilationUnit getPackage1Package2Class2CU() {
		return this.package1package2Class2.getCompilationUnit();
	}
}
