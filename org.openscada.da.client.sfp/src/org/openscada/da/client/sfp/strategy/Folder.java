/*
 * This file is part of the openSCADA project
 * 
 * Copyright (C) 2013 Jens Reimann (ctron@dentrassi.de)
 *
 * openSCADA is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 3
 * only, as published by the Free Software Foundation.
 *
 * openSCADA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License version 3 for more details
 * (a copy is included in the LICENSE file that accompanied this code).
 *
 * You should have received a copy of the GNU Lesser General Public License
 * version 3 along with openSCADA. If not, see
 * <http://opensource.org/licenses/lgpl-3.0.html> for a copy of the LGPLv3 License.
 */

package org.openscada.da.client.sfp.strategy;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.concurrent.Executor;

import org.openscada.core.Variant;
import org.openscada.da.client.FolderListener;
import org.openscada.da.core.Location;
import org.openscada.da.core.browser.DataItemEntry;
import org.openscada.da.data.IODirection;

public class Folder
{
    private final Folder parent;

    private FolderListener listener;

    private final Executor executor;

    private final Location location;

    private static class Entry
    {
        private final String name;

        public Entry ( final String name )
        {
            this.name = name;
        }

        public String getName ()
        {
            return this.name;
        }
    }

    private static class FolderEntry extends Entry implements org.openscada.da.core.browser.FolderEntry
    {
        private final Folder folder;

        public FolderEntry ( final String name, final Folder folder )
        {
            super ( name );
            this.folder = folder;
        }

        public Folder getFolder ()
        {
            return this.folder;
        }

        @Override
        public Map<String, Variant> getAttributes ()
        {
            return Collections.emptyMap ();
        }
    }

    private static class ItemEntry extends Entry implements DataItemEntry
    {
        private final String itemId;

        private final HashMap<String, Variant> attributes;

        public ItemEntry ( final String name, final String itemId, final String description )
        {
            super ( name );
            this.itemId = itemId;
            this.attributes = new HashMap<String, Variant> ( 1 );
            if ( description != null )
            {
                this.attributes.put ( "description", Variant.valueOf ( description ) );
            }
        }

        @Override
        public Map<String, Variant> getAttributes ()
        {
            return this.attributes;
        }

        @Override
        public String getId ()
        {
            return this.itemId;
        }

        @Override
        public Set<IODirection> getIODirections ()
        {
            return EnumSet.allOf ( IODirection.class );
        }
    }

    private final Map<String, Entry> entries = new HashMap<> ();

    public Folder ( final Executor executor, final Folder parent, final Location location )
    {
        this.executor = executor;
        this.parent = parent;
        this.location = location;
    }

    public void setListener ( final FolderListener listener )
    {
        this.listener = listener;
    }

    public void dispose ()
    {
        for ( final Map.Entry<String, Entry> entry : this.entries.entrySet () )
        {
            if ( entry.getValue () instanceof FolderEntry )
            {
                ( (FolderEntry)entry.getValue () ).getFolder ().dispose ();
            }
        }
        fireListener ( null, null, true );
        this.entries.clear ();
    }

    protected void fireListener ( final Collection<org.openscada.da.core.browser.Entry> added, final Collection<String> removed, final boolean full )
    {
        final FolderListener listener = this.listener;

        if ( listener == null )
        {
            return;
        }

        this.executor.execute ( new Runnable () {

            @Override
            public void run ()
            {
                listener.folderChanged ( added == null ? Collections.<org.openscada.da.core.browser.Entry> emptyList () : added, removed == null ? Collections.<String> emptyList () : removed, full );
            }
        } );
    }

    public Folder findFolder ( final Stack<String> path, final boolean create )
    {
        if ( path.isEmpty () )
        {
            return this;
        }
        else
        {
            final String name = path.pop ();
            final Entry entry = this.entries.get ( name );
            if ( entry instanceof FolderEntry )
            {
                return ( (FolderEntry)entry ).getFolder ().findFolder ( path, create );
            }
            else if ( entry == null && create )
            {
                final Folder folder = new Folder ( this.executor, this, new Location ( this.location, name ) );
                final FolderEntry folderEntry = new FolderEntry ( name, folder );
                this.entries.put ( name, folderEntry );
                fireListener ( Arrays.<org.openscada.da.core.browser.Entry> asList ( folderEntry ), null, false );
                return folder.findFolder ( path, create );
            }
            else
            {
                return null;
            }
        }
    }

    public void addItemEntry ( final String name, final String itemId, final String description )
    {
        final ItemEntry entry = new ItemEntry ( name, itemId, description );
        this.entries.put ( name, entry );
        fireListener ( Arrays.<org.openscada.da.core.browser.Entry> asList ( entry ), null, false );
    }

    public void removeItemEntry ( final String name )
    {
        if ( this.entries.remove ( name ) != null )
        {
            fireListener ( null, Arrays.asList ( name ), false );
            propagateEmpty ();
        }
    }

    private void propagateEmpty ()
    {
        if ( this.parent != null && this.entries.isEmpty () )
        {
            this.parent.removeFolderEntry ( this );
        }
    }

    protected void removeFolderEntry ( final Folder folder )
    {
        boolean removed = false;
        final Iterator<java.util.Map.Entry<String, Entry>> i = this.entries.entrySet ().iterator ();
        while ( i.hasNext () )
        {
            final java.util.Map.Entry<String, Entry> entry = i.next ();
            if ( ! ( entry.getValue () instanceof FolderEntry ) )
            {
                continue;
            }
            final Folder childFolder = ( (FolderEntry)entry.getValue () ).getFolder ();
            if ( childFolder == folder )
            {
                i.remove ();
                fireListener ( null, Arrays.asList ( entry.getKey () ), false );
                removed = true;
            }
        }

        if ( removed )
        {
            propagateEmpty ();
        }
    }
}
