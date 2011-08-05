package org.openscada.hd.server.proxy;

import org.openscada.hd.server.common.HistoricalItem;
import org.openscada.utils.osgi.FilterUtil;
import org.openscada.utils.osgi.SingleServiceListener;
import org.openscada.utils.osgi.SingleServiceTracker;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;

public class ProxyValueSource
{
    private final BundleContext context;

    private final String id;

    private final SingleServiceTracker tracker;

    private final SingleServiceListener listener;

    private HistoricalItem service;

    private final ProxyHistoricalItem item;

    public ProxyValueSource ( final BundleContext context, final String id, final ProxyHistoricalItem item ) throws InvalidSyntaxException
    {
        this.context = context;
        this.id = id;
        this.item = item;

        this.listener = new SingleServiceListener () {

            @Override
            public void serviceChange ( final ServiceReference reference, final Object service )
            {
                setService ( (HistoricalItem)service );
            }
        };

        this.tracker = new SingleServiceTracker ( context, FilterUtil.createClassAndPidFilter ( HistoricalItem.class.getName (), id ), this.listener );
        this.tracker.open ();
    }

    protected void setService ( final HistoricalItem service )
    {
        if ( this.service != null )
        {
            this.item.removeSource ( this.service );
        }

        this.service = service;

        if ( this.service != null )
        {
            this.item.addSource ( this.service );
        }
    }

    public void dispose ()
    {
        this.tracker.close ();

        if ( this.service != null )
        {
            this.item.removeSource ( this.service );
        }
    }
}
