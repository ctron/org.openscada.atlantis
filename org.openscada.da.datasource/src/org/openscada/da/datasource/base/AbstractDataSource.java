package org.openscada.da.datasource.base;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executor;

import org.openscada.core.Variant;
import org.openscada.da.client.DataItemValue;
import org.openscada.da.datasource.DataSource;
import org.openscada.da.datasource.DataSourceListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Basic implementation of a data source.
 * @author Jens Reimann
 * @since 0.15.0
 *
 */
public abstract class AbstractDataSource implements DataSource
{
    private final static Logger logger = LoggerFactory.getLogger ( AbstractDataSource.class );

    private DataItemValue value;

    protected abstract Executor getExecutor ();

    private final Set<DataSourceListener> listeners = new HashSet<DataSourceListener> ();

    private Variant lastValue = Variant.NULL;

    public synchronized void addListener ( final DataSourceListener listener )
    {
        if ( this.listeners.add ( listener ) )
        {
            final DataItemValue value = this.value;
            getExecutor ().execute ( new Runnable () {

                public void run ()
                {
                    listener.stateChanged ( value );
                }
            } );
        }
    }

    public synchronized void removeListener ( final DataSourceListener listener )
    {
        this.listeners.remove ( listener );
    }

    protected synchronized void updateData ( DataItemValue value )
    {
        logger.debug ( "Update data: {} -> {}", new Object[] { value, value.getAttributes () } );

        if ( this.value != null )
        {
            if ( this.value.equals ( value ) )
            {
                logger.debug ( "No data change. Discarding" );
                return;
            }
        }

        if ( value.getTimestamp () == null )
        {
            try
            {
                if ( !this.lastValue.equals ( value.getValue () ) )
                {
                    final DataItemValue.Builder builder = new DataItemValue.Builder ( value );
                    builder.setTimestamp ( Calendar.getInstance () );
                    value = builder.build ();
                }
            }
            catch ( final Exception e )
            {
                // nothing
            }
        }

        this.lastValue = value.getValue ();
        this.value = value;

        final DataItemValue finalValue = value;

        final Set<DataSourceListener> listeners = new HashSet<DataSourceListener> ( this.listeners );
        getExecutor ().execute ( new Runnable () {

            public void run ()
            {
                for ( final DataSourceListener listener : listeners )
                {
                    listener.stateChanged ( finalValue );
                }
            }
        } );

    }

}