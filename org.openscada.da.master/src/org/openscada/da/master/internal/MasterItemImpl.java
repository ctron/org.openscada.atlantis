package org.openscada.da.master.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;

import org.openscada.core.OperationException;
import org.openscada.core.Variant;
import org.openscada.da.client.DataItemValue;
import org.openscada.da.client.DataItemValue.Builder;
import org.openscada.da.core.WriteAttributeResult;
import org.openscada.da.core.WriteAttributeResults;
import org.openscada.da.core.WriteResult;
import org.openscada.da.datasource.DataSource;
import org.openscada.da.datasource.WriteInformation;
import org.openscada.da.datasource.base.AbstractDataSourceHandler;
import org.openscada.da.master.MasterItem;
import org.openscada.da.master.MasterItemHandler;
import org.openscada.da.master.WriteRequest;
import org.openscada.da.master.WriteRequestResult;
import org.openscada.utils.concurrent.AbstractFuture;
import org.openscada.utils.concurrent.FutureListener;
import org.openscada.utils.concurrent.NotifyFuture;
import org.openscada.utils.osgi.pool.ObjectPoolTracker;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MasterItemImpl extends AbstractDataSourceHandler implements MasterItem
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

    private final Executor executor;

    public MasterItemImpl ( final Executor executor, final BundleContext context, final String id, final ObjectPoolTracker dataSourcePoolTracker ) throws InvalidSyntaxException
    {
        super ( dataSourcePoolTracker );
        this.executor = executor;
        this.sourceValue = initValue ();
    }

    private static DataItemValue initValue ()
    {
        final DataItemValue.Builder builder = new Builder ();
        builder.setAttribute ( "master.uninitialized", Variant.TRUE );
        return builder.build ();
    }

    @Override
    protected Executor getExecutor ()
    {
        return this.executor;
    }

    public void dispose ()
    {
        synchronized ( this.itemHandler )
        {
            this.itemHandler.clear ();
        }
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

    @Override
    protected synchronized void stateChanged ( final DataItemValue value )
    {
        this.sourceValue = value;
        reprocess ();
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

        final Map<String, Object> context = new HashMap<String, Object> ();

        for ( final HandlerEntry entry : handler )
        {
            logger.debug ( "Process: {} -> {}", new Object[] { entry.getPriority (), entry.getHandler () } );
            final DataItemValue newValue = entry.getHandler ().dataUpdate ( context, value );
            if ( newValue != null )
            {
                value = newValue;
            }
        }
        return value;
    }

    public synchronized NotifyFuture<WriteResult> startWriteValue ( final WriteInformation writeInformation, final Variant value )
    {
        final WriteListenerValueImpl task = new WriteListenerValueImpl ();
        processWrite ( new WriteRequest ( writeInformation, value ), task );
        return task;
    }

    public synchronized NotifyFuture<WriteAttributeResults> startWriteAttributes ( final WriteInformation writeInformation, final Map<String, Variant> attributes )
    {
        final WriteListenerAttributeImpl task = new WriteListenerAttributeImpl ();
        processWrite ( new WriteRequest ( writeInformation, attributes ), task );
        return task;
    }

    private void processWrite ( final WriteRequest writeRequest, final WriteListener listener )
    {

        final DataSource dataSource = getDataSource ();
        if ( dataSource == null )
        {
            listener.error ( new OperationException ( "No connection" ).fillInStackTrace () );
            return;
        }

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
                final NotifyFuture<WriteResult> task = dataSource.startWriteValue ( writeRequest.getWriteInformation (), value );
                task.addListener ( new FutureListener<WriteResult> () {

                    public void complete ( final Future<WriteResult> future )
                    {
                        try
                        {
                            listener.complete ( future.get () );
                        }
                        catch ( final Throwable e )
                        {
                            listener.error ( e );
                        }
                    }
                } );
            }
            else
            {
                listener.complete ( new WriteResult () );
            }

            // process attributes
            final HashMap<String, Variant> attributes = result.getAttributes ();
            if ( !attributes.isEmpty () )
            {
                final NotifyFuture<WriteAttributeResults> task = dataSource.startWriteAttributes ( writeRequest.getWriteInformation (), attributes );
                task.addListener ( new FutureListener<WriteAttributeResults> () {

                    public void complete ( final Future<WriteAttributeResults> future )
                    {
                        try
                        {
                            listener.complete ( future.get () );
                        }
                        catch ( final Throwable e )
                        {
                            listener.error ( e );
                        }
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
                // add previous attribute results first
                nextResult.getAttributeResults ().putAll ( finalResult.getAttributeResults () );

                finalResult = nextResult;
                final HashMap<String, Variant> nextAttributes = finalResult.getAttributes ();

                // remove all attribute requests for which we have a result
                for ( final Map.Entry<String, WriteAttributeResult> entry : finalResult.getAttributeResults ().entrySet () )
                {
                    nextAttributes.remove ( entry.getKey () );
                }

                request = new WriteRequest ( writeRequest.getWriteInformation (), finalResult.getValue (), nextAttributes );
            }
        }

        return finalResult;
    }

    public synchronized void update ( final Map<String, String> properties ) throws InvalidSyntaxException
    {
        setDataSource ( properties.get ( "datasource.id" ) );

        final DataItemValue.Builder builder = new Builder ();
        builder.setAttribute ( "master.waitingFor.datasource", new Variant ( properties.get ( "datasource.id" ) ) );
        this.sourceValue = builder.build ();
        reprocess ();
    }

}
