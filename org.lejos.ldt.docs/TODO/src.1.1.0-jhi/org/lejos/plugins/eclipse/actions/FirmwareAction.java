package org.lejos.plugins.eclipse.actions;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
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
 * Insert the type's description here.
 * @see IWorkbenchWindowActionDelegate
 */
public class FirmwareAction implements IEditorActionDelegate {
	
	private IEditorPart editor;
	
	/**
	 * The constructor.
	 */
	public FirmwareAction() {
	}

	/**
	 * Insert the method's description here.
	 * @see IWorkbenchWindowActionDelegate#run
	 */
	public void run(IAction action)  {
		 try {
    		LejosFirmwareRunner op = new LejosFirmwareRunner();
		    new ProgressMonitorDialog(editor.getSite().getShell()).run(true, true, op);
		    
         	if (op.hasError()) {		 
         		String msg="RCX Firmware Download Error";
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


	// for now this will only handle fast download and rely on stderr output
	// possible download failures are returned through hasError() and getError()
	class LejosFirmwareRunner implements IRunnableWithProgress {
		
		private String error=null;
		
		boolean hasError(){
		  return error!=null;
		}
		
		String getError() {
		  return error;
		}
		
		public void run(IProgressMonitor monitor) {
 
	        try {	        	
	        	File file=new File(LejosPlugin.getDefault().getLejosPath(),"bin/lejosfirmdl");
				int port=LejosPlugin.getDefault().getLejosPort();
	        	String sport=LejosPlugin.getPort(port);
	        	boolean isFast=LejosPlugin.getDefault().getLejosIsFast();
	        	String smode;
	        	if (isFast) smode=" -f"; else smode=" -s";
	        	String cmd=FileUtilities.getAbsolutePath(file)+smode+" --tty="+sport;
//LejosPlugin.debug("cmd: "+cmd);
    	        Process p=Runtime.getRuntime().exec(cmd);
        	  
            	BufferedReader err=new BufferedReader(new InputStreamReader(p.getErrorStream()));
            	BufferedReader out=new BufferedReader(new InputStreamReader(p.getInputStream()));
            	String s;
            	int pos;
            	int opg=0;
            	int npg=0;
            	boolean fast=false;
            	StringBuffer msg=new StringBuffer();
                                        
		  	  	while(true) {
		  	  		if (monitor.isCanceled()) {
		  	  			p.destroy();
		  	  			try {
			  	  			p.waitFor();
		  	  			} catch(InterruptedException e) {
		  	  			}
   	  	  			  	break;
		  	  		}
		  	  		
		  	  		
			    	s=err.readLine();
				    if (s==null) break;
				    
				    pos=s.indexOf("Transferring");
				    if (pos!=-1) {
				      npg=0;
			    	  monitor.beginTask(s,100);
			    	  continue;
				    }
				    
				    pos=s.indexOf("%");
				    if (pos==-1) {
				      s=s.trim();
				      if (s.length()>0) msg.append(s+"\n");
				      continue;
				    }
				    
				    try {
				      	npg=Integer.parseInt(s.substring(0,pos).trim());
				      	monitor.worked(npg-opg);
				      	monitor.subTask(s+" completed");
				      	opg=npg;
				    } catch(NumberFormatException e) {
				    }				    
			  	}
			  				  	
		  		monitor.done();
		  		
		  		// sth is wrong
		  		if (npg!=100) {
	 				error=msg.toString();
		  		}
		  		
	        } catch(IOException e) {
	        	error=e.getMessage();
	        }
		}

	}	
	
}
