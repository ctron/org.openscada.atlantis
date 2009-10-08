package org.openscada.da.client.connection.service;

import java.util.Dictionary;
import java.util.Hashtable;

import org.openscada.ca.ConfigurationAdministrator;
import org.openscada.ca.ConfigurationFactory;
import org.openscada.da.client.connection.service.internal.ManagedConnectionServiceFactory;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;

public class Activator implements BundleActivator
{
    private static final String FACTORY_ID = "da.connection"; //$NON-NLS-1$

    private org.openscada.da.client.connection.service.internal.ManagedConnectionServiceFactory service;

    private BundleContext context;

    private ServiceRegistration handle;

    /*
     * (non-Javadoc)
     * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
     */
    public void start ( final BundleContext context ) throws Exception
    {
        this.context = context;
        this.service = new ManagedConnectionServiceFactory ( context );
        final Dictionary<Object, Object> properties = new Hashtable<Object, Object> ();
        properties.put ( ConfigurationAdministrator.FACTORY_ID, FACTORY_ID );
        properties.put ( Constants.SERVICE_VENDOR, "inavare GmbH" );
        properties.put ( Constants.SERVICE_DESCRIPTION, "An OpenSCADA DA Connection Service Factory" );
        this.handle = this.context.registerService ( ConfigurationFactory.class.getName (), this.service, properties );
    }

    /*
     * (non-Javadoc)
     * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
     */
    public void stop ( final BundleContext context ) throws Exception
    {
        this.handle.unregister ();
        this.context = null;
        this.service = null;
    }

}
