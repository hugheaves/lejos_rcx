package org.lejos.tools.eclipse.plugin;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.*;
import org.lejos.tools.api.*;

/**
 * The <code>EclipseToolsetFacade</code> provides the services of the
 * <code>IRuntimeToolset</code> based on Eclipse interfaces.
 * 
 * @author <a href="mailto:jochen.hiller@t-online.de">Jochen Hiller </a>
 */
public class EclipseToolsetFacade
{
  // attributes

  /**
   * a progress monitor to use
   */
  private IProgressMonitorToolset progressMonitor;

  // public methods

  /**
   * Sets a progress monitor for the further actions.
   * 
   * @param aMonitor a progress monitor
   */
  public void setProgressMonitor (IProgressMonitorToolset aMonitor)
  {
    this.progressMonitor = aMonitor;

  }

  /**
   * Link one compilation unit with the given preferences
   * 
   * @param aCompilationUnit the compulation unit
   * @param aPreferences the lejos preferences
   * @throws JavaModelException will be raised, if some internal model error
   *           occured
   * @throws ToolsetException will be raised if some error occurs during calling
   *           the leJOS toolset
   */
  public void linkCU (ICompilationUnit aCompilationUnit,
      LejosPreferences aPreferences) throws ToolsetException,
      JavaModelException
  {
    ToolsetFactory factory = ToolsetFactory.newInstance();
    IRuntimeToolset toolset = factory.newRuntimeToolset();
    if (this.progressMonitor != null)
    {
      toolset.setProgressMonitor(this.progressMonitor);
    }

    // prepare all arguments
    File outputFile = EclipseUtilities.getOutputFile(aCompilationUnit,
        aPreferences.getExtensionBinary());
    int linkMethod = aPreferences.getLinkMethod();

    // only create signature, if specified in preferences
    boolean createSignatureFile = aPreferences.isCreateSignatureFile();
    String[] classNames = new String[1];
    classNames[0] = EclipseUtilities.getFQCN(aCompilationUnit);
    String[] args = new String[0]; // no args in eclipse

    // get the classpath from Eclipse
    String classpath = EclipseUtilities.getOutputFolder(aCompilationUnit)
        .toString()
        + File.pathSeparator;
    String[] lejosLibs = aPreferences.getDefaultClasspathEntries();
    try
    {
      for (int i = 0; i < lejosLibs.length; i++)
      {
        IPath lejosLibPath = new Path(lejosLibs[i]);
        Path absoluteLibPath = EclipseUtilities.findFileInPlugin("org.lejos",
            lejosLibPath.toString());
        classpath = classpath + absoluteLibPath.toString() + File.pathSeparator;
      }
    }
    catch (IOException ex)
    {
      throw new ToolsetException("Could not create CLASSPATH for link: ", ex);
    }

    // finally, call the link process
    StringBuffer sbuf = new StringBuffer();
    sbuf.append("Linking ");
    for (int i = 0; i < classNames.length; i++)
    {
      if (i > 0)
      {
        sbuf.append(", ");
      }
      sbuf.append(classNames[i]);
    }
    sbuf.append(" to " + outputFile.toString());
    LejosPlugin.debug(sbuf.toString());
    toolset.link(outputFile, linkMethod, createSignatureFile, classpath,
        classNames, args);
  }

  /**
   * Link an array of java elements.
   * 
   * <p>
   * Will collect all classed for this java element, and link every compilation
   * unit.
   * </p>
   * 
   * @param aJavaElems an array with java elements
   * @param aPreferences the lejos preferences
   * @throws JavaModelException will be raised, if some internal model error
   *           occured
   * @throws ToolsetException will be raised if some error occurs during calling
   *           the leJOS toolset
   */
  public void linkJavaElement (IJavaElement[] aJavaElems,
      LejosPreferences aPreferences) throws ToolsetException,
      JavaModelException
  {

    for (int i = 0; i < aJavaElems.length; i++)
    {
      linkJavaElement(aJavaElems[i], aPreferences);
    }
  }

  /**
   * Link a single java element.
   * 
   * <p>
   * Will collect all classed for this java element, and link every compilation
   * unit.
   * </p>
   * 
   * @param aJavaElem the java element
   * @param aPreferences the lejos preferences
   * @throws JavaModelException will be raised, if some internal model error
   *           occured
   * @throws ToolsetException will be raised if some error occurs during calling
   *           the leJOS toolset
   */
  public void linkJavaElement (IJavaElement aJavaElem,
      LejosPreferences aPreferences) throws ToolsetException,
      JavaModelException
  {
    ICompilationUnit[] cus = EclipseUtilities.collectLinkClasses(aJavaElem);

    for (int i = 0; i < cus.length; i++)
    {
      linkCU(cus[i], aPreferences);
    }
  }

  /**
   * Count the number of compilation units for a list of java elements.
   * 
   * @param elems an array of java elements
   * @return the number of compilation units
   * @throws JavaModelException will be raised in any error case within the java
   *           model
   */
  public int countCU (IJavaElement[] elems) throws JavaModelException
  {
    int n = 0;
    for (int i = 0; i < elems.length; i++)
    {
      IJavaElement elem = elems[i];
      ICompilationUnit[] cus = EclipseUtilities.collectLinkClasses(elem);
      n = n + cus.length;
    }
    return n;
  }

  ///////////////////////////////////////////////////////////////////////////////////////////////////////
  /**
   * compiles an array of java elements
   * 
   * @author <a href="mailto:mp.scholz@t-online.de">Matthias Paul Scholz </a>
   * @param aJavaElems an array with java elements
   * @param aPreferences the lejos preferences
   * @throws JavaModelException will be raised, if some internal model error
   *           occured
   * @throws ToolsetException will be raised if some error occurs during call of
   *           the leJOS toolset
   */
  public void compileJavaElements (IJavaElement[] aJavaElems,
      LejosPreferences aPreferences) throws ToolsetException,
      JavaModelException
  {
    for (int i = 0; i < aJavaElems.length; i++)
    {
      // compile each element
      compileJavaElement(aJavaElems[i], aPreferences);
    } //for
  } // compileJavaElements()

  ///////////////////////////////////////////////////////////////////////////////////////////////////////
  /**
   * compiles a single java element.
   * 
   * @author <a href="mailto:mp.scholz@t-online.de">Matthias Paul Scholz </a>
   * @param aJavaElem the java element
   * @param aPreferences the lejos preferences
   * @throws JavaModelException will be raised, if some internal model error
   *           occured
   * @throws ToolsetException will be raised if some error occurs during call of
   *           the leJOS toolset
   */
  public void compileJavaElement (IJavaElement aJavaElem,
      LejosPreferences aPreferences) throws ToolsetException,
      JavaModelException
  {
    // get source files to compile
    ICompilationUnit[] cus = EclipseUtilities.collectLinkClasses(aJavaElem);
    // compile them
    for (int i = 0; i < cus.length; i++)
    {
      // compile compilation unit
      compileCU(cus[i], aPreferences);
    } //for
  } //compileJavaElement()

  ///////////////////////////////////////////////////////////////////////////////////////////////////////
  /**
   * compiles a compilation unit with the given preferences <br>
   * uses the com.sun.tools.javac.main class
   * 
   * @author <a href="mailto:mp.scholz@t-online.de">Matthias Paul Scholz </a>
   * @param aCompilationUnit the compulation unit
   * @param aPreferences the lejos preferences
   * @throws JavaModelException will be raised, if some internal model error
   *           occured
   * @throws ToolsetException will be raised if some error occurs during call of
   *           the leJOS toolset
   */
  public void compileCU (ICompilationUnit aCompilationUnit,
      LejosPreferences aPreferences) throws ToolsetException,
      JavaModelException
  {
    // set progress monitor at need
    ToolsetFactory factory = ToolsetFactory.newInstance();
    IRuntimeToolset toolset = factory.newRuntimeToolset();
    if (this.progressMonitor != null)
    {
      toolset.setProgressMonitor(this.progressMonitor);
    } // if
    // create compiler arguments
    //TODO set additional arguments from preferences (?)
    String[] arguments = new String[6];
    arguments[0] = "-bootclasspath";
    String[] lejosLibs = aPreferences.getDefaultClasspathEntries();
    // tools.jar of SUN's compiler
    String javaHome = System.getProperty("java.home");
    if (javaHome == null)
      throw new ToolsetException("System property java.home could not be read");
    String libs = javaHome + File.pathSeparator + "lib" + File.pathSeparator
        + "tools.jar" + File.pathSeparator;
    // leJOS libs
    try
    {
      for (int i = 0; i < lejosLibs.length; i++)
      {
        IPath lejosLibPath = new Path(lejosLibs[i]);
        Path absoluteLibPath = EclipseUtilities.findFileInPlugin("org.lejos",
            lejosLibPath.toString());
        libs += absoluteLibPath.toString() + File.pathSeparator;
      } //for
      arguments[1] = libs;
    }
    catch (IOException ex)
    {
      throw new ToolsetException(
          "Could not create CLASSPATH for compilation: ", ex);
    } // catch
    // target 1.1
    arguments[2] = "-target";
    arguments[3] = "1.1";
    // output directory
    arguments[4] = "-d";
    arguments[5] = EclipseUtilities.getOutputFolder(aCompilationUnit)
        .toString();
    // get name & packages of source file
    String javaSource = EclipseUtilities.getAbsoluteLocationForResource(
        aCompilationUnit.getJavaProject().getProject(),
        aCompilationUnit.getPath()).getAbsolutePath();
    String[] sourceFile =
    {javaSource};
    // now compile via toolset
    StringBuffer debug = new StringBuffer("compiling: ");
    for (int i = 0; i < arguments.length; i++)
      debug.append(arguments[i] + " ");
    LejosPlugin.debug(debug.toString());
    toolset.compile(sourceFile, arguments);
  } // compileCU()

  /**
   * Install firmware.
   */
  public void installFirmware () throws ToolsetException
  {
    IRuntimeToolset toolset = createToolset();
    // TODO get port and fas mode from preferences
    toolset.installFirmware("usb", true);
  }

  /**
   * Dump binary.
   * 
   * @param classdir dir with all classes
   * @param classname main class
   * @param stream output stream to write binary to
   * @throws ToolsetException
   */
  public void link (String classdir, String classname, OutputStream stream)
      throws ToolsetException
  {
    IRuntimeToolset toolset = createToolset();
    toolset.link(classdir, classname, false, stream, true);
  }
  
  /**
   * 
   */
  protected IRuntimeToolset createToolset ()
  {
    // set progress monitor at need
    ToolsetFactory factory = ToolsetFactory.newInstance();
    IRuntimeToolset result = factory.newRuntimeToolset();
    if (progressMonitor != null)
    {
      result.setProgressMonitor(progressMonitor);
    }
    
    assert result != null : "Postconditon: result != null";
    return result;
  }
}