/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2010 TH4 SYSTEMS GmbH (http://th4-systems.com)
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

package org.openscada.hd.perf.test;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class PerformanceData
{
    static class Path
    {
        public Object from;

        public Object to;

        @Override
        public int hashCode ()
        {
            final int prime = 31;
            int result = 1;
            result = prime * result + ( this.from == null ? 0 : this.from.hashCode () );
            result = prime * result + ( this.to == null ? 0 : this.to.hashCode () );
            return result;
        }

        @Override
        public boolean equals ( final Object obj )
        {
            if ( this == obj )
            {
                return true;
            }
            if ( obj == null )
            {
                return false;
            }
            if ( getClass () != obj.getClass () )
            {
                return false;
            }
            final Path other = (Path)obj;
            if ( this.from == null )
            {
                if ( other.from != null )
                {
                    return false;
                }
            }
            else if ( !this.from.equals ( other.from ) )
            {
                return false;
            }
            if ( this.to == null )
            {
                if ( other.to != null )
                {
                    return false;
                }
            }
            else if ( !this.to.equals ( other.to ) )
            {
                return false;
            }
            return true;
        }

    }

    static class Entry
    {
        public List<Long> times = new LinkedList<Long> ();
    }

    private final Map<Path, Entry> entries = new HashMap<Path, Entry> ();

    private Object lastNode;

    private Long lastTimestamp;

    public void marker ( final Object node )
    {
        final Path path = new Path ();

        path.from = this.lastNode;
        path.to = node;

        Entry entry = this.entries.get ( path );
        if ( entry == null )
        {
            entry = new Entry ();
            this.entries.put ( path, entry );
        }

        final long now = System.currentTimeMillis ();
        if ( this.lastTimestamp != null )
        {
            entry.times.add ( now - this.lastTimestamp );
        }
        else
        {
            entry.times.add ( 0L );
        }

        this.lastTimestamp = now;
        this.lastNode = node;
    }

    public Map<Path, Entry> getEntries ()
    {
        return this.entries;
    }

}
