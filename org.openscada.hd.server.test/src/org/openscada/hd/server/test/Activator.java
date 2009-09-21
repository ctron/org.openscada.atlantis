package org.openscada.hd.server.test;

import java.util.Dictionary;
import java.util.Hashtable;

import org.openscada.hd.server.common.HistoricalItem;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

public class Activator implements BundleActivator
{

    private ServiceRegistration handle;

    private TestItemImpl service;

    /*
     * (non-Javadoc)
     * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
     */
    public void start ( final BundleContext context ) throws Exception
    {
        final Dictionary<Object, Object> properties = new Hashtable<Object, Object> ();
        properties.put ( "itemId", "test1" );
        this.service = new TestItemImpl ();
        this.handle = context.registerService ( HistoricalItem.class.getName (), this.service, properties );
    }

    /*
     * (non-Javadoc)
     * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
     */
    public void stop ( final BundleContext context ) throws Exception
    {
        this.service.dispose ();
        this.handle.unregister ();
    }

}
