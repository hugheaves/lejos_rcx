/*
 * Created on 06.07.2004
 * 
 * TODO To change the template for this generated file go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
package org.lejos.tools.eclipse.plugin.view;

import java.util.ArrayList;
import java.util.List;

import js.tinyvm.Binary;
import js.tinyvm.ClassRecord;
import js.tinyvm.ConstantRecord;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.lejos.tools.eclipse.plugin.view.model.BinaryClassElement;
import org.lejos.tools.eclipse.plugin.view.model.BinaryConstantElement;
import org.lejos.tools.eclipse.plugin.view.model.BinaryElement;
import org.lejos.tools.eclipse.plugin.view.model.ClassElement;
import org.lejos.tools.eclipse.plugin.view.model.ConstantElement;
import org.lejos.tools.eclipse.plugin.view.model.Element;

/**
 * @author Markus
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class LinkerContentProvider implements ITreeContentProvider
{
   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
    */
   public Object[] getElements (Object inputElement)
   {
      System.out.println("getElements: " + inputElement);
      if (inputElement instanceof BinaryElement)
      {
         BinaryElement binaryNode = (BinaryElement) inputElement;
         return new Object[]
         {
            new BinaryClassElement(binaryNode),
            new BinaryConstantElement(binaryNode)
         };
      }

      return new Object[0];
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
    */
   public boolean hasChildren (Object element)
   {
      return element instanceof BinaryClassElement || element instanceof BinaryConstantElement;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
    */
   public Object[] getChildren (Object parentElement)
   {
      System.out.println("getChildren: " + parentElement);
      if (parentElement instanceof BinaryClassElement)
      {
         BinaryClassElement binaryClassElement = (BinaryClassElement) parentElement;
         Binary binary = binaryClassElement.getBinary();
         List classRecords = new ArrayList();
         ClassRecord classRecord = binary.getClassRecord("java/lang/Object");
         return new Object[]
         {
            new ClassElement(binaryClassElement, classRecord)
         };
      }
      else if (parentElement instanceof BinaryConstantElement)
      {
         BinaryConstantElement binaryConstantElement = (BinaryConstantElement) parentElement;
         Binary binary = binaryConstantElement.getBinary();
         ConstantRecord constantRecord = binary.getConstantRecord(0);
         return new Object[]
         {
            new ConstantElement(binaryConstantElement, constantRecord)
         };
      }

      return new Object[0];
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
    */
   public Object getParent (Object element)
   {
      System.out.println("getParent: " + element);
      if (element instanceof Element)
      {
         return ((Element) element).getParent();
      }

      return null;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.jface.viewers.IContentProvider#dispose()
    */
   public void dispose ()
   {
   // TODO Auto-generated method stub
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer,
    *      java.lang.Object, java.lang.Object)
    */
   public void inputChanged (Viewer viewer, Object oldInput, Object newInput)
   {
   // TODO Auto-generated method stub
   }
}