package org.openscada.ae.event;

import org.openscada.ae.event.internal.EventServiceImpl;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator
{

    private EventServiceImpl service;

    public void start ( final BundleContext context ) throws Exception
    {
        this.service = new EventServiceImpl ( context );
        context.registerService ( new String[] { EventManager.class.getName (), EventService.class.getName () }, this.service, null );
    }

    public void stop ( final BundleContext context ) throws Exception
    {
        this.service.dispose ();
    }

}
