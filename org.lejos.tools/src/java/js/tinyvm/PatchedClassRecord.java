package js.tinyvm;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import js.classfile.JCPE_Class;
import js.classfile.JCPE_InterfaceMethodref;
import js.classfile.JCPE_Methodref;
import js.classfile.JCPE_NameAndType;
import js.classfile.JClassFile;
import js.classfile.JConstantPool;
import js.classfile.JConstantPoolEntry;
import js.classfile.JMethod;

/**
 * Abstraction for a class record (see vmsrc/language.h).
 */
public class PatchedClassRecord extends ClassRecord {

	public static ClassRecord patchedGetClassRecord (String aName, ClassPath aCP,
	                                            Binary aBinary)
	  throws Exception
	  {
	    InputStream pIn = aCP.getInputStream (aName);
	    if (pIn == null)
	    {
	      Assertion.fatal ("Class " + aName.replace ('/', '.') + 
	        " (file " + aName + 
	        ".class) not found in CLASSPATH: " + aCP);
	    }
	    ClassRecord pCR = new PatchedClassRecord();
        pCR.iBinary = aBinary;
	    pCR.iCF = new JClassFile();
	    pCR.iName = aName;
	    InputStream pBufIn = new BufferedInputStream (pIn, 4096);
	    try {
	      pCR.iCF.read (pBufIn);
	    } catch (Throwable t) {
	      System.err.println ("Exception reading " + aName);
	      t.printStackTrace();
	      System.exit(1);
	    }
	    pBufIn.close();
	    return pCR;
	  }

	public void storeMethods (RecordTable aMethodTables,
	                            RecordTable aExceptionTables, 
	                            HashVector aSignatures,
	                            boolean aAll,
							    PrintWriter writer)
	  {
	    Assertion.trace ("Processing methods in " + iName);
	    Enumeration pEntries = iCF.getMethods().elements();
	    while (pEntries.hasMoreElements())
	    {
	      JMethod pMethod = (JMethod) pEntries.nextElement();
	      Signature pSignature = new Signature (pMethod.getName(), 
	                                         pMethod.getDescriptor());
	      String meth = pMethod.getName() + ":" +pMethod.getDescriptor();
	
	      if (aAll || iUseAllMethods || iUsedMethods.indexOf(meth) >= 0 || 
	          pMethod.getName().substring(0,1).equals("<") || meth.equals("run:()V")) {
	        //System.out.println("Adding Method " + meth + " for class " + iName + " length " + iName.length() + " used " + iUsedMethods.indexOf(meth)); 
	        MethodRecord pMethodRecord = new MethodRecord (pMethod, pSignature, 
	          this, iBinary, aExceptionTables, aSignatures);
	        iMethodTable.add (pMethodRecord);
	        iMethods.put (pSignature, pMethodRecord);
	      } else {
			// Assertion.verbose(1, "Omitting " + meth + " for class " + iName);
	      	writer.println("Omitting " + meth + " for class " + iName);
	      }
	    }
	    aMethodTables.add (iMethodTable);
	  }

	public void storeReferredClasses (Hashtable aClasses, RecordTable aClassRecords, ClassPath aClassPath, Vector aInterfaceMethods)
	  throws Exception
	  {
	    Assertion.trace ("Processing CONSTANT_Class entries in " + iName);
	    JConstantPool pPool = iCF.getConstantPool();
	    Enumeration pEntries = pPool.elements();
	    while (pEntries.hasMoreElements())
	    {
	      JConstantPoolEntry pEntry = (JConstantPoolEntry) pEntries.nextElement();
	      //Utilities.trace ("  " + pEntry.getClass().getName() + ": " + pEntry);
	      if (pEntry instanceof JCPE_Class)
	      {
	        String pClassName = ((JCPE_Class) pEntry).getName();
	        if (pClassName.startsWith ("["))
		{
	          Assertion.trace ("Skipping array: " + pClassName);
	          continue;
		}
	        if (aClasses.get (pClassName) == null)
		{
	          ClassRecord pRec = PatchedClassRecord.patchedGetClassRecord (pClassName, 
	                             aClassPath, iBinary);
	          aClasses.put (pClassName, pRec);
	          aClassRecords.add (pRec);
		}
	      } else if (pEntry instanceof JCPE_Methodref) {
	        // System.out.println(iName + " calls " + pEntry); 
	        JCPE_Class pClass = ((JCPE_Methodref) pEntry).getClassEntry();
	        ClassRecord pClassRec = (ClassRecord) aClasses.get(pClass.getName());
	        if (pClassRec == null) {
	          pClassRec = PatchedClassRecord.getClassRecord (pClass.getName(), 
	                             aClassPath, iBinary);
	          aClasses.put (pClass.getName(), pClassRec);
	          aClassRecords.add (pClassRec);
	        }
	        pClassRec.addUsedMethod(((JCPE_Methodref) pEntry).getNameAndType().getName()+":"+((JCPE_Methodref) pEntry).getNameAndType().getDescriptor());
	      } else if (pEntry instanceof JCPE_InterfaceMethodref) {
	        // System.out.println(iName + " calls interface method " + pEntry);
	        aInterfaceMethods.addElement (((JCPE_InterfaceMethodref) pEntry).getNameAndType().getName()+":"+((JCPE_InterfaceMethodref) pEntry).getNameAndType().getDescriptor());
	      } else if (pEntry instanceof JCPE_NameAndType) {
	        if (((JCPE_NameAndType) pEntry).getDescriptor().substring(0,1).equals("(")) {
	          if (!((JCPE_NameAndType) pEntry).getName().substring(0,1).equals("<")) {
	            // System.out.println("Method by variable: " + ((JCPE_NameAndType) pEntry).getName()+":"+((JCPE_NameAndType) pEntry).getDescriptor());
	            aInterfaceMethods.addElement (((JCPE_NameAndType) pEntry).getName()+":"+((JCPE_NameAndType) pEntry).getDescriptor());
	          }  
	        }
	      }
	    }
	  }
	
	
	/**
	 * Set the value of a protected field.
	 * 
	 * @param instance the instance to retrieve the field 
	 * @param fieldName the name of the field
	 * @param val the value to set
	 * @return Object
	 */
	public static void setProtectedField (Object instance, String fieldName, Object val) {
		try {
			Field field = instance.getClass ().getField(fieldName);
			field.setAccessible(true);
			field.set(instance, val);
		} catch (NoSuchFieldException ex) {
			ex.printStackTrace();
		} catch (IllegalAccessException ex) {
			ex.printStackTrace();
		}
	}	
}
