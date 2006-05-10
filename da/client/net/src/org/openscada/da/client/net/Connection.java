package org.openscada.da.client.net;

import java.io.IOException;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.openscada.da.core.data.Variant;
import org.openscada.net.base.ClientConnection;
import org.openscada.net.base.MessageListener;
import org.openscada.net.base.MessageStateListener;
import org.openscada.net.base.data.DoubleValue;
import org.openscada.net.base.data.LongValue;
import org.openscada.net.base.data.Message;
import org.openscada.net.base.data.StringValue;
import org.openscada.net.base.data.Value;
import org.openscada.net.da.handler.Messages;
import org.openscada.net.io.IOProcessor;
import org.openscada.utils.timing.Scheduler;

public class Connection
{
    private ConnectionInfo _connectionInfo = null;
    private IOProcessor _processor = null;
    
    private ClientConnection _client = null;
    
    private List<ConnectionStateListener> _connectionStateListeners = new ArrayList<ConnectionStateListener>();
    private Map<String,List<ItemUpdateListener>> _itemListeners = new HashMap<String,List<ItemUpdateListener>>();
    
    private boolean _connected = false;
    
    private static Object _defaultProcessorLock = new Object();
    private static IOProcessor _defaultProcessor = null;
    
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
            e.printStackTrace();
        }
        // operation failed;
        return null;
    }
    
    public Connection ( IOProcessor processor, ConnectionInfo connectionInfo )
    {
        super();
        
        _processor = processor;
        _connectionInfo = connectionInfo;
        
    }
    
    public Connection ( ConnectionInfo connectionInfo )
    {
        this ( getDefaultProcessor(), connectionInfo );
    }
    
    public void start ()
    {
        if ( _client != null )
            return;
        
        _client = new ClientConnection (_processor, _connectionInfo.getRemote() );
        _client.addStateListener(new  org.openscada.net.io.ConnectionStateListener(){
            
            public void closed ()
            {
                if ( isConnected() )
                    fireDisconnected();
            }
            
            public void opened ()
            {
                if ( !isConnected() )
                    fireConnected();
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
        _connected = true;
        
        List<ConnectionStateListener> connectionStateListeners;
        synchronized ( _connectionStateListeners )
        {
            connectionStateListeners = new ArrayList<ConnectionStateListener>(_connectionStateListeners);
        }
        for ( ConnectionStateListener listener : connectionStateListeners )
        {
            try
            {
                listener.connected(this);
            }
            catch ( Exception e )
            {
            }
        }
        
        requestSession ();
    }
    
    private void fireDisconnected ()
    {
        _connected = false;
        
        List<ConnectionStateListener> connectionStateListeners;
        synchronized ( _connectionStateListeners )
        {
            connectionStateListeners = new ArrayList<ConnectionStateListener>(_connectionStateListeners);
        }
        for ( ConnectionStateListener listener : connectionStateListeners )
        {
            try
            {
                listener.disconnected(this);
            }
            catch ( Exception e )
            {
            }
        }
        
    }
    
    private void requestSession ()
    {
        if ( _client == null )
            return;
        
        _client.getConnection().sendMessage ( Messages.createSession(new Properties()), new MessageStateListener(){

            public void messageComplete ( Message message )
            {
                gotSession ();
            }

            public void messageTimedOut ()
            {
                // TODO: so something
            }} );
    }
    
    private void gotSession ()
    {
        // sync again all items to maintain subscribtions
        syncAllItems();
    }
    
    public void addItemUpdateListener ( String itemName, ItemUpdateListener listener ) 
    {
        synchronized ( _itemListeners )
        {
            if (!_itemListeners.containsKey(itemName))
            {
                _itemListeners.put( itemName, new ArrayList<ItemUpdateListener> () );
            }
            
            int before = _itemListeners.get ( itemName ).size();
            _itemListeners.get ( itemName ).add ( listener );
            if ( before != _itemListeners.get ( itemName ).size () )
                syncRegItem ( itemName, _itemListeners.get ( itemName ).size () );
        }
    }
    
    public void removeItemUpdateListener ( String itemName, ItemUpdateListener listener ) 
    {
        synchronized ( _itemListeners )
        {
            if (!_itemListeners.containsKey(itemName))
            {
                _itemListeners.put( itemName, new ArrayList<ItemUpdateListener> () );
            }
            
            int before = _itemListeners.get ( itemName ).size();
            _itemListeners.get ( itemName).remove ( listener );
            if ( before != _itemListeners.get ( itemName ).size () )
                syncRegItem ( itemName, _itemListeners.get ( itemName ).size () );
        }
    }
    
    /**
     * Sync register one item. Check if subscription is needed or not
     * @param itemName name of the item
     * @param subscribers number of current subscribers
     */
    private void syncRegItem ( String itemName, int subscribers )
    {
        // don't sync if we don't have a connection
        if ( _client == null )
            return;
        
        Message message;
        if ( subscribers > 0 )
        {
            message = Messages.subscribeItem ( itemName );
        }
        else
        {
            message = Messages.unsubscribeItem ( itemName );
        }
        
        _client.getConnection().sendMessage ( message );
    }
    
    /**
     * Synchronized all items that are currently known
     *
     */
    private void syncAllItems ()
    {
        synchronized ( _itemListeners )
        {
            for ( Map.Entry<String,List<ItemUpdateListener>> entry : _itemListeners.entrySet() )
            {
                syncRegItem ( entry.getKey(), entry.getValue().size() );
            }
        }
    }
    
   
    
    private void fireValueChange ( String itemName, Variant value )
    {
        synchronized ( _itemListeners )
        {
            if ( _itemListeners.containsKey(itemName) )
            {
                for ( Iterator<ItemUpdateListener> i = _itemListeners.get(itemName).iterator(); i.hasNext() ; )
                {
                    i.next().notifyValueChange ( value );
                }
            }
        }
    }
    
    private void fireAttributesChange ( String itemName, Map<String,Variant> attributes )
    {
        synchronized ( _itemListeners )
        {
            if ( _itemListeners.containsKey(itemName) )
            {
                for ( Iterator<ItemUpdateListener> i = _itemListeners.get(itemName).iterator(); i.hasNext() ; )
                {
                    i.next().notifyAttributeChange ( attributes );
                }
            }
        }
    }
    
    private void notifyValueChange ( Message message )
    {
        Variant value = new Variant ();
        
        if ( message.getValues().containsKey("value") )
        {
            value = valueToVariant ( message.getValues().get("value") );
        }
        
        String itemName = message.getValues().get("item-name").toString();
        fireValueChange(itemName, value);
    }
    
    private Variant valueToVariant ( Value fromValue )
    {
        if ( fromValue instanceof StringValue )
            return new Variant ( ((StringValue)fromValue).getValue() );
        else if ( fromValue instanceof DoubleValue )
            return new Variant ( ((DoubleValue)fromValue).getValue() );
        else if ( fromValue instanceof LongValue )
            return new Variant ( ((LongValue)fromValue).getValue() );
        return null;
    }
    
    private void notifyAttributesChange ( Message message )
    {
        
        Map<String,Variant> attributes = new HashMap<String,Variant>();
        
        for ( Map.Entry<String,Value> entry : message.getValues().entrySet() )
        {
            String name = entry.getKey();
            if ( name.startsWith("set-") )
            {
                Variant value = valueToVariant ( entry.getValue() );
                name = name.substring("set-".length());
                attributes.put(name,value);
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
        fireAttributesChange(itemName, attributes);
    }

    public boolean isConnected ()
    {
        return _connected;
    }
}
