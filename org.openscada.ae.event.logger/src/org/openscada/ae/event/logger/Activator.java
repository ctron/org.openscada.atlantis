package org.openscada.ae.event.logger;

import java.util.Dictionary;
import java.util.Hashtable;

import org.openscada.ae.event.logger.internal.DataSourceLoggerFactory;
import org.openscada.ca.ConfigurationAdministrator;
import org.openscada.ca.ConfigurationFactory;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;

public class Activator implements BundleActivator
{

    private DataSourceLoggerFactory factory;

    /*
     * (non-Javadoc)
     * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
     */
    public void start ( final BundleContext context ) throws Exception
    {
        this.factory = new DataSourceLoggerFactory ( context );
        final Dictionary<String, String> properties = new Hashtable<String, String> ();
        properties.put ( ConfigurationAdministrator.FACTORY_ID, context.getBundle ().getSymbolicName () );
        properties.put ( Constants.SERVICE_DESCRIPTION, "Records write requests" );
        context.registerService ( ConfigurationFactory.class.getName (), this.factory, properties );
    }

    /*
     * (non-Javadoc)
     * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
     */
    public void stop ( final BundleContext context ) throws Exception
    {
        this.factory.dispose ();
    }

}
