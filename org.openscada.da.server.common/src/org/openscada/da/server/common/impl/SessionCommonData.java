/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2010 inavare GmbH (http://inavare.com)
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
    private final Set<DataItem> items = new HashSet<DataItem> ();

    private final Map<Object, Location> paths = new HashMap<Object, Location> ();

    private final Map<Location, Object> pathRev = new HashMap<Location, Object> ();

    public void addItem ( final DataItem item )
    {
        synchronized ( this.items )
        {
            this.items.add ( item );
        }
    }

    public void removeItem ( final DataItem item )
    {
        synchronized ( this.items )
        {
            this.items.remove ( item );
        }
    }

    public boolean containsItem ( final DataItem item )
    {
        synchronized ( this.items )
        {
            return this.items.contains ( item );
        }
    }

    public Set<DataItem> getItems ()
    {
        synchronized ( this.items )
        {
            return this.items;
        }
    }

    // paths
    public void addPath ( final Object tag, final Location path )
    {
        synchronized ( this.paths )
        {
            this.paths.put ( tag, path );
            this.pathRev.put ( path, tag );
        }
    }

    public void removePath ( final Location path )
    {
        synchronized ( this.paths )
        {
            final Object tag = this.pathRev.get ( path );
            if ( tag != null )
            {
                this.pathRev.remove ( path );
                this.paths.remove ( tag );
            }
        }
    }

    public Object getTag ( final Location path )
    {
        synchronized ( this.paths )
        {
            return this.pathRev.get ( path );
        }
    }

    public boolean containsPath ( final Object tag )
    {
        synchronized ( this.paths )
        {
            return this.paths.containsKey ( tag );
        }
    }

    public Map<Object, Location> getPaths ()
    {
        synchronized ( this.paths )
        {
            return this.paths;
        }
    }

    public void clearPaths ()
    {
        synchronized ( this.paths )
        {
            this.paths.clear ();
            this.pathRev.clear ();
        }
    }
}
