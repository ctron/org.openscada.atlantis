package org.openscada.da.master;

import java.util.Dictionary;
import java.util.Hashtable;

import org.openscada.ca.ConfigurationAdministrator;
import org.openscada.ca.ConfigurationFactory;
import org.openscada.da.master.internal.MasterFactory;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

public class Activator implements BundleActivator
{
    private MasterFactory masterService;

    private ServiceRegistration masterHandle;

    /*
     * (non-Javadoc)
     * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
     */
    public void start ( final BundleContext context ) throws Exception
    {
        Dictionary<Object, Object> properties;

        // master service
        this.masterService = new MasterFactory ( context );
        properties = new Hashtable<Object, Object> ();
        properties.put ( ConfigurationAdministrator.FACTORY_ID, "master.item" );
        this.masterHandle = context.registerService ( new String[] { ConfigurationFactory.class.getName () }, this.masterService, properties );
    }

    /*
     * (non-Javadoc)
     * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
     */
    public void stop ( final BundleContext context ) throws Exception
    {
        this.masterHandle.unregister ();
        this.masterService.dispose ();
        this.masterService = null;
    }

}
