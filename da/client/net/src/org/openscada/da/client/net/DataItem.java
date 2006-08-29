/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006 inavare GmbH (http://inavare.com)
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

package org.openscada.da.client.net;

import java.util.HashMap;
import java.util.Map;
import java.util.Observable;

import org.openscada.ae.core.Variant;
import org.openscada.ae.core.utils.AttributesHelper;

public class DataItem extends Observable
{
    private String _itemName;
    private Connection _connection = null;
    
    private Variant _value = new Variant();
    private Map<String,Variant> _attributes = new HashMap<String,Variant>();
    
    private ItemUpdateListener _listener = new ItemUpdateListener(){

        public void notifyValueChange ( Variant value, boolean initial )
        {
            performNotifyValueChange ( value, initial );
        }

        public void notifyAttributeChange ( Map<String, Variant> attributes, boolean initial )
        {
            performNotifyAttributeChange ( attributes, initial );
        }};
    
    public DataItem ( String itemName )
    {
        _itemName = new String(itemName);
    }
    
    public DataItem ( String itemName, Connection connection )
    {
        this(itemName);
        
        register ( connection );
    }
    
    synchronized public void register ( Connection connection )
    {
        if ( _connection == connection )
            return;
        
        _connection = connection;
        _connection.addItemUpdateListener ( _itemName, true, _listener );
    }
    
    synchronized public void unregister ()
    {
        if ( _connection == null )
            return;
        
        _connection.removeItemUpdateListener ( _itemName, _listener );
    }
    
    private void performNotifyValueChange ( Variant value, boolean initial )
    {
        _value = value;
        setChanged();
        notifyObservers();
    }

    private void performNotifyAttributeChange ( Map<String, Variant> attributes, boolean initial )
    {
        if ( initial )
            _attributes = new HashMap<String,Variant> ( attributes );
        else
            AttributesHelper.mergeAttributes ( _attributes, attributes );
        
        setChanged();
        notifyObservers();
    }
    
    /**
     * Fetch the current cached value.
     * 
     * <b>Note:</b> The returned object may not be modified!
     *  
     * @return the current value
     */
    public Variant getValue ()
    {
        return _value;
    }
    
    /**
     * Fetch the current cached attributes.
     * 
     * <b>Note:</b> The returned object may not be modified!
     *  
     * @return the current attributes
     */
    public Map<String,Variant> getAttributes ()
    {
        return _attributes;
    }
    
}
