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

package org.openscada.da.server.common.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.openscada.da.core.Location;
import org.openscada.da.server.common.DataItem;

public class SessionCommonData
{
    private final Set<DataItem> _items = new HashSet<DataItem> ();

    private final Map<Object, Location> _paths = new HashMap<Object, Location> ();

    private final Map<Location, Object> _pathRev = new HashMap<Location, Object> ();

    public void addItem ( final DataItem item )
    {
        synchronized ( this._items )
        {
            this._items.add ( item );
        }
    }

    public void removeItem ( final DataItem item )
    {
        synchronized ( this._items )
        {
            this._items.remove ( item );
        }
    }

    public boolean containsItem ( final DataItem item )
    {
        synchronized ( this._items )
        {
            return this._items.contains ( item );
        }
    }

    public Set<DataItem> getItems ()
    {
        synchronized ( this._items )
        {
            return this._items;
        }
    }

    // paths
    public void addPath ( final Object tag, final Location path )
    {
        synchronized ( this._paths )
        {
            this._paths.put ( tag, path );
            this._pathRev.put ( path, tag );
        }
    }

    public void removePath ( final Location path )
    {
        synchronized ( this._paths )
        {
            final Object tag = this._pathRev.get ( path );
            if ( tag != null )
            {
                this._pathRev.remove ( path );
                this._paths.remove ( tag );
            }
        }
    }

    public Object getTag ( final Location path )
    {
        synchronized ( this._paths )
        {
            return this._pathRev.get ( path );
        }
    }

    public boolean containsPath ( final Object tag )
    {
        synchronized ( this._paths )
        {
            return this._paths.containsKey ( tag );
        }
    }

    public Map<Object, Location> getPaths ()
    {
        synchronized ( this._paths )
        {
            return this._paths;
        }
    }

    public void clearPaths ()
    {
        synchronized ( this._paths )
        {
            this._paths.clear ();
            this._pathRev.clear ();
        }
    }
}
