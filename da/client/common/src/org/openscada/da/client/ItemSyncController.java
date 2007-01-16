/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2007 inavare GmbH (http://inavare.com)
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package org.openscada.da.client;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.openscada.core.Variant;
import org.openscada.core.utils.AttributesHelper;

public class ItemSyncController
{
    
    private static Logger _log = Logger.getLogger ( ItemSyncController.class );
    
    private org.openscada.da.client.Connection _connection;
    private String _itemName;
    
    private boolean _subscribedInitial = false;
    private boolean _subscribed = false;
    
    private Variant _cachedValue = new Variant();
    private Map<String,Variant> _cachedAttributes = new HashMap<String,Variant> ();
    
    /**
     * Holds some additional listener information 
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
    
    public ItemSyncController ( org.openscada.da.client.Connection connection, String itemName )
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
            if ( !_listeners.containsKey ( listener ) )
            {
                _listeners.put ( listener, new ListenerInfo ( listener, initial ) );
                if ( initial )
                {
                    _initialListeners++;
                    listener.notifyValueChange ( _cachedValue, true );
                    listener.notifyAttributeChange ( _cachedAttributes, true );
                }
                
                sync ();
            }
        }
        
    }
    
    public void remove ( ItemUpdateListener listener )
    {
        synchronized ( _listeners )
        {
            if ( _listeners.containsKey ( listener ) )
            {
                ListenerInfo info = _listeners.get ( listener );
                if ( info.isInitial () )
                    _initialListeners--;
                
                _listeners.remove ( listener );
                
                sync ();
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
            boolean initial = getNumerOfListenersInitial() > 0;
            boolean subscribe = getNumberOfListeners() > 0; 
            
            if ( ( _subscribedInitial == initial ) && ( _subscribed == subscribe ) && !force )
                return; // nothing to do
            
            _subscribed = subscribe;
            _subscribedInitial = initial;
            
            if ( subscribe )
            {
                _log.debug ( "Syncing listen state: active " + initial );
                _connection.subscribeItem ( _itemName, initial );
            }
            else
            {
                _log.debug ( "Syncing listen state: inactive " );
                _connection.unsubscribeItem ( _itemName );
            }
        }
    }
    
    public void fireValueChange ( Variant value, boolean initial )
    {
        synchronized ( _listeners )
        {
            _cachedValue = new Variant(value);
            
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
            AttributesHelper.mergeAttributes ( _cachedAttributes, attributes, initial );
            
            for ( ListenerInfo listenerInfo : _listeners.values() )
            {                
                if ( !initial || listenerInfo.isInitial() )
                    listenerInfo.getListener().notifyAttributeChange ( attributes, initial );
            }
        }
    }
}
