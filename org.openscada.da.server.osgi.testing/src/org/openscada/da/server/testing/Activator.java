package org.openscada.da.server.testing;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import org.apache.log4j.Logger;
import org.openscada.da.server.common.DataItem;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

public class Activator implements BundleActivator
{
    private final static Logger logger = Logger.getLogger ( Activator.class );

    private DataItemTest1 service;

    private ScheduledThreadPoolExecutor executor;

    private ServiceRegistration handle;

    /*
     * (non-Javadoc)
     * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
     */
    public void start ( final BundleContext context ) throws Exception
    {
        this.executor = new ScheduledThreadPoolExecutor ( 1 );
        this.service = new DataItemTest1 ( "test", this.executor );

        Dictionary<Object, Object> properties = new Hashtable<Object, Object> ();
        this.handle = context.registerService ( DataItem.class.getName (), this.service, properties );
    }

    /*
     * (non-Javadoc)
     * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
     */
    public void stop ( final BundleContext context ) throws Exception
    {
        logger.info ( "Stopping test server" );

        this.handle.unregister ();
        this.handle = null;

        this.service.dispose ();
        this.service = null;

        this.executor.shutdown ();
        this.executor = null;
    }

}
