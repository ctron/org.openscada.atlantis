package org.openscada.da.ui.connection.data;

import java.util.Observable;
import java.util.Observer;

import org.openscada.core.ConnectionInformation;
import org.openscada.core.connection.provider.ConnectionRequest;
import org.openscada.core.connection.provider.ConnectionTracker;
import org.openscada.da.client.DataItem;
import org.openscada.da.client.DataItemValue;
import org.openscada.da.connection.provider.ConnectionService;
import org.osgi.framework.BundleContext;

public class DataItemHolder
{
    private final Item item;

    private final BundleContext context;

    private final ConnectionTracker tracker;

    private final DataSourceListener listener;

    private ConnectionService connection;

    private DataItem dataItem;

    private final Observer observer;

    public DataItemHolder ( final BundleContext context, final Item item, final DataSourceListener listener )
    {
        this.context = context;
        this.item = item;

        this.listener = listener;

        this.observer = new Observer () {

            public void update ( final Observable o, final Object arg )
            {
                DataItemHolder.this.update ( o, arg );
            }
        };

        this.tracker = new ConnectionTracker ( this.context, createRequest (), new ConnectionTracker.Listener () {

            public void setConnection ( final org.openscada.core.connection.provider.ConnectionService connectionService )
            {
                DataItemHolder.this.setConnection ( (ConnectionService)connectionService );
            }
        } );
        this.tracker.listen ();

    }

    protected void update ( final Observable o, final Object arg )
    {
        if ( o != this.dataItem )
        {
            return;
        }
        if ( ! ( arg instanceof DataItemValue ) )
        {
            return;
        }
        fireListenerChange ( (DataItemValue)arg );
    }

    protected synchronized void setConnection ( final ConnectionService connectionService )
    {
        clearConnection ();
        createConnection ( connectionService );
    }

    private synchronized void createConnection ( final ConnectionService connectionService )
    {
        this.connection = connectionService;
        if ( this.connection != null )
        {
            this.dataItem = new DataItem ( this.item.getId () );
            this.dataItem.register ( this.connection.getItemManager () );
            this.dataItem.addObserver ( this.observer );
        }
    }

    private synchronized void clearConnection ()
    {
        if ( this.dataItem != null )
        {
            this.dataItem.deleteObserver ( this.observer );
            this.dataItem.unregister ();
            this.dataItem = null;
        }
        if ( this.connection != null )
        {
            this.connection = null;
        }
        fireListenerChange ( null );
    }

    private synchronized void fireListenerChange ( final DataItemValue value )
    {
        if ( this.listener != null )
        {
            this.listener.updateData ( value );
        }
    }

    private ConnectionRequest createRequest ()
    {
        return new ConnectionRequest ( null, ConnectionInformation.fromURI ( this.item.getConnectionString () ), null, false );
    }

    public synchronized void dispose ()
    {
        clearConnection ();
        this.tracker.close ();
    }

    public Item getItem ()
    {
        return this.item;
    }
}
