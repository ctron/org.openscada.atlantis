package org.openscada.hd.server.storage;

import java.util.Hashtable;

import org.openscada.ca.ConfigurationAdministrator;
import org.openscada.ca.SelfManagedConfigurationFactory;
import org.openscada.hd.server.storage.osgi.StorageService;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;
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

    /** Storage service instance. */
    private static StorageService service = null;

    /** Service registration of storage service. */
    private static ServiceRegistration serviceRegistration = null;

    /**
     * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
     */
    public void start ( BundleContext context ) throws Exception
    {
        final Object bundleName = context.getBundle ().getHeaders ().get ( Constants.BUNDLE_NAME );
        Hashtable<String, String> gescheiteHT = new Hashtable<String, String> ();
        gescheiteHT.put ( Constants.SERVICE_DESCRIPTION, StorageService.SERVICE_DESCRIPTION );
        gescheiteHT.put ( Constants.SERVICE_VENDOR, "inavare GmbH" );
        gescheiteHT.put ( ConfigurationAdministrator.FACTORY_ID, StorageService.FACTORY_ID );
        logger.info ( bundleName + " starting..." );
        service = new StorageService ( context );
        service.start ();
        logger.info ( bundleName + " service started" );
        serviceRegistration = context.registerService ( new String[] { StorageService.class.getName (), SelfManagedConfigurationFactory.class.getName () }, service, gescheiteHT );
        logger.info ( bundleName + " service registered" );
    }

    /**
     * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
     */
    public void stop ( BundleContext context ) throws Exception
    {
        final Object bundleName = context.getBundle ().getHeaders ().get ( Constants.BUNDLE_NAME );
        logger.info ( bundleName + " stopping..." );
        if ( serviceRegistration != null )
        {
            serviceRegistration.unregister ();
            serviceRegistration = null;
            logger.info ( bundleName + "service unregistered" );
        }
        if ( service != null )
        {
            service.stop ();
            service = null;
            logger.info ( bundleName + " service stopped" );
        }
    }
}
