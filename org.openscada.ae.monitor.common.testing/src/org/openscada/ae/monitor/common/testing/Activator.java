package org.openscada.ae.monitor.common.testing;

import java.util.Hashtable;

import org.openscada.ae.event.EventProcessor;
import org.openscada.ae.monitor.MonitorService;
import org.openscada.utils.osgi.pool.ObjectPoolHelper;
import org.openscada.utils.osgi.pool.ObjectPoolImpl;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

public class Activator implements BundleActivator
{

    private EventProcessor processor;

    private TestingCondition service;

    private ObjectPoolImpl monitorServicePool;

    private ServiceRegistration monitorServicePoolHandler;

    /*
     * (non-Javadoc)
     * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
     */
    public void start ( final BundleContext context ) throws Exception
    {
        this.processor = new EventProcessor ( context );
        this.processor.open ();
        this.service = new TestingCondition ( this.processor, context.getBundle ().getSymbolicName () + ".test" );
        this.service.init ();

        this.monitorServicePool = new ObjectPoolImpl ();
        this.monitorServicePoolHandler = ObjectPoolHelper.registerObjectPool ( context, this.monitorServicePool, MonitorService.class.getName () );

        this.monitorServicePool.addService ( this.service.getId (), context, new Hashtable<String, String> () );
    }

    /*
     * (non-Javadoc)
     * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
     */
    public void stop ( final BundleContext context ) throws Exception
    {
        this.monitorServicePool.dispose ();
        this.processor.close ();
        this.service.stop ();
    }

}
