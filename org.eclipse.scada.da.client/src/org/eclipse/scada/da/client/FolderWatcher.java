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

package org.eclipse.scada.da.client;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;

import org.eclipse.scada.da.core.Location;
import org.eclipse.scada.da.core.browser.Entry;

public class FolderWatcher extends Observable implements FolderListener
{
    protected Location location = null;

    protected Map<String, Entry> cache = new HashMap<String, Entry> ();

    public FolderWatcher ( final String... path )
    {
        this.location = new Location ( path );
    }

    public FolderWatcher ( final Location location )
    {
        this.location = location;
    }

    public void folderChanged ( final Collection<Entry> added, final Collection<String> removed, final boolean full )
    {
        int changed = 0;

        synchronized ( this )
        {

            if ( full )
            {
                this.cache.clear ();
            }

            for ( final Entry entry : added )
            {
                this.cache.put ( entry.getName (), entry );
                changed++;
            }

            for ( final String name : removed )
            {
                if ( this.cache.remove ( name ) != null )
                {
                    changed++;
                }
            }

            if ( changed > 0 || full )
            {
                setChanged ();

            }
        }

        notifyObservers ();
    }

    public Location getLocation ()
    {
        return this.location;
    }

    public Map<String, Entry> getCache ()
    {
        return this.cache;
    }

    public Collection<Entry> getList ()
    {
        return this.cache.values ();
    }
}
