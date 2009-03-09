/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006 inavare GmbH (http://inavare.com)
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package org.openscada.da.client.base.browser;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;

import org.apache.log4j.Logger;
import org.openscada.da.core.browser.Entry;

public abstract class FolderUpdater extends Observable
{
    private static Logger _log = Logger.getLogger ( FolderUpdater.class );

    private HiveConnection _connection = null;

    private FolderEntry _folder = null;

    private boolean _autoInitialize = true;

    protected Map<String, BrowserEntry> _entries = null;

    public FolderUpdater ( HiveConnection connection, FolderEntry folder, boolean autoInitialize )
    {
        _folder = folder;
        _connection = connection;
        _autoInitialize = autoInitialize;
    }

    synchronized public BrowserEntry[] list ()
    {
        if ( _entries == null )
        {
            if ( _autoInitialize )
                init ();

            return new BrowserEntry[0];
        }

        return _entries.values ().toArray ( new BrowserEntry[_entries.size ()] );
    }

    protected Map<String, BrowserEntry> convert ( Collection<Entry> entries )
    {
        Map<String, BrowserEntry> list = new HashMap<String, BrowserEntry> ();
        int i = 0;
        for ( Entry entry : entries )
        {
            if ( entry instanceof org.openscada.da.core.browser.FolderEntry )
            {
                FolderEntry folder = new FolderEntry ( entry.getName (), entry.getAttributes (), _folder, getConnection (), true );
                list.put ( entry.getName (), folder );
            }
            else if ( entry instanceof org.openscada.da.core.browser.DataItemEntry )
            {
                org.openscada.da.core.browser.DataItemEntry itemEntry = (org.openscada.da.core.browser.DataItemEntry)entry;
                list.put ( entry.getName (), new DataItemEntry ( entry.getName (), entry.getAttributes (), _folder, getConnection (), itemEntry.getId (), itemEntry.getIODirections () ) );
            }
            else
                _log.warn ( "Unknown entry type in tree: " + entry.getClass ().getName () ); //$NON-NLS-1$
            i++;
        }
        return list;
    }

    public HiveConnection getConnection ()
    {
        return _connection;
    }

    synchronized protected void update ( Map<String, BrowserEntry> newEntries )
    {
        // if we don't have content it is quite easy
        if ( _entries == null )
        {
            _entries = newEntries;
            notifyChange ();
            return;
        }

        // unsubscribe old
        for ( Map.Entry<String, BrowserEntry> entry : _entries.entrySet () )
        {
            if ( entry.getValue () instanceof FolderEntry )
            {
                ( (FolderEntry)entry.getValue () ).dispose ();
            }
        }

        // new entries are already subscribed
        _entries = newEntries;

        notifyChange ();
    }

    private void notifyChange ()
    {
        setChanged ();
        notifyObservers ();
    }

    synchronized public void clear ()
    {
        if ( _entries != null )
        {
            for ( Map.Entry<String, BrowserEntry> entry : _entries.entrySet () )
            {
                if ( entry.getValue () instanceof FolderEntry )
                {
                    FolderEntry folderEntry = (FolderEntry)entry.getValue ();
                    folderEntry.dispose ();
                }
            }
            _entries.clear ();
            notifyChange ();
        }

        _entries = null;
    }

    public FolderEntry getFolder ()
    {
        return _folder;
    }

    public abstract void dispose ();

    public abstract void init ();

    public boolean isAutoInitialize ()
    {
        return _autoInitialize;
    }
}
