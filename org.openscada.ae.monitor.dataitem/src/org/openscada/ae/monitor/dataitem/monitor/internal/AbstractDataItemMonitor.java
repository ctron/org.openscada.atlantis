package org.openscada.ae.monitor.dataitem.monitor.internal;

import java.util.HashMap;
import java.util.Map;

import org.openscada.ae.event.EventProcessor;
import org.openscada.ae.monitor.common.AbstractConditionService;
import org.openscada.da.master.MasterItem;
import org.openscada.da.master.MasterItemHandler;
import org.openscada.da.master.WriteRequest;
import org.openscada.da.master.WriteRequestResult;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;

public abstract class AbstractDataItemMonitor extends AbstractConditionService implements DataItemMonitor, ServiceListener, MasterItemHandler
{

    protected volatile Map<String, String> properties;

    private final BundleContext context;

    private final String masterId;

    private ServiceReference ref;

    private MasterItem service;

    public AbstractDataItemMonitor ( final BundleContext context, final EventProcessor eventProcessor, final String id, final String masterId, final Map<String, String> properties )
    {
        super ( eventProcessor, id );
        this.context = context;
        this.properties = properties;
        this.masterId = masterId;

        applyProperties ();
    }

    public synchronized void initialize () throws InvalidSyntaxException
    {
        this.context.addServiceListener ( this, String.format ( "(&(%s=%s)(%s=%s))", Constants.OBJECTCLASS, MasterItem.class.getName (), Constants.SERVICE_PID, this.masterId ) );

        final ServiceReference[] refs = this.context.getServiceReferences ( MasterItem.class.getName (), String.format ( "(%s=%s)", Constants.SERVICE_PID, this.masterId ) );
        if ( refs != null )
        {
            for ( final ServiceReference ref : refs )
            {
                addReference ( ref );
            }
        }
    }

    protected synchronized void addReference ( final ServiceReference ref )
    {
        if ( this.ref != null )
        {
            return;
        }

        this.ref = ref;
        final Object o = this.context.getService ( ref );
        if ( ! ( o instanceof MasterItem ) )
        {
            if ( o != null )
            {
                this.context.ungetService ( ref );
            }
            return;
        }

        this.service = (MasterItem)o;
        this.service.addHandler ( this, 0 );
    }

    protected synchronized void removeReference ( final ServiceReference ref )
    {
        if ( this.ref != ref )
        {
            return;
        }

        this.ref = null;
        if ( this.service != null )
        {
            this.service.removeHandler ( this );
            this.service = null;
            this.context.ungetService ( ref );
        }

        // signal "no data"
        dataUpdate ( null );
    }

    public void serviceChanged ( final ServiceEvent event )
    {
        switch ( event.getType () )
        {
        case ServiceEvent.REGISTERED:
            addReference ( event.getServiceReference () );
            break;
        case ServiceEvent.UNREGISTERING:
            removeReference ( event.getServiceReference () );
            break;
        }
    }

    /**
     * Apply properties after they have changed
     */
    private void applyProperties ()
    {
        setRequireAkn ( getBooleanDefault ( "requireAkn", false ) );
    }

    protected boolean getBooleanDefault ( final String name, final boolean defaultValue )
    {
        final Boolean value = getBoolean ( name );
        if ( value == null )
        {
            return defaultValue;
        }
        return value;
    }

    protected Boolean getBoolean ( final String name )
    {
        final Object o = this.properties.get ( name );
        if ( o == null )
        {
            return null;
        }
        if ( o instanceof Boolean )
        {
            return (Boolean)o;
        }
        if ( o instanceof Number )
        {
            return ( (Number)o ).intValue () != 0;
        }
        final String strValue = o.toString ();
        return Boolean.parseBoolean ( strValue );
    }

    protected Number getNumber ( final String name )
    {
        final Object o = this.properties.get ( name );
        if ( o == null )
        {
            return null;
        }
        if ( o instanceof Boolean )
        {
            return (Boolean)o ? 1 : 0;
        }
        if ( o instanceof Number )
        {
            return (Number)o;
        }
        final String strValue = o.toString ();
        return Double.parseDouble ( strValue );
    }

    /* (non-Javadoc)
     * @see org.openscada.ae.monitor.dataitem.monitor.internal.DataItemMonitor#configure(java.util.Dictionary)
     */
    public void configure ( final Map<String, String> properties )
    {
        this.properties = new HashMap<String, String> ( properties );
        applyProperties ();
    }

    /* (non-Javadoc)
     * @see org.openscada.ae.monitor.dataitem.monitor.internal.DataItemMonitor#dispose()
     */
    public void dispose ()
    {
        removeReference ( this.ref );
    }

    public WriteRequestResult processWrite ( final WriteRequest request )
    {
        return null;
    }
}