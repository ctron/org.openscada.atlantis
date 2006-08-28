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

import java.util.Collection;
import java.util.HashSet;
import java.util.Observable;
import java.util.Set;

import org.openscada.da.core.server.DataItemInformation;

public class ItemList extends Observable implements ItemListListener
{
    private Set<DataItemInformation> _items = new HashSet<DataItemInformation>();
    
    public ItemList ()
    {
    }
    
    public void changed ( Collection<DataItemInformation> added, Collection<String> removed, boolean initial )
    {
        int changes = 0;
        
        synchronized ( _items )
        {
            if ( initial )
            {
                _items.clear ();
                _items = new HashSet<DataItemInformation> ( added );
                changes = _items.size ();
            }
            else
            {
                
                for ( DataItemInformation item : added )
                {
                    if ( _items.add(item) )
                        changes++;
                }
                for ( String item : removed )
                {
                    if ( _items.remove(item) )
                        changes++;
                }
            }
        }
        
        // perform notifaction
        if ( changes > 0 )
        {
            setChanged();
            notifyObservers();
        }
        
    }
    
    public Collection<DataItemInformation> getItemList()
    {
        synchronized ( _items )
        {
            return new HashSet<DataItemInformation> ( _items );
        }
    }
}
