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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;

import org.openscada.da.core.Location;
import org.openscada.da.core.browser.Entry;

public class FolderWatcher extends Observable implements FolderListener
{
    protected Location _location = null;
    protected Map<String, Entry> _cache = new HashMap<String, Entry> (); 
    
    public FolderWatcher ( String... path )
    {
        _location = new Location ( path );
    }
    
    public FolderWatcher ( Location location )
    {
        _location = location;
    }
    
    synchronized public void folderChanged ( Collection<Entry> added, Collection<String> removed, boolean full )
    {
        int changed = 0;
        
        if ( full )
        {
            _cache.clear ();
        }
        
        for ( Entry entry : added )
        {
            _cache.put ( entry.getName (), entry );
            changed++;
        }
        
        for ( String name : removed )
        {
            if ( _cache.remove ( name ) != null )
            {
                changed++;
            }
        }
        
        if ( changed > 0 || full )
        {
            setChanged ();
            notifyObservers ();
        }
    }

    public Location getLocation ()
    {
        return _location;
    }

    public Map<String, Entry> getCache ()
    {
        return _cache;
    }

    public Collection<Entry> getList ()
    {
        return _cache.values ();
    }
}
