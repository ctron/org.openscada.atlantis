package org.openscada.da.master.internal;

import java.util.ArrayList;
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
import org.openscada.da.core.WriteAttributeResult;
import org.openscada.da.core.WriteAttributeResults;
import org.openscada.da.core.WriteResult;
import org.openscada.da.datasource.base.AbstractDataSource;
import org.openscada.da.master.MasterItem;
import org.openscada.da.master.MasterItemHandler;
import org.openscada.da.master.WriteRequest;
import org.openscada.da.master.WriteRequestResult;
import org.openscada.utils.concurrent.AbstractFuture;
import org.openscada.utils.concurrent.NotifyFuture;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MasterItemImpl extends AbstractDataSource implements ItemUpdateListener, MasterItem
{

    private static class WriteListenerAttributeImpl extends AbstractFuture<WriteAttributeResults> implements WriteListener
    {
        public void complete ( final WriteResult result )
        {
            setResult ( new WriteAttributeResults () );
        }

        public void complete ( final WriteAttributeResults results )
        {
            setResult ( results );
        }

        public void failed ( final String error )
        {
            setError ( new OperationException ( error ).fillInStackTrace () );
        }

        public void error ( final Throwable error )
        {
            setError ( error );
        }
    }

    private static class WriteListenerValueImpl extends AbstractFuture<WriteResult> implements WriteListener
    {

        public void complete ( final WriteResult result )
        {
            setResult ( result );
        }

        public void complete ( final WriteAttributeResults results )
        {
            setResult ( new WriteResult () );
        }

        public void failed ( final String error )
        {
            setError ( new OperationException ( error ).fillInStackTrace () );
        }

        public void error ( final Throwable error )
        {
            setError ( error );
        }

    }

    private static interface WriteListener
    {
        public void complete ( WriteResult result );

        public void complete ( WriteAttributeResults results );

        public void failed ( String error );

        public void error ( Throwable error );
    }

    private final static Logger logger = LoggerFactory.getLogger ( MasterItemImpl.class );

    private ConnectionService connection;

    private final String itemId;

    private volatile DataItemValue value;

    private final Set<MasterItemHandler> itemHandler = new HashSet<MasterItemHandler> ();

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
            this.itemHandler.clear ();
        }
    }

    public synchronized void notifyDataChange ( final Variant value, final Map<String, Variant> attributes, final boolean cache )
    {
        logger.debug ( "Data update: {} -> {} / {} (cache: {})", new Object[] { this.itemId, value, attributes, cache } );
        // re-process
        updateData ( this.value = processHandler ( applyDataChange ( value, attributes, cache ) ) );
    }

    private DataItemValue applyDataChange ( final Variant value, final Map<String, Variant> attributes, final boolean cache )
    {
        final DataItemValue.Builder newValue = new DataItemValue.Builder ( this.value );

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
        return newValue.build ();
    }

    public synchronized void notifySubscriptionChange ( final SubscriptionState state, final Throwable error )
    {
        logger.info ( "Subscription state changed: " + state );

        // re-process
        updateData ( this.value = processHandler ( applyStateChange ( state, error ) ) );
    }

    private DataItemValue applyStateChange ( final SubscriptionState state, final Throwable error )
    {
        final DataItemValue.Builder newValue = new DataItemValue.Builder ( this.value );
        newValue.setSubscriptionState ( state );
        newValue.setSubscriptionError ( error );
        return newValue.build ();
    }

    public synchronized void addHandler ( final MasterItemHandler handler, final int priority )
    {
        logger.debug ( "Adding handler: {}/{}", new Object[] { handler, priority } );

        if ( this.itemHandler.add ( handler ) )
        {
            logger.debug ( "Added handler: {}/{}", new Object[] { handler, priority } );

            // re-process
            reprocess ();
        }
    }

    public synchronized void reprocess ()
    {
        this.executor.execute ( new Runnable () {

            public void run ()
            {
                MasterItemImpl.this.handleReprocess ();
            }
        } );
    }

    protected synchronized void handleReprocess ()
    {
        logger.info ( "Reprocessing" );
        updateData ( processHandler ( this.value ) );
    }

    /* (non-Javadoc)
     * @see org.openscada.da.master.interal.MasterImpl#removeHandler(org.openscada.da.master.MasterItemHandler)
     */
    public synchronized void removeHandler ( final MasterItemHandler handler )
    {
        if ( this.itemHandler.remove ( handler ) )
        {
            logger.debug ( "Removed handler: {}", handler );
            reprocess ();
        }
    }

    protected synchronized DataItemValue processHandler ( DataItemValue value )
    {
        for ( final MasterItemHandler subCondition : this.itemHandler )
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
        final WriteListenerValueImpl task = new WriteListenerValueImpl ();
        processWrite ( new WriteRequest ( value ), task );
        return task;
    }

    public NotifyFuture<WriteAttributeResults> startWriteAttributes ( final Map<String, Variant> attributes )
    {
        final WriteListenerAttributeImpl task = new WriteListenerAttributeImpl ();
        processWrite ( new WriteRequest ( attributes ), task );
        return task;
    }

    private void processWrite ( final WriteRequest writeRequest, final WriteListener listener )
    {
        // FIXME: combined value and attribute writes will be a problem
        try
        {
            final WriteRequestResult result = preProcessWrite ( writeRequest );
            final Throwable error = result.getError ();

            if ( error != null )
            {
                listener.error ( error );
                return;
            }

            // process value
            final Variant value = result.getValue ();
            if ( value != null )
            {
                this.connection.getConnection ().write ( this.itemId, value, new WriteOperationCallback () {

                    public void failed ( final String error )
                    {
                        listener.failed ( error );
                    }

                    public void error ( final Throwable e )
                    {
                        listener.error ( e );
                    }

                    public void complete ()
                    {
                        listener.complete ( new WriteResult () );
                    }
                } );
            }
            // process attributes
            final HashMap<String, Variant> attributes = result.getAttributes ();
            if ( !attributes.isEmpty () )
            {
                this.connection.getConnection ().writeAttributes ( this.itemId, attributes, new WriteAttributeOperationCallback () {

                    public void failed ( final String error )
                    {
                        listener.failed ( error );
                    }

                    public void error ( final Throwable e )
                    {
                        listener.error ( e );
                    }

                    public void complete ( final WriteAttributeResults callbackResult )
                    {
                        listener.complete ( mergeResults ( callbackResult, result.getAttributeResults () ) );
                    }
                } );
            }
            else if ( !result.getAttributeResults ().isEmpty () )
            {
                listener.complete ( result.getAttributeResults () );
            }
        }
        catch ( final Throwable e )
        {
            // total failure
            listener.error ( e );
        }
    }

    /**
     * Merge the two result sets
     * @param firstResult first set
     * @param secondResult second set
     * @return the merged result
     */
    protected WriteAttributeResults mergeResults ( final WriteAttributeResults firstResult, final WriteAttributeResults secondResult )
    {
        final WriteAttributeResults newResults = new WriteAttributeResults ();
        if ( firstResult != null )
        {
            newResults.putAll ( firstResult );
        }
        if ( secondResult != null )
        {
            newResults.putAll ( secondResult );
        }
        return newResults;
    }

    private WriteRequestResult preProcessWrite ( final WriteRequest writeRequest )
    {
        final ArrayList<MasterItemHandler> handlers;
        synchronized ( this )
        {
            handlers = new ArrayList<MasterItemHandler> ( this.itemHandler );
        }

        WriteRequest request = writeRequest;
        WriteRequestResult finalResult = new WriteRequestResult ( writeRequest.getValue (), writeRequest.getAttributes (), null );
        for ( final MasterItemHandler handler : handlers )
        {
            final WriteRequestResult nextResult = handler.processWrite ( request );

            if ( nextResult != null )
            {
                finalResult = nextResult;
                final HashMap<String, Variant> nextAttributes = finalResult.getAttributes ();

                // remove all attribute requests for which we have a result
                for ( final Map.Entry<String, WriteAttributeResult> entry : finalResult.getAttributeResults ().entrySet () )
                {
                    nextAttributes.remove ( entry.getKey () );
                }

                request = new WriteRequest ( finalResult.getValue (), nextAttributes );
            }
        }

        return finalResult;
    }

    public void update ( final Map<String, String> properties )
    {
        // FIXME: implement
    }

}
