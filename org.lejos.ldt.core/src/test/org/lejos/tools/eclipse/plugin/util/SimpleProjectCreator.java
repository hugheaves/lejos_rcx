package org.lejos.tools.eclipse.plugin.util;

import java.io.File;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.swt.widgets.Shell;
import org.lejos.tools.eclipse.plugin.EclipseUtilities;
import org.lejos.tools.eclipse.plugin.LejosPlugin;

/**
 * Helper class to create and compiler a simple project.
 * 
 * @author <a href="mailto:jochen.hiller@t-online.de">Jochen Hiller</a>
 */
public class SimpleProjectCreator {

	// attributes

	private IJavaProject javaProject;
	private IPackageFragmentRoot sourceFolder;
	private IPackageFragment package1;
	private IPackageFragment package2;
	private ICompilationUnit package2Class1;
	private ICompilationUnit package2Class2;
	private ICompilationUnit package1Class1;
	
	// public methods

	public void createSimpleProject() throws CoreException {
		this.javaProject =
			JavaProjectHelper.createJavaProject(
				"simpleproject1",
				"build/classes");

		JavaProjectHelper.setAutoBuilding(true);
		// enforce compile with 1.1 class format
		this.javaProject.setOption(
			JavaCore.COMPILER_CODEGEN_TARGET_PLATFORM,
			JavaCore.VERSION_1_1);

		// add the lejos classes.zip
		IPath classesZip = new Path("lejos/lib/classes.zip");
		// have to convert to an absolute path. Better way ?
		File classesZipFile =
			LejosPlugin.getDefault().getFileInPlugin(classesZip);
		classesZip = new Path(classesZipFile.toString());
		// add this as runtime library to make compilable
		JavaProjectHelper.addLibrary(javaProject, classesZip);

		this.sourceFolder =
			JavaProjectHelper.addSourceContainer(javaProject, "src");

		this.package1 =
			this.sourceFolder.createPackageFragment("package1", false, null);
		StringBuffer buf;
		buf = new StringBuffer();
		buf.append("package package1;\n");
		buf.append("\n");
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
			this.package1.createCompilationUnit(
				"Class1.java",
				buf.toString(),
				false,
				null);

		this.package2 =
			this.sourceFolder.createPackageFragment(
				"package1.package2",
				false,
				null);
		buf = new StringBuffer();
		buf.append("package package1.package2;\n");
		buf.append("\n");
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
		this.package2Class1 =
			this.package2.createCompilationUnit(
				"Class1.java",
				buf.toString(),
				false,
				null);

		buf = new StringBuffer();
		buf.append("package package1.package2;\n");
		buf.append("\n");
		buf.append("public class Class2 {\n");
		buf.append("}\n");

		this.package2Class2 =
			this.package2.createCompilationUnit(
				"Class2.java",
				buf.toString(),
				false,
				null);

		try {
			// give auto build a chance to run
			Thread.sleep(5000);
		} catch (InterruptedException ex) {
			// ignore
		}

		// project should be compiled automatically
		boolean hasBeenBuild = javaProject.hasBuildState();
		// assertTrue(hasBeenBuild);
		// TODO but, sometimes it has NOT been compiled
		if (!hasBeenBuild) {

			Shell shell = new Shell();
			IProject myProject = javaProject.getProject();
			ProgressMonitorDialog dialog = new ProgressMonitorDialog(shell);
			dialog.open();
			IProgressMonitor monitor = dialog.getProgressMonitor();
			try {
				monitor.beginTask("hello", 100);
				myProject.build(IncrementalProjectBuilder.FULL_BUILD, monitor);
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// ignore
			} finally {
				monitor.done();
			}
		}

		// now check, if Class1.class now really exists
		File classFile =
			EclipseUtilities.getOutputFile(this.package2Class1, ".class");
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

	public void deleteProject(IJavaProject fProject) throws CoreException {
		JavaProjectHelper.delete(fProject);
	}
	public IJavaProject getJavaProject() {
		return this.javaProject;
	}
	public IPackageFragmentRoot getSourceFolder() {
		return this.sourceFolder;
	}
	public IPackageFragment getPackage1() {
		return this.package1;
	}
	public IPackageFragment getPackage2() {
		return this.package2;
	}
	public ICompilationUnit getCUPackage1Class1() {
		return this.package1Class1;
	}
	public ICompilationUnit getCUPackage2Class1() {
		return this.package2Class1;
	}
	public ICompilationUnit getCUPackage2Class2() {
		return this.package2Class2;
	}
}
