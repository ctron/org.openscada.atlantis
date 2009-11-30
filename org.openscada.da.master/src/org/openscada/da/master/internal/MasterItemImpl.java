package org.openscada.da.master.internal;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executor;

import org.openscada.core.OperationException;
import org.openscada.core.Variant;
import org.openscada.core.connection.provider.ConnectionIdTracker;
import org.openscada.core.connection.provider.ConnectionTracker;
import org.openscada.core.subscription.SubscriptionState;
import org.openscada.core.utils.AttributesHelper;
import org.openscada.da.client.DataItemValue;
import org.openscada.da.client.ItemUpdateListener;
import org.openscada.da.client.WriteAttributeOperationCallback;
import org.openscada.da.client.WriteOperationCallback;
import org.openscada.da.client.connection.service.ConnectionService;
import org.openscada.da.core.WriteAttributeResults;
import org.openscada.da.core.WriteResult;
import org.openscada.da.datasource.base.AbstractDataSource;
import org.openscada.da.master.MasterItem;
import org.openscada.da.master.MasterItemHandler;
import org.openscada.utils.concurrent.AbstractFuture;
import org.openscada.utils.concurrent.NotifyFuture;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MasterItemImpl extends AbstractDataSource implements ItemUpdateListener, MasterItem
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

    private final class WriteAttributesOperationCallbackImplementation extends AbstractFuture<WriteAttributeResults> implements WriteAttributeOperationCallback
    {
        public void complete ( final WriteAttributeResults results )
        {
            super.setResult ( results );
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

    private final static Logger logger = LoggerFactory.getLogger ( MasterItemImpl.class );

    private ConnectionService connection;

    private final String itemId;

    private volatile DataItemValue value;

    private final Set<MasterItemHandler> subHandler = new HashSet<MasterItemHandler> ();

    private final BundleContext context;

    private final ConnectionIdTracker tracker;

    private final Executor executor;

    public MasterItemImpl ( final Executor executor, final BundleContext context, final String id, final String connectionId, final String itemId ) throws InvalidSyntaxException
    {
        this.executor = executor;
        this.context = context;
        this.itemId = itemId;
        this.value = new DataItemValue ();

        this.tracker = new ConnectionIdTracker ( this.context, connectionId, new ConnectionTracker.Listener () {

            public void setConnection ( final org.openscada.core.connection.provider.ConnectionService connectionService )
            {
                MasterItemImpl.this.setConnection ( (ConnectionService)connectionService );
            }
        }, ConnectionService.class );

        this.tracker.open ();
    }

    @Override
    protected Executor getExecutor ()
    {
        return this.executor;
    }

    protected synchronized void setConnection ( final ConnectionService connectionService )
    {
        logger.info ( "Set connection: {}", connectionService );

        if ( this.connection == connectionService )
        {
            // no change at all
            return;
        }

        // clear the old connection
        if ( this.connection != null )
        {
            this.connection.getItemManager ().removeItemUpdateListener ( this.itemId, this );
        }

        // assign the new one
        this.connection = connectionService;

        if ( this.connection != null )
        {
            // and connect to it
            this.connection.getItemManager ().addItemUpdateListener ( this.itemId, this );
        }
    }

    public void dispose ()
    {
        this.tracker.close ();

        synchronized ( this )
        {
            this.subHandler.clear ();
        }
    }

    public void notifyDataChange ( final Variant value, final Map<String, Variant> attributes, final boolean cache )
    {
        // logger.debug ( "Data update: {} -> {} / {} (cache: {})", new Object[] { this.itemId, value, attributes, cache } );
        applyDataChange ( value, attributes, cache );
        // re-process
        updateData ( processHandler ( this.value ) );
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
        // re-process
        updateData ( processHandler ( this.value ) );
    }

    private void applyStateChange ( final SubscriptionState state, final Throwable error )
    {
        final DataItemValue newValue = new DataItemValue ( this.value );
        newValue.setSubscriptionState ( state );
        newValue.setSubscriptionError ( error );
        this.value = newValue;
    }

    public void addHandler ( final MasterItemHandler handler, final int priority )
    {
        synchronized ( this )
        {
            this.subHandler.add ( handler );
            // re-process
            updateData ( processHandler ( this.value ) );
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
            // re-process
            updateData ( processHandler ( this.value ) );
        }
    }

    protected synchronized DataItemValue processHandler ( DataItemValue value )
    {
        for ( final MasterItemHandler subCondition : this.subHandler )
        {
            final DataItemValue newValue = subCondition.dataUpdate ( value );
            if ( newValue != null )
            {
                value = newValue;
            }
        }
        return value;
    }

    public NotifyFuture<WriteResult> startWriteValue ( final Variant value )
    {
        final WriteOperationCallbackImplementation task = new WriteOperationCallbackImplementation ();
        this.connection.getConnection ().write ( this.itemId, value, task );
        return task;
    }

    public NotifyFuture<WriteAttributeResults> startWriteAttributes ( final Map<String, Variant> attributes )
    {
        final WriteAttributesOperationCallbackImplementation task = new WriteAttributesOperationCallbackImplementation ();
        this.connection.getConnection ().writeAttributes ( this.itemId, attributes, task );
        return task;
    }
}
