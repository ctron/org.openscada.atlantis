package org.openscada.da.client.net;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.openscada.da.core.data.Variant;
import org.openscada.net.base.ClientConnection;
import org.openscada.net.base.data.Message;
import org.openscada.net.da.handler.Messages;

public class ItemSyncController
{
    
    private static Logger _log = Logger.getLogger ( ItemSyncController.class );
    
    private Connection _connection;
    private String _itemName;
    
    private boolean _subscribedInitial = false;
    private boolean _subscribed = false;
    
    /**
     * Holds some additional listner information 
     * @author jens
     *
     */
    private class ListenerInfo
    {
        private ItemUpdateListener _listener;
        private boolean _initial;
        
        public ListenerInfo ( ItemUpdateListener listener, boolean initial )
        {
            _listener = listener;
            _initial = initial;
        }

        public boolean isInitial ()
        {
            return _initial;
        }

        public ItemUpdateListener getListener ()
        {
            return _listener;
        }
        
        @Override
        public boolean equals ( Object obj )
        {
           if ( obj == null )
               return false;
           if ( obj == this )
               return true;
           
           if ( obj instanceof ItemUpdateListener )
           {
               return obj == _listener;
           }
           else if ( obj instanceof ListenerInfo )
           {
               return ((ListenerInfo)obj)._listener == _listener;
           }
           else
           {
               return false;
           }
        }
        
        @Override
        public int hashCode ()
        {
            return _listener.hashCode();
        }
    }

    private Map<ItemUpdateListener,ListenerInfo> _listeners = new HashMap<ItemUpdateListener,ListenerInfo>();
    private long _initialListeners = 0;
    
    public ItemSyncController ( Connection connection, String itemName )
    {
        _connection = connection;
        _itemName = itemName;
    }

    public String getItemName ()
    {
        return _itemName;
    }
    
    public int getNumberOfListeners ()
    {
        synchronized ( _listeners )
        {
            return _listeners.size();
        }
    }
    
    public long getNumerOfListenersInitial ()
    {
        synchronized ( _listeners )
        {
            return _initialListeners;
        }
    }
    
    public void add ( ItemUpdateListener listener, boolean initial )
    {
        synchronized ( _listeners )
        {
            if ( !_listeners.containsKey(listener) )
            {
                _listeners.put(listener, new ListenerInfo(listener, initial));
                if ( initial )
                    _initialListeners++;
                
                sync();
            }
        }
        
    }
    
    public void remove ( ItemUpdateListener listener )
    {
        synchronized ( _listeners )
        {
            if ( !_listeners.containsKey(listener) )
            {
                ListenerInfo info = _listeners.get(listener);
                if ( info.isInitial() )
                    _initialListeners--;
                
                _listeners.remove(listener);
                
                sync();
            }
        }
    }
    
    public void sync ( )
    {
        sync ( false );
    }
    
    public void sync ( boolean force )
    {
        synchronized ( _listeners )
        {
            Message message;
            
            boolean initial = getNumerOfListenersInitial() > 0;
            boolean subscribe = getNumberOfListeners() > 0; 
            
            if ( (_subscribedInitial == initial) && (_subscribed == subscribe) && !force )
                return; // nothing to do
            
            if ( subscribe )
            {
                _log.debug("Syncing listen state: active " + initial );
                message = Messages.subscribeItem ( _itemName, initial );
            }
            else
            {
                _log.debug("Syncing listen state: inactive " );
                message = Messages.unsubscribeItem ( _itemName );
            }
            
            ClientConnection client = _connection.getClient();
            if ( client != null )
                client.getConnection().sendMessage ( message );
            else
                _log.debug("No connection. Skipping sync message!");
        }
    }
    
    public void fireValueChange ( Variant value, boolean initial )
    {
        synchronized ( _listeners )
        {
            for ( ListenerInfo listenerInfo : _listeners.values() )
            {
                if ( !initial || listenerInfo.isInitial() )
                    listenerInfo.getListener().notifyValueChange ( value, initial );
            }
        }
    }

    public void fireAttributesChange ( Map<String, Variant> attributes, boolean initial )
    {
        synchronized ( _listeners )
        {
            for ( ListenerInfo listenerInfo : _listeners.values() )
            {                
                if ( !initial || listenerInfo.isInitial() )
                    listenerInfo.getListener().notifyAttributeChange ( attributes, initial );
            }
        }
    }
}
