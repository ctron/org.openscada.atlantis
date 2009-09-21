package org.openscada.hd.server.common;

import java.util.Hashtable;

import org.openscada.hd.server.Service;
import org.openscada.hd.server.common.internal.ServiceImpl;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

public class Activator implements BundleActivator
{
    @SuppressWarnings ( "unused" )
    private BundleContext context;

    private ServiceImpl service;

    private ServiceRegistration serviceRegistration;

    /*
     * (non-Javadoc)
     * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
     */
    public void start ( final BundleContext context ) throws Exception
    {
        this.context = context;
        this.service = new ServiceImpl ( context );
        this.service.start ();

        this.serviceRegistration = context.registerService ( Service.class.getName (), this.service, new Hashtable<String, String> () );
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
        this.context = null;
    }

}
