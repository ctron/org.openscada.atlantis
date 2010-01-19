package org.openscada.da.master.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
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

    private volatile DataItemValue sourceValue;

    private static class HandlerEntry implements Comparable<HandlerEntry>
    {
        private final MasterItemHandler handler;

        private final int priority;

        public HandlerEntry ( final MasterItemHandler handler, final int priority )
        {
            this.handler = handler;
            this.priority = priority;
        }

        public MasterItemHandler getHandler ()
        {
            return this.handler;
        }

        public int compareTo ( final HandlerEntry o )
        {
            return this.priority - o.priority;
        }

        @Override
        public int hashCode ()
        {
            final int prime = 31;
            int result = 1;
            result = prime * result + ( this.handler == null ? 0 : this.handler.hashCode () );
            return result;
        }

        @Override
        public boolean equals ( final Object obj )
        {
            if ( this == obj )
            {
                return true;
            }
            if ( obj == null )
            {
                return false;
            }
            if ( getClass () != obj.getClass () )
            {
                return false;
            }
            final HandlerEntry other = (HandlerEntry)obj;
            if ( this.handler == null )
            {
                if ( other.handler != null )
                {
                    return false;
                }
            }
            else if ( !this.handler.equals ( other.handler ) )
            {
                return false;
            }
            return true;
        }

        public int getPriority ()
        {
            return this.priority;
        }
    }

    private final List<HandlerEntry> itemHandler = new LinkedList<HandlerEntry> ();

    private final BundleContext context;

    private final ConnectionIdTracker tracker;

    private final Executor executor;

    public MasterItemImpl ( final Executor executor, final BundleContext context, final String id, final String connectionId, final String itemId ) throws InvalidSyntaxException
    {
        this.executor = executor;
        this.context = context;
        this.itemId = itemId;
        this.sourceValue = new DataItemValue ();

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

        synchronized ( this.itemHandler )
        {
            this.itemHandler.clear ();
        }
    }

    public synchronized void notifyDataChange ( final Variant value, final Map<String, Variant> attributes, final boolean cache )
    {
        logger.debug ( "Data update: {} -> {} / {} (cache: {})", new Object[] { this.itemId, value, attributes, cache } );
        // re-process
        this.sourceValue = applyDataChange ( value, attributes, cache );
        updateData ( processHandler ( this.sourceValue ) );
    }

    private DataItemValue applyDataChange ( final Variant value, final Map<String, Variant> attributes, final boolean cache )
    {
        final DataItemValue.Builder newValue = new DataItemValue.Builder ( this.sourceValue );

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
        this.sourceValue = applyStateChange ( this.sourceValue, state, error );
        updateData ( processHandler ( this.sourceValue ) );
    }

    private static DataItemValue applyStateChange ( final DataItemValue sourceValue, final SubscriptionState state, final Throwable error )
    {
        final DataItemValue.Builder newValue = new DataItemValue.Builder ( sourceValue );
        newValue.setSubscriptionState ( state );
        newValue.setSubscriptionError ( error );
        return newValue.build ();
    }

    public void addHandler ( final MasterItemHandler handler, final int priority )
    {
        synchronized ( this.itemHandler )
        {
            logger.debug ( "Adding handler: {}/{}", new Object[] { handler, priority } );

            final HandlerEntry entry = new HandlerEntry ( handler, priority );
            if ( this.itemHandler.contains ( entry ) )
            {
                return;
            }

            this.itemHandler.add ( entry );
            Collections.sort ( this.itemHandler );

            logger.debug ( "Added handler: {}/{}", new Object[] { handler, priority } );
        }
        // re-process
        reprocess ();
    }

    public void reprocess ()
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
        updateData ( processHandler ( this.sourceValue ) );
    }

    /* (non-Javadoc)
     * @see org.openscada.da.master.interal.MasterImpl#removeHandler(org.openscada.da.master.MasterItemHandler)
     */
    public void removeHandler ( final MasterItemHandler handler )
    {
        synchronized ( this.itemHandler )
        {
            logger.debug ( "Before - Handlers: {}", this.itemHandler.size () );

            if ( this.itemHandler.remove ( new HandlerEntry ( handler, 0 ) ) )
            {
                logger.debug ( "Removed handler: {}", handler );
                reprocess ();
            }

            logger.debug ( "After - Handlers: {}", this.itemHandler.size () );
        }
    }

    protected DataItemValue processHandler ( DataItemValue value )
    {
        logger.debug ( "Processing handlers" );

        ArrayList<HandlerEntry> handler;
        synchronized ( this.itemHandler )
        {
            handler = new ArrayList<HandlerEntry> ( this.itemHandler );
        }

        for ( final HandlerEntry entry : handler )
        {
            logger.debug ( "Process: {} -> {}", new Object[] { entry.getPriority (), entry.getHandler () } );
            final DataItemValue newValue = entry.getHandler ().dataUpdate ( value );
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
        final HandlerEntry[] handlers;
        synchronized ( this )
        {
            handlers = this.itemHandler.toArray ( new HandlerEntry[this.itemHandler.size ()] );
        }

        WriteRequest request = writeRequest;
        WriteRequestResult finalResult = new WriteRequestResult ( writeRequest.getValue (), writeRequest.getAttributes (), null );
        for ( final HandlerEntry handler : handlers )
        {
            final WriteRequestResult nextResult = handler.getHandler ().processWrite ( request );

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
