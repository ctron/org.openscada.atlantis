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

package org.openscada.da.core.common.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.openscada.da.core.common.DataItem;
import org.openscada.da.core.server.browser.Location;

public class SessionCommonData
{
	private Set<DataItem> _items = new HashSet<DataItem> ();
    private Map<Object,Location> _paths = new HashMap<Object, Location> ();
    private Map<Location,Object> _pathRev = new HashMap<Location, Object> ();
	
	public void addItem ( DataItem item )
	{
		synchronized ( _items )
		{
			_items.add ( item );
		}
	}
	
	public void removeItem ( DataItem item )
	{
		synchronized ( _items )
		{
			_items.remove ( item );
		}
	}
	
	public boolean containsItem ( DataItem item )
	{
		synchronized ( _items )
		{
			return _items.contains ( item );
		}	
	}

	public Set<DataItem> getItems()
    {
        synchronized ( _items )
        {
            return _items;
        }
	}
    
    // paths
    public void addPath ( Object tag, Location path )
    {
        synchronized ( _paths )
        {
            _paths.put ( tag, path );
            _pathRev.put ( path, tag );
        }
    }
    
    public void removePath ( Location path )
    {
        synchronized ( _paths )
        {
            Object tag = _pathRev.get ( path );
            if ( tag != null )
            {
                _pathRev.remove ( path );
                _paths.remove ( tag );
            }
        }
    }
    
    public Object getTag ( Location path )
    {
        synchronized ( _paths )
        {
            return _pathRev.get ( path );
        }
    }
    
    public boolean containsPath ( Object tag )
    {
        synchronized ( _paths )
        {
            return _paths.containsKey ( tag );
        }   
    }

    public Map<Object, Location> getPaths ()
    {
        synchronized ( _paths )
        {
            return _paths;
        }
    }
    
    public void clearPaths ()
    {
        synchronized ( _paths )
        {
            _paths.clear ();
            _pathRev.clear ();
        }
    }
}
