package org.openscada.ae.server.common.condition;

import java.util.Dictionary;
import java.util.Hashtable;

import org.openscada.ae.server.common.condition.internal.BundleConditionQuery;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;

public class Activator implements BundleActivator
{

    private BundleContext context;

    private BundleConditionQuery allQuery;

    private ServiceRegistration handle;

    /*
     * (non-Javadoc)
     * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
     */
    public void start ( final BundleContext context ) throws Exception
    {
        this.context = context;
        this.allQuery = new BundleConditionQuery ( context );

        Dictionary<String, String> properties = new Hashtable<String, String> ();
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
        this.handle.unregister ();
    }

}
