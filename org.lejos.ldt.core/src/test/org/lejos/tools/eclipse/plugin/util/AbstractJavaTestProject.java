/*******************************************************************************
 * Copyright (c) 2003 IBM Corporation and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Common Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors: Erich Gamma (erich_gamma@ch.ibm.com) and Kent Beck
 * (kent@threeriversinstitute.org)
 ******************************************************************************/
package org.lejos.tools.eclipse.plugin.util;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceDescription;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.swt.widgets.Shell;
import org.osgi.framework.Bundle;

/**
 * Abstract class for Java test projects.
 * 
 * <p>
 * Most parts of the code are taken from the book from Erich Gamma / Kent Beck:
 * <b>Contributing to Eclipse </b>, from the class <code>TestProject</code>.
 * </p>
 * <p>
 * The class has been adopted for usage within any java project.
 * </p>
 * 
 * @author <a href="mailto:jochen.hiller@t-online.de">Jochen Hiller </a>
 */
public abstract class AbstractJavaTestProject
{

   // attributes

   protected IProject project;

   protected IJavaProject javaProject;

   protected IPackageFragmentRoot sourceFolder;

   // constructors

   public AbstractJavaTestProject ()
   {}

   // abstract methods

   public abstract void createProject () throws Exception;

   // public methods

   public void createProject (String projectName) throws CoreException
   {
      IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
      project = root.getProject(projectName);
      project.create(null);

      project.open(null);

      javaProject = JavaCore.create(project);
      setJavaNature();

      javaProject.setRawClasspath(new IClasspathEntry[0], null);
   }

   public IProject getProject ()
   {
      return project;
   }

   public IJavaProject getJavaProject ()
   {
      return javaProject;
   }

   public void addJar (String plugin, String jar) throws MalformedURLException,
      IOException, JavaModelException
   {
      Path result = findFileInPlugin(plugin, jar);
      IClasspathEntry[] oldEntries = javaProject.getRawClasspath();
      IClasspathEntry[] newEntries = new IClasspathEntry[oldEntries.length + 1];
      System.arraycopy(oldEntries, 0, newEntries, 0, oldEntries.length);
      newEntries[oldEntries.length] = JavaCore.newLibraryEntry(result, null,
         null);
      javaProject.setRawClasspath(newEntries, null);
   }

   public IPackageFragment createPackage (String name) throws CoreException
   {
      if (sourceFolder == null)
         sourceFolder = createSourceFolder("src");
      return sourceFolder.createPackageFragment(name, false, null);
   }

   public IType createType (IPackageFragment pack, String cuName, String source)
      throws JavaModelException
   {
      StringBuffer buf = new StringBuffer();
      buf.append("package " + pack.getElementName() + ";\n");
      buf.append("\n");
      buf.append(source);
      ICompilationUnit cu = pack.createCompilationUnit(cuName, buf.toString(),
         false, null);
      return cu.getTypes()[0];
   }

   public void dispose () throws CoreException
   {
      waitForIndexer();
      project.delete(true, true, null);
   }

   public boolean hasBeenBuild ()
   {
      return this.javaProject.hasBuildState();
   }

   public void buildProject () throws CoreException
   {
      if (!hasBeenBuild())
      {
         Shell shell = new Shell();
         ProgressMonitorDialog dialog = new ProgressMonitorDialog(shell);
         dialog.open();
         IProgressMonitor monitor = dialog.getProgressMonitor();
         try
         {
            monitor.beginTask("Building project", 100);
            this.project.build(IncrementalProjectBuilder.FULL_BUILD, monitor);
         }
         finally
         {
            monitor.done();
         }
         dialog.close();
      }
   }

   // protected methods

   protected IFolder createFolder (String folderName) throws CoreException
   {
      IFolder binFolder = project.getFolder(folderName);
      binFolder.create(false, true, null);
      return binFolder;
   }

   protected void setJavaNature () throws CoreException
   {
      IProjectDescription description = project.getDescription();
      description.setNatureIds(new String[]
      {
         JavaCore.NATURE_ID
      });
      project.setDescription(description, null);
   }

   protected void createOutputFolder (IFolder binFolder)
      throws JavaModelException
   {
      IPath outputLocation = binFolder.getFullPath();
      javaProject.setOutputLocation(outputLocation, null);
   }

   protected IPackageFragmentRoot createSourceFolder (String srcFolder)
      throws CoreException
   {
      IFolder folder = project.getFolder(srcFolder);
      folder.create(false, true, null);
      IPackageFragmentRoot root = javaProject.getPackageFragmentRoot(folder);

      IClasspathEntry[] oldEntries = javaProject.getRawClasspath();
      IClasspathEntry[] newEntries = new IClasspathEntry[oldEntries.length + 1];
      System.arraycopy(oldEntries, 0, newEntries, 0, oldEntries.length);
      newEntries[oldEntries.length] = JavaCore.newSourceEntry(root.getPath());
      javaProject.setRawClasspath(newEntries, null);
      return root;
   }

   protected void addSystemLibraries () throws JavaModelException
   {
      IClasspathEntry[] oldEntries = javaProject.getRawClasspath();
      IClasspathEntry[] newEntries = new IClasspathEntry[oldEntries.length + 1];
      System.arraycopy(oldEntries, 0, newEntries, 0, oldEntries.length);
      newEntries[oldEntries.length] = JavaRuntime.getDefaultJREContainerEntry();
      javaProject.setRawClasspath(newEntries, null);
   }

   /**
    * Sets autobuilding state for the test workspace.
    */
   protected void setAutoBuilding (boolean state) throws CoreException
   {
      IWorkspace workspace = ResourcesPlugin.getWorkspace();
      IWorkspaceDescription desc = workspace.getDescription();
      desc.setAutoBuilding(state);
      workspace.setDescription(desc);
   }

   protected Path findFileInPlugin (String pluginId, String fileName)
      throws MalformedURLException, IOException
   {
      // get the bundle and its location
      Bundle theBundle = Platform.getBundle(pluginId);
      String theBundleLocation = theBundle.getLocation();

      // get an entry in bundle as URL, will return bundleentry://nnn/...
      // resolve the entry as an URL, typically file://...
      URL theFileAsEntry = theBundle.getEntry(fileName);
      URL resEntry = Platform.resolve(theFileAsEntry);

      // convert from URL to an IPath
      Path thePath = new Path(new File(resEntry.getFile()).getAbsolutePath());
      return thePath;
   }

   protected void waitForIndexer () throws JavaModelException
   {
   // removed for the moment in Eclipse 3
   // probably see book Gamma/Beck
   }
}