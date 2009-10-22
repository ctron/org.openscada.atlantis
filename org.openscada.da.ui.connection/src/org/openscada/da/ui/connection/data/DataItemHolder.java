package org.openscada.da.ui.connection.data;

import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import org.openscada.core.ConnectionInformation;
import org.openscada.core.Variant;
import org.openscada.core.connection.provider.ConnectionRequest;
import org.openscada.core.connection.provider.ConnectionRequestTracker;
import org.openscada.core.connection.provider.ConnectionTracker;
import org.openscada.da.client.DataItem;
import org.openscada.da.client.DataItemValue;
import org.openscada.da.client.WriteAttributeOperationCallback;
import org.openscada.da.client.WriteOperationCallback;
import org.openscada.da.client.connection.service.ConnectionService;
import org.openscada.da.core.WriteAttributeResults;
import org.openscada.utils.concurrent.AbstractFuture;
import org.openscada.utils.concurrent.NotifyFuture;
import org.osgi.framework.BundleContext;

public class DataItemHolder
{
    private final class WriteFuture extends AbstractFuture<Object>
    {
        @Override
        public void setError ( final Throwable error )
        {
            super.setError ( error );
        }

        @Override
        protected void setResult ( final Object result )
        {
            super.setResult ( result );
        }
    }

    private final class WriteAttributesFuture extends AbstractFuture<WriteAttributeResults>
    {
        @Override
        public void setError ( final Throwable error )
        {
            super.setError ( error );
        }

        @Override
        protected void setResult ( final WriteAttributeResults result )
        {
            super.setResult ( result );
        }
    }

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

        this.tracker = new ConnectionRequestTracker ( this.context, createRequest (), new ConnectionTracker.Listener () {

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

    public NotifyFuture<Object> write ( final Variant value )
    {
        final WriteFuture writeResult = new WriteFuture ();

        this.connection.getConnection ().write ( this.item.getId (), value, new WriteOperationCallback () {

            public void failed ( final String error )
            {
                writeResult.setError ( new RuntimeException ( error ).fillInStackTrace () );
            }

            public void error ( final Throwable e )
            {
                writeResult.setError ( e );
            }

            public void complete ()
            {
                writeResult.setResult ( null );
            }
        } );

        return writeResult;
    }

    public NotifyFuture<WriteAttributeResults> writeAtrtibutes ( final Map<String, Variant> attributes )
    {
        final WriteAttributesFuture writeResult = new WriteAttributesFuture ();

        this.connection.getConnection ().writeAttributes ( this.item.getId (), attributes, new WriteAttributeOperationCallback () {

            public void failed ( final String error )
            {
                writeResult.setError ( new RuntimeException ( error ).fillInStackTrace () );
            }

            public void error ( final Throwable e )
            {
                writeResult.setError ( e );
            }

            public void complete ( final WriteAttributeResults result )
            {
                writeResult.setResult ( result );
            }
        } );

        return writeResult;
    }

    public Item getItem ()
    {
        return this.item;
    }

    public boolean waitForConnection ( final long timeout ) throws InterruptedException
    {
        return this.tracker.waitForService ( timeout );
    }
}
