package org.openscada.hd.server.common;

import java.util.Hashtable;

import org.openscada.hd.server.Service;
import org.openscada.hd.server.common.internal.ServiceImpl;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;

/**
 * This activator registeres the a common OSGi based
 * implemenation of the HD server side service
 * @author Jens Reimann
 * @since 0.14.0
 *
 */
public class Activator implements BundleActivator
{
    private ServiceImpl service;

    private ServiceRegistration serviceRegistration;

    /*
     * (non-Javadoc)
     * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
     */
    public void start ( final BundleContext context ) throws Exception
    {
        this.service = new ServiceImpl ( context );
        this.service.start ();

        final Hashtable<String, String> properties = new Hashtable<String, String> ();
        properties.put ( Constants.SERVICE_VENDOR, "inavare GmbH" );
        properties.put ( Constants.SERVICE_DESCRIPTION, "An OpenSCADA HD service implementation" );
        this.serviceRegistration = context.registerService ( Service.class.getName (), this.service, properties );
    }

    /*
     * (non-Javadoc)
     * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
     */
    public void stop ( final BundleContext context ) throws Exception
    {
        this.serviceRegistration.unregister ();

        this.service.stop ();
        this.service = null;
    }

}
