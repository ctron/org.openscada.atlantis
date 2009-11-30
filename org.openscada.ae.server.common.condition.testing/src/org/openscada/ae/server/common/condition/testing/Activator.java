package org.openscada.ae.server.common.condition.testing;

import java.util.Dictionary;
import java.util.Hashtable;

import org.openscada.ae.server.common.condition.ConditionQuery;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;

public class Activator implements BundleActivator
{

    private BundleContext context;

    private TestConditionQuery service;

    private ServiceRegistration handle;

    /*
     * (non-Javadoc)
     * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
     */
    public void start ( final BundleContext context ) throws Exception
    {
        this.context = context;
        this.service = new TestConditionQuery ();
        Dictionary<String, String> properties = new Hashtable<String, String> ();
        properties.put ( Constants.SERVICE_PID, "test" );
        this.handle = this.context.registerService ( ConditionQuery.class.getName (), this.service, properties );
    }

    /*
     * (non-Javadoc)
     * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
     */
    public void stop ( final BundleContext context ) throws Exception
    {
        this.handle.unregister ();
        this.service.stop ();
    }

}
