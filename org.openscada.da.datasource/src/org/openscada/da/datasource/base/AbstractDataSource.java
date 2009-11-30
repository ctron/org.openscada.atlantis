package org.openscada.da.datasource.base;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executor;

import org.openscada.da.client.DataItemValue;
import org.openscada.da.datasource.DataSource;
import org.openscada.da.datasource.DataSourceListener;

public abstract class AbstractDataSource implements DataSource
{
    private DataItemValue value;

    protected abstract Executor getExecutor ();

    private final Set<DataSourceListener> listeners = new HashSet<DataSourceListener> ();

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

    protected synchronized void updateData ( final DataItemValue value )
    {
        final Set<DataSourceListener> listeners = new HashSet<DataSourceListener> ( this.listeners );
        getExecutor ().execute ( new Runnable () {

            public void run ()
            {
                for ( final DataSourceListener listener : listeners )
                {
                    listener.stateChanged ( value );
                }
            }
        } );

    }

}