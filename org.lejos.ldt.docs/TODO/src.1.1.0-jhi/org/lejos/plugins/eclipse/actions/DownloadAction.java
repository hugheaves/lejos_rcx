package org.lejos.plugins.eclipse.actions;


import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageDeclaration;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;
import org.lejos.plugins.eclipse.LejosPlugin;
import org.lejos.plugins.eclipse.util.FileUtilities;


/**
 * Action to download the selected class to RCX or Emulator.
 * 
 * @author Christoph Ponsard
 * @author <a href="mailto:jochen.hiller@t-online.de">Jochen Hiller</a>
 */
public class DownloadAction implements IEditorActionDelegate {  //, IEditorActionDelegate, IViewActionDelegate {

	private IEditorPart editor;
	
	/**
	 * The constructor.
	 */
	public DownloadAction() {
	}

	/**
	 * Insert the method's description here.
	 * @see IWorkbenchWindowActionDelegate#run
	 */
	public void run(IAction action)  {
		 try {	 	
    		LejosDownloadRunner op = new LejosDownloadRunner();
		    new ProgressMonitorDialog(editor.getSite().getShell()).run(true, true, op);
		    
         	if (op.hasError()) {		 
         		String msg="RCX Byte Code Download Error";
  	 	 		ErrorDialog.openError(editor.getSite().getShell(), msg, msg,
									  new Status(IStatus.ERROR, "org.RCX", 0, op.getError(), null));
         	}		    
		 } catch (InvocationTargetException e) {
		 } catch (InterruptedException e) {
		 }	
         	
	}

	/**
	 * Insert the method's description here.
	 * @see IWorkbenchWindowActionDelegate#selectionChanged
	 */
	public void selectionChanged(IAction action, ISelection selection)  {
	}

	public void setActiveEditor(IAction action, IEditorPart targetEditor)  {
		editor=targetEditor;
	}

	
//	public void init(IViewPart part) {
//	}
	
//	public void setActiveEditor(IAction action, IEditorPart part) {
//	}
	
	
	// for now this will only handle fast download and rely on stderr output
	// possible download failures are returned through hasError() and getError()
	class LejosDownloadRunner implements IRunnableWithProgress {
		
		private String error;
		
		boolean hasError(){
		  return error!=null;
		}
		
		String getError() {
		  return error;
		}
			
		public void run(IProgressMonitor monitor) {
			// no error
			error=null;
			
			String home=LejosPlugin.getDefault().getLejosPath();
/* 
 			// properties for class filtering
 			System.setProperty("tinyvm.write.order","BE");
			System.setProperty("tinyvm.home",home);
				
			//  excerpts of the scripts used for the JPadPro interface
	var command="\""+interpreter.path+"\" -Dtinyvm.write.order=BE -Dtinyvm.home=\""+rcx_home+"\""+
			      " -classpath \""+rcx_home+"\\lib\\jtools.jar\" js.tinyvm.TinyVM -classpath \""+
			      file.directory+";"+rcx_home+"\\lib\\classes.jar;"+rcx_cp+"\" "+file.title+" "+verbose+" -o \""+rcx_temp+"\\"+file.title+".bin\"";
*/						
								
//	 command="\""+rcx_home+"\\bin\\lejosrun\" \""+rcx_temp+"\\"+file.title+".bin\"";

	        try {
	        	//==== STEP 1: building bundle file (not progress-monitored)
	
				// 1.1 computing package name
	        	ICompilationUnit cu=LejosPlugin.getDefault().getCurrentCompilationUnit();
	        	IPackageDeclaration[] pk=cu.getPackageDeclarations();
	        	String code;
	        	if (pk.length==0)
	        		code=cu.getElementName();
        		else
	        		code=pk[0].getElementName()+"."+cu.getElementName();
				int pcode=code.lastIndexOf(".java");
				code=code.substring(0,pcode);
//LejosPlugin.debug.println("CODE:  "+code);

	        	// 1.2 installation workspace	
	        	IFile fmain=LejosPlugin.getDefault().getCurrentJavaFile();
				String main=FileUtilities.getAbsolutePath(fmain);

				String prel=FileUtilities.getAbsolutePath(fmain.getFullPath());
				int pdir=main.indexOf(prel);
				if (pdir==-1) throw new IOException("Not a Java File: "+main);
				String dir=main.substring(0,pdir);

				// 1.3 appending output location
				IJavaProject prj=cu.getJavaProject();
				IPath pol=prj.getOutputLocation();		
				String ol=FileUtilities.getAbsolutePath(pol);
				dir=FileUtilities.getAbsolutePath(new File(dir,ol.toString()));
//LejosPlugin.debug.println("DIR:  "+dir);
										        	
	        	// 1.4 filtering and assembling the bundle
	        	IPath resPath = FileUtilities.getLinkFileForCompilationUnit(cu);
	        	File outputFile = FileUtilities.getAbsoluteLocationForResource(
	        		cu.getJavaProject().getProject(), resPath
	        	);

LejosPlugin.debug(outputFile.getAbsolutePath());

				// cannot directly js.tinyvm.TinyVM.main(args) because 
				// error are triggering System.exit code 
				// could be solved using security manager
/*
				File lib=new File(LejosPlugin.getDefault().getLejosPath(),"lib/classes.jar");
				String sep=System.getProperty("path.separator");
	        	String[] args=new String[] { 	"-classpath",
	        									dir+sep+lib.getAbsolutePath(),
	        									code,
	        									"-verbose",
	        									"-o",
	        									tmp.getAbsolutePath() };



				} catch(Throwable e) {
					error=e.getMessage();
					return;
				}

*/

				IVMInstall vmInstall= JavaRuntime.getDefaultVMInstall();
				File jvm=new File(vmInstall.getInstallLocation(),"bin/java");
				File cflib=new File(home,"lib/jtools.jar");
				File rtlib=new File(home,"lib/classes.jar");				
				String sep=System.getProperty("path.separator");

				String cmd=FileUtilities.getAbsolutePath(jvm)+
		   					" -Dtinyvm.write.order=BE -Dtinyvm.home="+home+
						   	" -cp "+FileUtilities.getAbsolutePath(cflib)+" js.tinyvm.TinyVM"+
		   					" -classpath "+dir+sep+FileUtilities.getAbsolutePath(rtlib)+" "+
		   					code+" -o "+FileUtilities.getAbsolutePath(outputFile.getName());
LejosPlugin.debug("cmd1: "+cmd);
//LejosPlugin.debug("tmpo: "+tmpo);
				monitor.beginTask("Filtering classes",100);
    	        Process p=Runtime.getRuntime().exec(cmd);
            	BufferedReader err=new BufferedReader(new InputStreamReader(p.getErrorStream()));
            	BufferedReader out=new BufferedReader(new InputStreamReader(p.getErrorStream()));
            	String s;
            	StringBuffer msg=new StringBuffer();

		  	  	while(true) {
		  	  		if (monitor.isCanceled()) {
		  	  			p.destroy();
		  	  			try {
			  	  			p.waitFor();
			  	  			return;
		  	  			} catch(InterruptedException e) {
		  	  			}
   	  	  			  	break;
		  	  		}
		  	  		
			    	s=err.readLine();
				    if (s==null) break;
//LejosPlugin.debug("--> "+s);
	
					s=s.trim();
					if (s.length()>0) msg.append(s+"\n");
		  	  	}
		  	  	monitor.done();
		  	  			  	  	
		  	  	if (msg.length()>0) {
		  	  		error=msg.toString();
		  	  	 	return;
		  	  	}
				
				//===== STEP 2: download (progress-monitored)
	        	File lejosrun=new File(LejosPlugin.getDefault().getLejosPath(),"bin/lejosrun");
	        	int port=LejosPlugin.getDefault().getLejosPort();
	        	String sport=LejosPlugin.getPort(port);
	        	boolean isFast=LejosPlugin.getDefault().getLejosIsFast();
	        	String smode;
	        	if (isFast) smode=" -f"; else smode=" -s";
	        	cmd=FileUtilities.getAbsolutePath(lejosrun)+smode+" --tty="+sport+" "+FileUtilities.getAbsolutePath(outputFile.getName());

//LejosPlugin.debug("cmd2: "+cmd);

    	        p=Runtime.getRuntime().exec(cmd);
    	            	      
            	err=new BufferedReader(new InputStreamReader(p.getErrorStream()));
            	out=new BufferedReader(new InputStreamReader(p.getInputStream()));
            	int pos;
            	int opg=100;
            	int npg=0;
            	boolean fast=false;
            	msg=new StringBuffer();
                                        
		  	  	while(true) {
		  	  		if (monitor.isCanceled()) {
		  	  			p.destroy();
		  	  			try {
			  	  			p.waitFor();
			  	  			error="User interrupted - please check RCX state before downloading again";
			  	  			return;
		  	  			} catch(InterruptedException e) {
		  	  			}
   	  	  			  	break;
		  	  		}
		  	  				  	  		
			    	s=err.readLine();
				    if (s==null) break;				    			    
				    				    
				    pos=s.indexOf("%");
				    if (pos==-1) {
				      s=s.trim();
				      if (s.length()>0) msg.append(s+"\n");
				      continue;
				    }
				    
				    try {
				    	npg=Integer.parseInt(s.substring(0,pos).trim());
				    	if (opg>npg) {
				      		npg=0;
			    	  		monitor.beginTask("Downloading byte-code",100);			    	  		
				    	} else {				    					      	
				      		monitor.worked(npg-opg);
				      		monitor.subTask(npg+"% completed");
				    	}
				    	opg=npg;		
				    } catch(NumberFormatException e) {
				    }				    
			  	}
			  				  	
		  		monitor.done();
		  		
		  		// sth is wrong
		  		if (npg!=100) {
	 				error=msg.toString();
		  		}
		  		
	        } catch(Exception e) {
	        	error=e.getMessage().trim();
//	        	e.printStackTrace();
	        }
       		if (error.length()==0) error="Unknown - probably interrupted or firmware not present";
		}		

	}		
	
}