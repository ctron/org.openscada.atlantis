package org.openscada.da.master;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.openscada.da.client.DataItemValue;
import org.openscada.utils.osgi.FilterUtil;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

public abstract class AbstractMasterHandlerImpl implements MasterItemHandler
{
    private final BundleContext context;

    private ServiceTracker tracker;

    private final Set<MasterItem> items = new CopyOnWriteArraySet<MasterItem> ();

    private final int priority;

    private String masterId;

    public AbstractMasterHandlerImpl ( final BundleContext context, final int priority )
    {
        this.context = context;
        this.priority = priority;
    }

    public synchronized void dispose ()
    {
        if ( this.tracker != null )
        {
            this.tracker.close ();
            this.tracker = null;
        }
    }

    protected String getMasterId ()
    {
        return this.masterId;
    }

    public synchronized void update ( final Map<String, String> parameters ) throws Exception
    {
        if ( this.tracker != null )
        {
            this.tracker.close ();
            this.tracker = null;
        }

        this.masterId = parameters.get ( MasterItem.MASTER_ID );
        if ( this.masterId == null )
        {
            throw new IllegalArgumentException ( String.format ( "'%s' must be set", MasterItem.MASTER_ID ) );
        }

        final Map<String, String> filterParameters = new HashMap<String, String> ();
        filterParameters.put ( MasterItem.MASTER_ID, this.masterId );

        final Filter filter = FilterUtil.createAndFilter ( MasterItem.class.getName (), filterParameters );

        this.tracker = new ServiceTracker ( this.context, filter, new ServiceTrackerCustomizer () {

            public void removedService ( final ServiceReference reference, final Object service )
            {
                if ( removeItem ( (MasterItem)service ) )
                {
                    AbstractMasterHandlerImpl.this.context.ungetService ( reference );
                }
            }

            public void modifiedService ( final ServiceReference reference, final Object service )
            {

            }

            public Object addingService ( final ServiceReference reference )
            {
                Object o = AbstractMasterHandlerImpl.this.context.getService ( reference );
                try
                {
                    final MasterItem item = (MasterItem)o;
                    addItem ( item );
                    o = null;
                }
                finally
                {
                    if ( o != null )
                    {
                        AbstractMasterHandlerImpl.this.context.ungetService ( reference );
                    }
                }
                return null;
            }
        } );
        this.tracker.open ();
    }

    protected boolean removeItem ( final MasterItem item )
    {
        if ( this.items.remove ( item ) )
        {
            item.removeHandler ( this );
            return true;
        }
        else
        {
            return false;
        }
    }

    protected void addItem ( final MasterItem item )
    {
        if ( this.items.add ( item ) )
        {
            item.addHandler ( this, this.priority );
        }
    }

    public abstract DataItemValue dataUpdate ( final DataItemValue value );

    public WriteRequestResult processWrite ( final WriteRequest request )
    {
        return null;
    }
}
