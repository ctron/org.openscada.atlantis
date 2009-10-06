package org.openscada.da.master.internal;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.openscada.core.OperationException;
import org.openscada.core.Variant;
import org.openscada.core.subscription.SubscriptionState;
import org.openscada.core.utils.AttributesHelper;
import org.openscada.da.client.DataItemValue;
import org.openscada.da.client.ItemUpdateListener;
import org.openscada.da.client.WriteOperationCallback;
import org.openscada.da.client.connection.service.ConnectionService;
import org.openscada.da.core.WriteResult;
import org.openscada.da.master.MasterItem;
import org.openscada.da.master.MasterItemHandler;
import org.openscada.da.master.MasterItemListener;
import org.openscada.utils.concurrent.AbstractFuture;
import org.openscada.utils.concurrent.NotifyFuture;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;

public class MasterItemImpl implements ItemUpdateListener, ServiceListener, MasterItem
{
    private final class WriteOperationCallbackImplementation extends AbstractFuture<WriteResult> implements WriteOperationCallback
    {
        public void complete ()
        {
            super.setResult ( new WriteResult () );
        }

        public void error ( final Throwable error )
        {
            super.setError ( error );
        }

        public void failed ( final String reason )
        {
            super.setError ( new OperationException ( reason ).fillInStackTrace () );
        }
    }

    private final static Logger logger = Logger.getLogger ( MasterItemImpl.class );

    private ConnectionService connection;

    private final String itemId;

    private volatile DataItemValue value;

    private final Set<MasterItemHandler> subHandler = new HashSet<MasterItemHandler> ();

    private final Set<MasterItemListener> listeners = new HashSet<MasterItemListener> ();

    private final BundleContext context;

    private ServiceReference connectionRef;

    public MasterItemImpl ( final BundleContext context, final String id, final String connectionId, final String itemId ) throws InvalidSyntaxException
    {
        this.context = context;
        this.itemId = itemId;
        this.value = new DataItemValue ();

        final ServiceReference[] refs = context.getServiceReferences ( ConnectionService.class.getName (), String.format ( "(%s=%s)", Constants.SERVICE_PID, connectionId ) );
        if ( refs != null )
        {
            for ( final ServiceReference ref : refs )
            {
                addReference ( ref );
            }
        }
        this.context.addServiceListener ( this, String.format ( "(&(%s=%s)(%s=%s))", Constants.OBJECTCLASS, ConnectionService.class.getName (), Constants.SERVICE_PID, connectionId ) );
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

    private synchronized void addReference ( final ServiceReference ref )
    {
        logger.info ( "New connection: " + ref );

        if ( this.connection != null )
        {
            return;
        }

        this.connection = (ConnectionService)this.context.getService ( ref );
        this.connectionRef = ref;

        this.connection.getItemManager ().addItemUpdateListener ( this.itemId, this );
    }

    private synchronized void removeReference ( final ServiceReference ref )
    {
        if ( this.connectionRef != ref )
        {
            return;
        }

        if ( this.connection != null )
        {
            this.connection.getItemManager ().removeItemUpdateListener ( this.itemId, this );
        }

        this.context.ungetService ( ref );
        this.connection = null;
        this.connectionRef = null;

        // simulate state change
        applyStateChange ( SubscriptionState.DISCONNECTED, null );
        notifyHandler ();
        notifyListener ();
    }

    public void dispose ()
    {
        removeReference ( this.connectionRef );

        synchronized ( this )
        {
            this.subHandler.clear ();
        }
    }

    public void notifyDataChange ( final Variant value, final Map<String, Variant> attributes, final boolean cache )
    {
        applyDataChange ( value, attributes, cache );
        notifyHandler ();
        notifyListener ();
    }

    private void notifyListener ()
    {
        final DataItemValue value = this.value;
        for ( final MasterItemListener listener : this.listeners )
        {
            listener.stateChanged ( value );
        }
    }

    private void applyDataChange ( final Variant value, final Map<String, Variant> attributes, final boolean cache )
    {
        final DataItemValue newValue = new DataItemValue ( this.value );

        final Map<String, Variant> oldAttributes;
        if ( cache )
        {
            oldAttributes = new HashMap<String, Variant> ();
        }
        else
        {
            oldAttributes = new HashMap<String, Variant> ( newValue.getAttributes () );
        }

        if ( value != null )
        {
            newValue.setValue ( value );
        }
        if ( attributes != null )
        {
            AttributesHelper.mergeAttributes ( oldAttributes, attributes, cache );
            newValue.setAttributes ( oldAttributes );
        }
        this.value = newValue;
    }

    public void notifySubscriptionChange ( final SubscriptionState state, final Throwable error )
    {
        logger.info ( "Subscription state changed: " + state );

        applyStateChange ( state, error );
        notifyHandler ();
        notifyListener ();
    }

    private void applyStateChange ( final SubscriptionState state, final Throwable error )
    {
        final DataItemValue newValue = new DataItemValue ( this.value );
        newValue.setSubscriptionState ( state );
        newValue.setSubscriptionError ( error );
        this.value = newValue;
    }

    public void addHandler ( final MasterItemHandler handler )
    {
        synchronized ( this )
        {
            this.subHandler.add ( handler );
            handler.dataUpdate ( this.value );
        }
    }

    /* (non-Javadoc)
     * @see org.openscada.da.master.interal.MasterImpl#removeHandler(org.openscada.da.master.MasterItemHandler)
     */
    public void removeHandler ( final MasterItemHandler handler )
    {
        synchronized ( this )
        {
            this.subHandler.remove ( handler );
        }
    }

    protected synchronized void notifyHandler ()
    {
        DataItemValue value = this.value;
        for ( final MasterItemHandler subCondition : this.subHandler )
        {
            final DataItemValue newValue = subCondition.dataUpdate ( value );
            if ( newValue != null )
            {
                value = newValue;
            }
        }
        this.value = value;
    }

    public void addListener ( final MasterItemListener listener )
    {
        if ( listener == null )
        {
            return;
        }

        this.listeners.add ( listener );
        listener.stateChanged ( this.value );
    }

    public void removeListener ( final MasterItemListener listener )
    {
        if ( listener == null )
        {
            return;
        }
        this.listeners.remove ( listener );
    }

    public NotifyFuture<WriteResult> startWriteValue ( final Variant value )
    {
        final WriteOperationCallbackImplementation task = new WriteOperationCallbackImplementation ();
        this.connection.getConnection ().write ( this.itemId, value, task );
        return task;
    }
}
