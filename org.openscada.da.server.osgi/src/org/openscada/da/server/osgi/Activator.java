package org.openscada.da.server.osgi;

import java.util.Dictionary;
import java.util.Hashtable;

import org.openscada.da.core.server.Hive;
import org.openscada.da.server.common.DataItem;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;

public class Activator implements BundleActivator
{

    private HiveImpl service;

    private ServiceRegistration handle;

    private ServiceListener listener;

    /*
     * (non-Javadoc)
     * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
     */
    public void start ( final BundleContext context ) throws Exception
    {
        this.service = new HiveImpl ( context );
        this.service.start ();

        Dictionary<Object, Object> properties = new Hashtable<Object, Object> ();

        properties.put ( Constants.SERVICE_VENDOR, "inavare GmbH" );
        properties.put ( Constants.SERVICE_DESCRIPTION, "A common generic OSGi DA Hive" );

        this.handle = context.registerService ( Hive.class.getName (), this.service, properties );

        context.addServiceListener ( this.listener = new ServiceListener () {

            public void serviceChanged ( final ServiceEvent event )
            {
                switch ( event.getType () )
                {
                case ServiceEvent.REGISTERED:
                    Activator.this.addItem ( event.getServiceReference () );
                    break;
                case ServiceEvent.UNREGISTERING:
                    Activator.this.removeItem ( event.getServiceReference () );
                    break;
                }
            }
        }, "(" + Constants.OBJECTCLASS + "=" + DataItem.class.getName () + ")" );

        ServiceReference[] refs = context.getServiceReferences ( DataItem.class.getName (), null );
        if ( refs != null )
        {
            for ( ServiceReference ref : refs )
            {
                addItem ( ref );
            }
        }
    }

    protected void removeItem ( final ServiceReference serviceReference )
    {
        this.service.removeItem ( serviceReference );
    }

    protected void addItem ( final ServiceReference serviceReference )
    {
        this.service.addItem ( serviceReference );
    }

    /*
     * (non-Javadoc)
     * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
     */
    public void stop ( final BundleContext context ) throws Exception
    {
        context.removeServiceListener ( this.listener );

        this.handle.unregister ();
        this.handle = null;

        this.service.stop ();
        this.service = null;

    }

}
