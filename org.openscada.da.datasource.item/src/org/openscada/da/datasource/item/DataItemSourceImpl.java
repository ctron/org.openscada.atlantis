package org.openscada.da.datasource.item;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;

import org.openscada.ca.ConfigurationDataHelper;
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
import org.openscada.da.client.DataItemValue.Builder;
import org.openscada.da.client.connection.service.ConnectionService;
import org.openscada.da.core.WriteAttributeResults;
import org.openscada.da.core.WriteResult;
import org.openscada.da.datasource.WriteInformation;
import org.openscada.da.datasource.base.AbstractDataSource;
import org.openscada.utils.concurrent.AbstractFuture;
import org.openscada.utils.concurrent.InstantErrorFuture;
import org.openscada.utils.concurrent.NotifyFuture;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DataItemSourceImpl extends AbstractDataSource implements ItemUpdateListener
{

    private final static Logger logger = LoggerFactory.getLogger ( DataItemSourceImpl.class );

    private static class WriteListenerAttributeImpl extends AbstractFuture<WriteAttributeResults> implements WriteAttributeOperationCallback
    {
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

    private static class WriteListenerValueImpl extends AbstractFuture<WriteResult> implements WriteOperationCallback
    {

        public void failed ( final String error )
        {
            setError ( new OperationException ( error ).fillInStackTrace () );
        }

        public void error ( final Throwable error )
        {
            setError ( error );
        }

        public void complete ()
        {
            setResult ( new WriteResult () );
        }

    }

    private String itemId;

    private String connectionId;

    private final BundleContext context;

    private ConnectionIdTracker tracker;

    private ConnectionService connection;

    private DataItemValue sourceValue;

    private final Executor executor;

    private boolean debug;

    public DataItemSourceImpl ( final BundleContext context, final Executor executor )
    {
        this.context = context;
        this.executor = executor;

        fireValueChange ( new Builder () );
    }

    @Override
    protected Executor getExecutor ()
    {
        return this.executor;
    }

    public synchronized void dispose ()
    {
        disconnect ();
    }

    private void disconnect ()
    {
        if ( this.tracker != null )
        {
            this.tracker.close ();
            this.tracker = null;
        }
    }

    public synchronized void update ( final Map<String, String> parameters )
    {
        final ConfigurationDataHelper cfg = new ConfigurationDataHelper ( parameters );

        disconnect ();

        this.itemId = cfg.getStringChecked ( "item.id", "'item.id' must be set" );
        this.connectionId = cfg.getStringChecked ( "connection.id", "'connection.id' must be checked" );
        this.debug = cfg.getBoolean ( "debug", false );

        connect ();
    }

    private void connect ()
    {
        this.tracker = new ConnectionIdTracker ( this.context, this.connectionId, new ConnectionTracker.Listener () {

            public void setConnection ( final org.openscada.core.connection.provider.ConnectionService connectionService )
            {
                DataItemSourceImpl.this.setConnection ( (ConnectionService)connectionService );
            }
        }, ConnectionService.class );

        this.tracker.open ();
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

        fireValueChange ( new Builder () );

        if ( this.connection != null )
        {
            // and connect to it
            this.connection.getItemManager ().addItemUpdateListener ( this.itemId, this );
        }
    }

    private void fireValueChange ( final DataItemValue.Builder builder )
    {
        this.sourceValue = builder.build ();
        injectAttributes ( builder );
        updateData ( this.sourceValue );
    }

    public synchronized void notifyDataChange ( final Variant value, final Map<String, Variant> attributes, final boolean cache )
    {
        logger.debug ( "Data update: {} -> {} / {} (cache: {})", new Object[] { this.itemId, value, attributes, cache } );
        fireValueChange ( applyDataChange ( value, attributes, cache ) );
    }

    private DataItemValue.Builder applyDataChange ( final Variant value, final Map<String, Variant> attributes, final boolean cache )
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

        injectAttributes ( newValue );

        return newValue;
    }

    private void injectAttributes ( final Builder newValue )
    {
        if ( this.debug )
        {
            newValue.setAttribute ( "source.hasConnection", this.connection != null ? Variant.TRUE : Variant.FALSE );
            newValue.setAttribute ( "source.item.subscriptionState", new Variant ( newValue.getSubscriptionState ().toString () ) );
        }

        newValue.setAttribute ( "source.itemId", new Variant ( this.itemId ) );
        newValue.setAttribute ( "source.connectionId", new Variant ( this.connectionId ) );

        newValue.setAttribute ( "source.error", newValue.getSubscriptionState () != SubscriptionState.CONNECTED ? Variant.TRUE : Variant.FALSE );
    }

    public synchronized void notifySubscriptionChange ( final SubscriptionState state, final Throwable error )
    {
        logger.info ( "Subscription state changed: {}", state );

        // re-process
        fireValueChange ( applyStateChange ( this.sourceValue, state, error ) );
    }

    private DataItemValue.Builder applyStateChange ( final DataItemValue sourceValue, final SubscriptionState state, final Throwable error )
    {
        final DataItemValue.Builder newValue = new DataItemValue.Builder ( sourceValue );
        newValue.setSubscriptionState ( state );
        newValue.setSubscriptionError ( error );

        injectAttributes ( newValue );

        return newValue;
    }

    public synchronized NotifyFuture<WriteResult> startWriteValue ( final WriteInformation writeInformation, final Variant value )
    {
        final WriteListenerValueImpl task = new WriteListenerValueImpl ();

        final ConnectionService connection = this.connection;
        if ( connection != null )
        {
            connection.getConnection ().write ( this.itemId, value, task );
        }
        else
        {
            return new InstantErrorFuture<WriteResult> ( new OperationException ( "No connection" ).fillInStackTrace () );
        }
        return task;
    }

    public synchronized NotifyFuture<WriteAttributeResults> startWriteAttributes ( final WriteInformation writeInformation, final Map<String, Variant> attributes )
    {
        final WriteListenerAttributeImpl task = new WriteListenerAttributeImpl ();

        final ConnectionService connection = this.connection;
        if ( connection != null )
        {
            connection.getConnection ().writeAttributes ( this.itemId, attributes, task );
        }
        else
        {
            return new InstantErrorFuture<WriteAttributeResults> ( new OperationException ( "No connection" ).fillInStackTrace () );
        }

        return task;
    }

}
