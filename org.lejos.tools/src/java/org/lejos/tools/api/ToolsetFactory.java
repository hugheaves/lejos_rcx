package org.lejos.tools.api;

import java.util.Properties;

import org.lejos.tools.impl.DevelopmentToolsetImpl;
import org.lejos.tools.impl.RuntimeToolsetImpl;

/**
 * The <code>ToolsetFactory</code> creates the required implementation classes
 * which supports the toolset api.
 * 
 * <p>
 * Its implementation is based on the same concept like the JAXP implementation
 * of SUN.
 * </p>
 * 
 * <p>
 * The typical usage is:
 * </p>
 * <code>
 *   Properties props = new Properties();<br />
 *   props.setProperty(<br />
 *      ToolsetFactory.FACTORY_CLASS_PROPERTY,<br />
 * 		MyToolsetFactory.class.getName());<br />
 *   props.setProperty(<br />
 *      ToolsetFactory.PROGRESS_MONITOR_CLASS_PROPERTY,<br />
 * 		MyProgressMonitor.class.getName());<br />
 *   ToolsetFactory factory = ToolsetFactory.newInstance (props);<br />
 *   // can be casted to our own class<br />
 *   MyToolsetFactory myFactory = (MyToolsetFactory) factory;<br />
 *   IRuntimeToolset toolset = myFactory.newRuntimeToolset();<br />
 *   toolset.setVerbose (true);<br />
 *   toolset.link (...);<br />
 * <code>
 * 
 * TODO ENH add property support for RuntimeToolset and DevelopmentToolset too
 * 
 * @author <a href="mailto:jochen.hiller@t-online.de">Jochen Hiller</a>
 */
public class ToolsetFactory
{

   // static attributes

   /** Name of the property, which specifies the factory class. */
   public static final String FACTORY_CLASS_PROPERTY = "org.lejos.tools.api.factory";

   /** Name of the default factory. */
   private static final String DEFAULT_FACTORY_CLASS_NAME = "org.lejos.tools.api.ToolsetFactory";

   /**
    * Name of the property, which specifies the progress monitor to be used.
    */
   public static final String PROGRESS_MONITOR_CLASS_PROPERTY = "org.lejos.tools.api.monitor";

   /** Name of the default progress monitor. */
   private static final String DEFAULT_PROGRESS_MONITOR_CLASS_NAME = "org.lejos.tools.impl.ConsoleProgressMonitorToolsetImpl";

   // attributes

   /** The features specifiy which implementation classes to be used. */
   private Properties features;

   // constructors

   /**
    * Default constructor is protected to ensure instance creation only by
    * subclasses.
    */
   protected ToolsetFactory ()
   {
   // nothing to do yet
   }

   /**
    * Creates a new runtime toolset.
    * 
    * @return a created runtime toolset
    */
   public IRuntimeToolset newRuntimeToolset ()
   {
      return new RuntimeToolsetImpl();
   }

   /**
    * Creates a new development toolset.
    * 
    * @return a created devlopment toolset
    */
   public IDevelopmentToolset newDevelopmentToolset ()
   {
      return new DevelopmentToolsetImpl();
   }

   /**
    * Creates a new progress monitor.
    * 
    * @return a new progress monitor
    * @throws ToolsetException will be raised in any error case, e.g. if a
    *            progress monitor can NOT be instantiated
    */
   public IProgressMonitorToolset newProgressMonitor () throws ToolsetException
   {
      // this property specifies the factory class name
      String progressMonitorClassName = this.features
         .getProperty(PROGRESS_MONITOR_CLASS_PROPERTY);
      // must be set, otherwise we will use the default implementation.
      if (progressMonitorClassName == null)
      {
         progressMonitorClassName = DEFAULT_PROGRESS_MONITOR_CLASS_NAME;
      }
      // now create an instance of this progress monitor
      try
      {
         Class clazz = Class.forName(progressMonitorClassName);
         Object progressMonitorObj = clazz.newInstance();
         if (!(progressMonitorObj instanceof IProgressMonitorToolset))
         {
            throw new ToolsetException("progress monitor class name invalid: "
               + "Not an instance of "
               + IProgressMonitorToolset.class.getName());
         }
         IProgressMonitorToolset progressMonitor = (IProgressMonitorToolset) progressMonitorObj;
         return progressMonitor;
      }
      catch (ClassNotFoundException ex)
      {
         throw new ToolsetException("progress monitor class "
            + String.valueOf(progressMonitorClassName) + " not found", ex);
      }
      catch (InstantiationException ex)
      {
         throw new ToolsetException("progress monitor class "
            + String.valueOf(progressMonitorClassName)
            + " could not be instantiated", ex);
      }
      catch (IllegalAccessException ex)
      {
         throw new ToolsetException(
            "progress monitor class "
               + String.valueOf(progressMonitorClassName)
               + " could not be access", ex);
      }
   }

   // static methods

   /**
    * Creates a new instance of this factory, based on the system properties.
    * 
    * <p>
    * Is currently NOT a singleton !
    * </p>
    * 
    * @see ToolsetFactory#newInstance(Properties)
    * 
    * @return a new factory
    */
   public static ToolsetFactory newInstance ()
   {
      // if not specified, use system properties
      return ToolsetFactory.newInstance(System.getProperties());
   }

   /**
    * Creates a new instance of this factory, based on the given properties
    * 
    * @param aFeatures the given features to use for cration of the factory
    * @return a new factory
    */
   public static ToolsetFactory newInstance (Properties aFeatures)
   {
      // this property specifies the factory class name
      String factoryClassName = aFeatures.getProperty(FACTORY_CLASS_PROPERTY);
      // must be set, otherwise we will use the default implementation.
      if (factoryClassName == null)
      {
         factoryClassName = DEFAULT_FACTORY_CLASS_NAME;
      }
      // now create an instance of this factory
      try
      {
         Class clazz = Class.forName(factoryClassName);
         Object factoryObj = clazz.newInstance();
         if (!(factoryObj instanceof ToolsetFactory))
         {
            throw new FactoryConfigurationError("factory class name invalid: "
               + "Not an instance of " + ToolsetFactory.class.getName());
         }
         ToolsetFactory factory = (ToolsetFactory) factoryObj;
         factory.features = aFeatures;
         return factory;
      }
      catch (ClassNotFoundException ex)
      {
         throw new FactoryConfigurationError("factory class "
            + String.valueOf(factoryClassName) + " not found", ex);
      }
      catch (InstantiationException ex)
      {
         throw new FactoryConfigurationError("factory class "
            + String.valueOf(factoryClassName) + " could not be instantiated",
            ex);
      }
      catch (IllegalAccessException ex)
      {
         throw new FactoryConfigurationError("factory class "
            + String.valueOf(factoryClassName) + " could not be access", ex);
      }
   }
}