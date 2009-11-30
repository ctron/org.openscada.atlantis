package org.openscada.ae.monitor.common.testing;

import java.util.Hashtable;

import org.openscada.ae.event.EventProcessor;
import org.openscada.ae.monitor.ConditionService;
import org.openscada.ae.server.common.akn.AknHandler;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator
{

    private EventProcessor processor;

    private TestingCondition service;

    /*
     * (non-Javadoc)
     * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
     */
    public void start ( final BundleContext context ) throws Exception
    {
        this.processor = new EventProcessor ( context );
        this.processor.open ();
        this.service = new TestingCondition ( this.processor, context.getBundle ().getSymbolicName () + ".test" );

        context.registerService ( new String[] { ConditionService.class.getName (), AknHandler.class.getName () }, this.service, new Hashtable<String, String> () );
    }

    /*
     * (non-Javadoc)
     * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
     */
    public void stop ( final BundleContext context ) throws Exception
    {
        this.processor.close ();
        this.service.stop ();
    }

}
