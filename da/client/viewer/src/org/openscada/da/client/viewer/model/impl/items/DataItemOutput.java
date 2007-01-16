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

package org.openscada.da.client.viewer.model.impl.items;

import java.util.EnumSet;
import java.util.Observable;
import java.util.Observer;

import org.apache.log4j.Logger;
import org.openscada.da.client.DataItem;
import org.openscada.da.client.ItemManager;
import org.openscada.da.client.net.Connection;
import org.openscada.da.client.viewer.model.OutputDefinition;
import org.openscada.da.client.viewer.model.OutputListener;
import org.openscada.da.client.viewer.model.Type;
import org.openscada.da.client.viewer.model.impl.BaseOutput;

public class DataItemOutput extends BaseOutput implements OutputDefinition, Observer
{
    private static Logger _log = Logger.getLogger ( DataItemOutput.class );
    
    private ItemManager _itemManager = null;
    private DataItem _dataItem = null;
    private boolean _subscribed = false;
    
    public DataItemOutput ( ItemManager itemManager, String item, String name )
    {
        super ( name );
        _itemManager = itemManager;
        _dataItem = new DataItem ( item );
    }
    
    protected synchronized void subscribe ()
    {
        if ( _subscribed )
            return;
        
        _log.debug ( String.format ( "Subscribing to item" ) );
        
        _dataItem.addObserver ( this );
        _dataItem.register ( _itemManager );
        _subscribed = true;
    }
    
    protected synchronized void unsubscribe ()
    {
        if ( !_subscribed )
            return;
        
        _log.debug ( String.format ( "Un-Subscribing from item" ) );
        
        _dataItem.deleteObserver ( this );
        _dataItem.unregister ();
        _subscribed = false;
    }
    
    public EnumSet<Type> getSupportedTypes ()
    {
        return EnumSet.of ( Type.VARIANT );
    }

    public void update ( Observable o, Object arg )
    {
        fireEvent ( Type.VARIANT, _dataItem.getValue () );
    }

    @Override
    public synchronized void addListener ( OutputListener listener )
    {
        super.addListener ( listener );
        if ( hasListeners () )
        {
            subscribe ();
        }
    }
    
    @Override
    public synchronized void removeListener ( OutputListener listener )
    {
        _log.debug ( "Removing listener" );
        
        super.removeListener ( listener );
        if ( !hasListeners () )
        {
            unsubscribe ();
        }
    }
}
