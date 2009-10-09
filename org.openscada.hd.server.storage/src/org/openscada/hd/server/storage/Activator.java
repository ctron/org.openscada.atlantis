package org.openscada.hd.server.storage;

import java.util.Hashtable;

import org.openscada.ca.ConfigurationAdministrator;
import org.openscada.ca.SelfManagedConfigurationFactory;
import org.openscada.hd.server.storage.osgi.StorageService;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * OSGi activator for package org.openscada.hd.server.storage.
 * @author Ludwig Straub
 */
public class Activator implements BundleActivator
{
    /** Default logger. */
    private final static Logger logger = LoggerFactory.getLogger ( Activator.class );

    /** OSGi bundle context. */
    private static BundleContext bundleContext = null;

    /** Storage service instance. */
    private static StorageService service = null;

    /**
     * This method returns the currently available bundle context.
     * @return currently available bundle context
     */
    public static BundleContext getBundleContext ()
    {
        return bundleContext;
    }

    /**
     * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
     */
    public void start ( BundleContext context ) throws Exception
    {
        Hashtable<String, String> gescheiteHT = new Hashtable<String, String> ();
        gescheiteHT.put ( Constants.SERVICE_DESCRIPTION, "" );
        gescheiteHT.put ( Constants.SERVICE_VENDOR, "inavare GmbH" );
        gescheiteHT.put ( ConfigurationAdministrator.FACTORY_ID, StorageService.FACTORY_ID );
        logger.debug ( context.getBundle ().getHeaders ().get ( Constants.BUNDLE_NAME ) + " starting..." );
        service = new StorageService ( context );
        context.registerService ( new String[] { StorageService.class.getName (), SelfManagedConfigurationFactory.class.getName () }, service, gescheiteHT );
        logger.debug ( "Service registered: StorageService" );
        Activator.bundleContext = context;
        service.start ();
        logger.debug ( "Service started: StorageService" );
    }

    /**
     * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
     */
    public void stop ( BundleContext context ) throws Exception
    {
        logger.debug ( context.getBundle ().getHeaders ().get ( Constants.BUNDLE_NAME ) + " stopping..." );
        if ( service != null )
        {
            service.stop ();
            service = null;
        }
        Activator.bundleContext = null;
    }
}
