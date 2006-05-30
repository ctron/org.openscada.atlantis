package org.openscada.da.client.net;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.openscada.da.client.net.operations.WriteOperation;
import org.openscada.da.client.net.operations.WriteOperationArguments;
import org.openscada.da.core.DataItemInformation;
import org.openscada.da.core.data.Variant;
import org.openscada.net.base.ClientConnection;
import org.openscada.net.base.MessageListener;
import org.openscada.net.base.MessageStateListener;
import org.openscada.net.base.data.Message;
import org.openscada.net.base.data.Value;
import org.openscada.net.da.handler.EnumEvent;
import org.openscada.net.da.handler.Messages;
import org.openscada.net.io.IOProcessor;
import org.openscada.utils.exec.OperationResult;
import org.openscada.utils.exec.OperationResultHandler;
import org.openscada.utils.lang.Holder;

public class Connection
{

    public static final String VERSION = "0.1.0";

    public enum State
    {
        CLOSED,
        LOOKUP,
        CONNECTING,
        CONNECTED,
        BOUND,
        CLOSING,
    }

    private static Logger _log = Logger.getLogger ( Connection.class );

    private ConnectionInfo _connectionInfo = null;
    private SocketAddress _remote = null;
    private IOProcessor _processor = null;

    private ClientConnection _client = null;

    private List<ConnectionStateListener> _connectionStateListeners = new ArrayList < ConnectionStateListener > ();
    private Map<String,ItemSyncController> _itemListeners = new HashMap < String, ItemSyncController > ();

    //private boolean _connected = false;
    private State _state = State.CLOSED;

    private static Object _defaultProcessorLock = new Object();
    private static IOProcessor _defaultProcessor = null;

    private ItemList _itemList = new ItemList ();
    private List<ItemListListener> _itemListListeners = new ArrayList<ItemListListener>();

    private WriteOperation _writeOperation;

    private static IOProcessor getDefaultProcessor ()
    {
        try
        {
            synchronized ( _defaultProcessorLock )
            {
                if ( _defaultProcessor == null )
                {
                    _defaultProcessor = new IOProcessor ();
                    _defaultProcessor.start();
                }
                return _defaultProcessor;
            }
        }
        catch ( IOException e )
        {    
            _log.error ( "unable to created io processor", e );
        }
        // operation failed
        return null;
    }

    public Connection ( IOProcessor processor, ConnectionInfo connectionInfo )
    {
        super();

        _processor = processor;
        _connectionInfo = connectionInfo;

        // register our own list
        addItemListListener ( _itemList );

        init ();

        _writeOperation = new WriteOperation ( this );
    }

    public Connection ( ConnectionInfo connectionInfo )
    {
        this ( getDefaultProcessor(), connectionInfo );
    }

    private void init ()
    {
        if ( _client != null )
            return;

        _client = new ClientConnection ( _processor );
        _client.addStateListener(new  org.openscada.net.io.ConnectionStateListener(){

            public void closed ( Exception error )
            {
                _log.debug ( "closed" );
                fireDisconnected ( error );
            }

            public void opened ()
            {
                _log.debug ( "opened" );
                fireConnected ();
            }});

        _client.getMessageProcessor().setHandler(Messages.CC_NOTIFY_VALUE, new MessageListener(){

            public void messageReceived ( org.openscada.net.io.Connection connection, Message message )
            {
                notifyValueChange(message);
            }} );

        _client.getMessageProcessor().setHandler(Messages.CC_NOTIFY_ATTRIBUTES, new MessageListener(){

            public void messageReceived ( org.openscada.net.io.Connection connection, Message message )
            {
                notifyAttributesChange(message);
            }});

        _client.getMessageProcessor().setHandler(Messages.CC_ENUM_EVENT, new MessageListener(){

            public void messageReceived ( org.openscada.net.io.Connection connection, Message message )
            {
                _log.debug("Enum message from server");
                performEnumEvent ( message );
            }});

    }

    synchronized public void connect ()
    {
        switch ( _state )
        {
        case CLOSED:
            setState ( State.CONNECTING, null );
            break;
        }        
    }

    synchronized public void disconnect ()
    {
        disconnect ( null );
    }

    synchronized private void disconnect ( Throwable reason )
    {
        switch ( _state )
        {
        case LOOKUP:
            setState ( State.CLOSED, reason );
            break;
            
        case BOUND:
        case CONNECTING:
        case CONNECTED:
            setState ( State.CLOSING, reason );
            break;
        }    
    }

    public void addItemListListener ( ItemListListener listener )
    {
        synchronized ( _itemListListeners )
        {
            _itemListListeners.add ( listener );
        }
    }

    public void removeItemListListener ( ItemListListener listener )
    {
        synchronized ( _itemListListeners )
        {
            _itemListListeners.remove ( listener );
        }
    }

    public void addConnectionStateListener ( ConnectionStateListener connectionStateListener )
    {
        synchronized ( _connectionStateListeners )
        {
            _connectionStateListeners.add ( connectionStateListener );
        }
    }

    public void removeConnectionStateListener ( ConnectionStateListener connectionStateListener )
    {
        synchronized ( _connectionStateListeners )
        {
            _connectionStateListeners.remove ( connectionStateListener );
        }
    }

    private void fireConnected ()
    {
        _log.debug ( "connected" );

        switch ( _state )
        {
        case CONNECTING:
            setState ( State.CONNECTED, null );
            break;
        }

    }

    private void fireDisconnected ( Throwable error )
    {
        _log.debug ( "dis-connected" );

        switch ( _state )
        {
        case BOUND:
        case CONNECTED:
        case CONNECTING:
        case LOOKUP:
        case CLOSING:
            setState ( State.CLOSED, error );
            break;
        }

    }

    private void fireItemListChange ( Collection<DataItemInformation> added, Collection<String> removed, boolean initial )
    {
        synchronized ( _itemListListeners )
        {
            _log.debug("Sending out enum events");

            for ( ItemListListener listener : _itemListListeners )
            {
                try {
                    listener.changed ( added, removed, initial );
                }
                catch ( Exception e )
                {}
            }
        }
    }

    private void requestSession ()
    {
        if ( _client == null )
            return;

        Properties props = new Properties();
        props.setProperty ( "client-version", VERSION );

        _client.getConnection().sendMessage ( Messages.createSession ( props ), new MessageStateListener(){

            public void messageReply ( Message message )
            {
                processSessionReply ( message );
            }

            public void messageTimedOut ()
            {
                setState ( State.CLOSED, new OperationTimedOutException().fillInStackTrace () );
            }} );
    }

    private void processSessionReply ( Message message )
    {
        _log.debug ( "Got session reply!" );

        if ( message.getValues ().containsKey ( Message.FIELD_ERROR_INFO ) )
        {
            String errorInfo = message.getValues ().get ( Message.FIELD_ERROR_INFO ).toString ();
            disconnect ( new DisconnectReason ( "Failed to create session: " + errorInfo ) );
        }
        else
        {
            setState ( State.BOUND, null );

            // sync again all items to maintain subscribtions
            resyncAllItems ();

            // subscribe enum service
            subscribeEnum ();
        }
    }

    private void subscribeEnum ()
    {
        if ( _client == null )
            return;

        _log.debug("Subscribing to enum");

        _client.getConnection().sendMessage(Messages.subscribeEnum());
        _log.debug("Subscribing to enum...complete");
    }

    public void addItemUpdateListener ( String itemName, boolean initial, ItemUpdateListener listener ) 
    {
        synchronized ( _itemListeners )
        {
            if (!_itemListeners.containsKey(itemName))
            {
                _itemListeners.put( itemName, new ItemSyncController(this, itemName) );
            }

            ItemSyncController controller = _itemListeners.get ( itemName );
            controller.add ( listener, initial );
        }
    }

    public void removeItemUpdateListener ( String itemName, ItemUpdateListener listener ) 
    {
        synchronized ( _itemListeners )
        {
            if (!_itemListeners.containsKey(itemName))
            {
                return;
            }

            ItemSyncController controller = _itemListeners.get(itemName);
            controller.remove(listener);
        }
    }

    /**
     * Synchronized all items that are currently known
     *
     */
    private void resyncAllItems ()
    {
        _log.debug("Syncing all items");

        synchronized ( _itemListeners )
        {
            for ( Map.Entry<String,ItemSyncController> entry : _itemListeners.entrySet() )
            {
                entry.getValue().sync(true);
            }
        }
        _log.debug("re-sync complete");
    }


    private void fireValueChange ( String itemName, Variant value, boolean initial )
    {
        synchronized ( _itemListeners )
        {
            if ( _itemListeners.containsKey(itemName) )
            {
                _itemListeners.get(itemName).fireValueChange(value,initial);
            }
        }
    }

    private void fireAttributesChange ( String itemName, Map<String,Variant> attributes, boolean initial )
    {
        synchronized ( _itemListeners )
        {
            if ( _itemListeners.containsKey ( itemName ) )
            {
                _itemListeners.get ( itemName ).fireAttributesChange(attributes,initial);
            }
        }
    }

    private void notifyValueChange ( Message message )
    {
        Variant value = new Variant ();

        // extract initial bit
        boolean initial = message.getValues().containsKey("initial");


        if ( message.getValues().containsKey("value") )
        {
            value = Messages.valueToVariant ( message.getValues().get("value"), null );
        }

        String itemName = message.getValues().get("item-name").toString();
        fireValueChange(itemName, value, initial);
    }



    private void notifyAttributesChange ( Message message )
    {

        Map<String,Variant> attributes = new HashMap<String,Variant>();

        // extract initial bit
        boolean initial = message.getValues().containsKey("initial");

        for ( Map.Entry<String,Value> entry : message.getValues().entrySet() )
        {
            String name = entry.getKey();
            if ( name.startsWith("set-") )
            {
                Variant value = Messages.valueToVariant ( entry.getValue(), null );
                name = name.substring ( "set-".length() );
                attributes.put ( name, value );
            }
            else if ( name.startsWith("null-"))
            {
                name = name.substring("null-".length());
                attributes.put(name,new Variant());
            }
            else if ( name.startsWith("unset-"))
            {
                name = name.substring("unset-".length());
                attributes.put(name,null);
            }

        }

        String itemName = message.getValues().get("item-name").toString();
        fireAttributesChange ( itemName, attributes, initial );
    }

    private void performEnumEvent ( Message message )
    {
        synchronized ( _itemList )
        {

            List<DataItemInformation> added = new ArrayList<DataItemInformation> ();
            List<String> removed = new ArrayList<String> ();
            Holder<Boolean> initial = new Holder<Boolean> ();

            EnumEvent.parse ( message, added, removed, initial );

            fireItemListChange ( added, removed, initial.value.booleanValue() );
        }

    }

    public State getState ()
    {
        return _state;
    }

    /**
     * Get the network client
     * @return the client instance of <em>null</em> if the client has not been started
     */
    public ClientConnection getClient ()
    {
        return _client;
    }

    /**
     * Get the item list. This list is maintained by the connection and will be
     * feeded with events.
     * @return the dynamic item list
     */
    public ItemList getItemList ()
    {
        synchronized ( _itemList )
        {
            return _itemList;
        }
    }

    public void write ( String itemName, Variant value ) throws Exception
    {
        _writeOperation.execute ( new WriteOperationArguments ( itemName, value ) );
    }

    public OperationResult<Object> startWrite ( String itemName, Variant value )
    {
        return _writeOperation.startExecute ( new WriteOperationArguments ( itemName, value ) );
    }

    public OperationResult<Object> startWrite ( String itemName, Variant value, OperationResultHandler<Object> handler )
    {
        return _writeOperation.startExecute ( handler, new WriteOperationArguments ( itemName, value ) );
    }

    /**
     * set new state internaly
     * @param state
     * @param error additional error information or <code>null</code> if we don't have an error.
     */
    synchronized private void setState ( State state, Throwable error )
    {
        _state = state;

        stateChanged ( state, error );
    }

    private void stateChanged ( State state, Throwable error )
    {
        switch ( state )
        {

        case CLOSED:
            // if we got the close and are auto-reconnect ... schedule the job
            if ( _connectionInfo.isAutoReconnect () )
            {
                _processor.getScheduler ().scheduleJob ( new Runnable() {

                    public void run ()
                    {
                        connect ();
                    }}, _connectionInfo.getReconnectDelay () );
            }
            break;

        case CONNECTING:
            performConnect ();
            break;
            
        case LOOKUP:
            break;
            
        case CONNECTED:
            requestSession ();
            break;

        case BOUND:
            break;

        case CLOSING:
            _client.disconnect ();
            break;
        }

        notifyStateChange ( state, error );

    }



    /**
     * Notify state change listeners
     * @param state new state
     * @param error additional error information or <code>null</code> if we don't have an error. 
     */
    private void notifyStateChange ( State state, Throwable error )
    {   
        List<ConnectionStateListener> connectionStateListeners;

        synchronized ( _connectionStateListeners )
        {
            connectionStateListeners = new ArrayList<ConnectionStateListener> ( _connectionStateListeners );
        }
        for ( ConnectionStateListener listener : connectionStateListeners )
        {
            try
            {
                listener.stateChange ( this, state, error );
            }
            catch ( Exception e )
            {
            }
        }
    }

    synchronized private void performConnect ()
    {
        if ( _remote != null )
        {
            _client.connect ( _remote );
        }
        else
        {
            setState ( State.LOOKUP, null );
            Thread lookupThread = new Thread ( new Runnable() {

                public void run ()
                {
                    performLookupAndConnect ();
                }} );
            lookupThread.setDaemon ( true );
            lookupThread.start ();
        }
    }

    private void performLookupAndConnect ()
    {
        // lookup may take some time
        try
        {
            SocketAddress remote = new InetSocketAddress ( InetAddress.getByName ( _connectionInfo.getHostName () ), _connectionInfo.getPort () );
            _remote = remote;
            // this time "remote" should not be null
            synchronized ( this )
            {
                if ( _state.equals ( State.LOOKUP ) )
                    setState ( State.CONNECTING, null );
            }
        }
        catch ( UnknownHostException e )
        {
            synchronized ( this )
            {
                if ( _state.equals ( State.LOOKUP ) ) 
                    setState ( State.CLOSED, e );
            }
        } 
    }
}
