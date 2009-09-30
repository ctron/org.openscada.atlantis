package org.openscada.hd.client.net;

import java.util.Dictionary;
import java.util.Hashtable;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;

public class Activator implements BundleActivator
{
    private DriverFactoryImpl factory;

    private ServiceRegistration handle;

    /*
     * (non-Javadoc)
     * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
     */
    public void start ( final BundleContext context ) throws Exception
    {
        this.factory = new DriverFactoryImpl ();

        final Dictionary<String, String> properties = new Hashtable<String, String> ();
        properties.put ( org.openscada.core.client.DriverFactory.INTERFACE_NAME, "hd" );
        properties.put ( org.openscada.core.client.DriverFactory.DRIVER_NAME, "net" );
        properties.put ( Constants.SERVICE_DESCRIPTION, "OpenSCADA HD NET Adapter" );
        properties.put ( Constants.SERVICE_VENDOR, "inavare GmbH" );
        this.handle = context.registerService ( org.openscada.core.client.DriverFactory.class.getName (), this.factory, properties );
    }

    /*
     * (non-Javadoc)
     * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
     */
    public void stop ( final BundleContext context ) throws Exception
    {
        this.handle.unregister ();
        this.handle = null;
        this.factory = null;
    }

}
