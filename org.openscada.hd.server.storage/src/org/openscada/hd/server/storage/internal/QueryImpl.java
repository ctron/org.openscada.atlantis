package org.openscada.hd.server.storage.internal;

import java.util.concurrent.FutureTask;

import org.openscada.hd.Query;
import org.openscada.hd.QueryListener;
import org.openscada.hd.QueryParameters;
import org.openscada.hd.QueryState;
import org.openscada.hd.server.storage.ShiService;
import org.openscada.hsdb.ExtendedStorageChannel;
import org.openscada.hsdb.StorageChannelMetaData;
import org.openscada.hsdb.datatypes.DoubleValue;
import org.openscada.hsdb.datatypes.LongValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is the internal implementation of the HD query interface.
 * @see org.openscada.hd.Query
 * @author Ludwig Straub
 */
public class QueryImpl implements Query, ExtendedStorageChannel, Runnable
{
    /** The default logger. */
    private final static Logger logger = LoggerFactory.getLogger ( QueryImpl.class );

    /** Time span between two consecutive calls of the future task. */
    private final static long DELAY_BETWEEN_TWO_QUERY_CALCULATIONS = 1000;

    /** Service that created the query object. */
    private final ShiService service;

    /** Listener that should receive the data. */
    private final QueryListener listener;

    /** Input parameters of the query. */
    private QueryParameters parameters;

    /** Flag indicating whether the result should be periodically updated or not. */
    private final boolean updateDataPeriodically;

    /** Flag indicating whether the query is closed or not. */
    private boolean closed;

    /** Flag indicating whether the query has to be executed again due to data changes. */
    private boolean updateRequired;

    /** Task that will calculate the result. */
    private FutureTask<Object> futureTask;

    /**
     * Constructor.
     * @param service service that created the query object
     * @param listener listener that should receive the data
     * @param parameters input parameters of the query
     * @param updateData flag indicating whether the result should be periodically updated or not
     */
    public QueryImpl ( final ShiService service, final QueryListener listener, final QueryParameters parameters, final boolean updateData )
    {
        this.service = service;
        this.listener = listener;
        this.parameters = parameters;
        this.updateDataPeriodically = updateData;
        this.closed = ( service == null ) || ( listener == null ) || ( parameters == null );
        if ( closed )
        {
            logger.error ( "not all data is available to execute query. no action will be performed" );
            if ( listener != null )
            {
                listener.updateState ( QueryState.DISCONNECTED );
            }
        }
        this.updateRequired = true;
        listener.updateState ( QueryState.LOADING );
        checkStartTask ();
    }

    /**
     * This method checks if the
     */
    private void checkStartTask ()
    {
        if ( !closed && updateRequired && ( futureTask == null ) )
        {
            futureTask = new FutureTask<Object> ( this, null );
        }
    }

    /**
     * This method processes the query.
     */
    public void run ()
    {
        // set the state to loading
        listener.updateState ( QueryState.LOADING );

        // retrieve data of level that is suitable for the requested time span
        synchronized ( service )
        {

        }

        // normalize retrieved data

        // send data to listener
        synchronized ( this )
        {
            if ( closed )
            {
                return;
            }
            listener.updateState ( QueryState.COMPLETE );
        }

        // wait for some time
        try
        {
            Thread.sleep ( DELAY_BETWEEN_TWO_QUERY_CALCULATIONS );
        }
        catch ( Exception e )
        {
            logger.debug ( "query thread was interrupted" );
        }

        // free related future task and restart query if new data is available
        synchronized ( this )
        {
            futureTask = null;
            checkStartTask ();
        }
    }

    /**
     * This method marks the values that are affected by the specified time as changed
     * @param time time at which the affected values have to be marked as changed
     */
    private void markTimeAsDirty ( final long time )
    {
        if ( updateDataPeriodically && !closed )
        {
            updateRequired = true;
        }
    }

    /**
     * @see org.openscada.hd.Query#changeParameters
     */
    public synchronized void changeParameters ( final QueryParameters parameters )
    {
        this.parameters = parameters;
        updateRequired = true;
        checkStartTask ();
    }

    /**
     * @see org.openscada.hd.Query#close
     */
    public synchronized void close ()
    {
        if ( !closed )
        {
            listener.updateState ( QueryState.DISCONNECTED );
            service.removeQuery ( this );
        }
        closed = true;
    }

    /**
     * @see org.openscada.hsdb.ExtendedStorageChannel#cleanupRelicts
     */
    public synchronized void cleanupRelicts () throws Exception
    {
        throw new UnsupportedOperationException ();
    }

    /**
     * @see org.openscada.hsdb.ExtendedStorageChannel#getLongValues
     */
    public synchronized LongValue[] getLongValues ( final long startTime, final long endTime ) throws Exception
    {
        throw new UnsupportedOperationException ();
    }

    /**
     * @see org.openscada.hsdb.ExtendedStorageChannel#getDoubleValues
     */
    public synchronized DoubleValue[] getDoubleValues ( final long startTime, final long endTime ) throws Exception
    {
        throw new UnsupportedOperationException ();
    }

    /**
     * @see org.openscada.hsdb.ExtendedStorageChannel#getMetaData
     */
    public synchronized StorageChannelMetaData getMetaData () throws Exception
    {
        throw new UnsupportedOperationException ();
    }

    /**
     * @see org.openscada.hsdb.ExtendedStorageChannel#updateLong
     */
    public synchronized void updateLong ( final LongValue longValue ) throws Exception
    {
        if ( longValue != null )
        {
            markTimeAsDirty ( longValue.getTime () );
        }
    }

    /**
     * @see org.openscada.hsdb.ExtendedStorageChannel#updateLongs
     */
    public synchronized void updateLongs ( final LongValue[] longValues ) throws Exception
    {
        if ( longValues != null )
        {
            for ( LongValue longValue : longValues )
            {
                updateLong ( longValue );
            }
        }
    }

    /**
     * @see org.openscada.hsdb.ExtendedStorageChannel#updateDouble
     */
    public synchronized void updateDouble ( final DoubleValue doubleValue ) throws Exception
    {
        if ( doubleValue != null )
        {
            markTimeAsDirty ( doubleValue.getTime () );
        }
    }

    /**
     * @see org.openscada.hsdb.ExtendedStorageChannel#updateDoubles
     */
    public synchronized void updateDoubles ( final DoubleValue[] doubleValues ) throws Exception
    {
        if ( doubleValues != null )
        {
            for ( DoubleValue doubleValue : doubleValues )
            {
                updateDouble ( doubleValue );
            }
        }
    }
}
