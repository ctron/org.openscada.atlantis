/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2010 TH4 SYSTEMS GmbH (http://inavare.com)
 *
 * OpenSCADA is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 3
 * only, as published by the Free Software Foundation.
 *
 * OpenSCADA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License version 3 for more details
 * (a copy is included in the LICENSE file that accompanied this code).
 *
 * You should have received a copy of the GNU Lesser General Public License
 * version 3 along with OpenSCADA. If not, see
 * <http://opensource.org/licenses/lgpl-3.0.html> for a copy of the LGPLv3 License.
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
