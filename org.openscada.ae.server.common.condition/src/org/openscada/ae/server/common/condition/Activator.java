package org.openscada.ae.server.common.condition;

import java.util.Dictionary;
import java.util.Hashtable;

import org.openscada.ae.monitor.MonitorService;
import org.openscada.ae.server.common.condition.internal.BundleConditionQuery;
import org.openscada.utils.osgi.pool.ObjectPoolTracker;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;

public class Activator implements BundleActivator
{

    private BundleContext context;

    private BundleConditionQuery allQuery;

    private ServiceRegistration handle;

    private ObjectPoolTracker poolTracker;

    /*
     * (non-Javadoc)
     * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
     */
    public void start ( final BundleContext context ) throws Exception
    {
        this.context = context;

        this.poolTracker = new ObjectPoolTracker ( context, MonitorService.class.getName () );
        this.poolTracker.open ();

        this.allQuery = new BundleConditionQuery ( context, this.poolTracker );

        final Dictionary<String, String> properties = new Hashtable<String, String> ();
        properties.put ( Constants.SERVICE_PID, context.getBundle ().getSymbolicName () + ".all" );
        properties.put ( Constants.SERVICE_VENDOR, "inavare GmbH" );
        properties.put ( Constants.SERVICE_DESCRIPTION, "A condition query containing all condition services" );

        this.handle = this.context.registerService ( ConditionQuery.class.getName (), this.allQuery, properties );
    }

    /*
     * (non-Javadoc)
     * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
     */
    public void stop ( final BundleContext context ) throws Exception
    {
        this.poolTracker.close ();

        this.allQuery.dispose ();
        this.handle.unregister ();
    }

}
