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

package org.eclipse.scada.da.server.browser.common.query;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import org.eclipse.scada.core.Variant;
import org.eclipse.scada.da.core.browser.Entry;
import org.eclipse.scada.da.core.server.browser.NoSuchFolderException;
import org.eclipse.scada.da.server.browser.common.FolderCommon;
import org.eclipse.scada.da.server.browser.common.FolderListener;
import org.eclipse.scada.da.server.common.DataItem;

public class QueryFolder implements StorageBasedFolder
{
    private Matcher _matcher = null;

    private NameProvider _nameProvider = null;

    private final FolderCommon _folder = new FolderCommon ();

    private final List<StorageBasedFolder> _folders = new ArrayList<StorageBasedFolder> ();

    private final Set<ItemDescriptor> _items = new HashSet<ItemDescriptor> ();

    public QueryFolder ( final Matcher matcher, final NameProvider nameProvider )
    {
        this._matcher = matcher;
        this._nameProvider = nameProvider;
    }

    public void addChild ( final String name, final StorageBasedFolder folder, final Map<String, Variant> attributes )
    {
        synchronized ( this )
        {
            this._folder.add ( name, folder, attributes );
            this._folders.add ( folder );

            // now push all possible descriptors
            for ( final ItemDescriptor desc : this._items )
            {
                folder.added ( desc );
            }
        }
    }

    public void removeChild ( final QueryFolder folder )
    {
        synchronized ( this )
        {
            this._folder.remove ( folder );
            this._folders.remove ( folder );
        }
    }

    private boolean match ( final ItemDescriptor desc )
    {
        if ( this._matcher != null )
        {
            return this._matcher.matches ( desc );
        }

        return false;
    }

    public void added ( final ItemDescriptor desc )
    {
        synchronized ( this )
        {
            if ( this._items.contains ( desc ) )
            {
                return;
            }

            if ( match ( desc ) )
            {
                this._items.add ( desc );
                notifyAdd ( desc );
            }
        }
    }

    public void removed ( final ItemDescriptor desc )
    {
        synchronized ( this )
        {
            if ( !this._items.contains ( desc ) )
            {
                return;
            }

            this._items.remove ( desc );
            notifyRemove ( desc );
        }
    }

    public void removeAllForItem ( final DataItem dataItem )
    {
        synchronized ( this )
        {
            final List<ItemDescriptor> removeList = new LinkedList<ItemDescriptor> ();
            for ( final ItemDescriptor desc : this._items )
            {
                if ( desc.getItem () == dataItem )
                {
                    removeList.add ( desc );
                }
            }
            for ( final ItemDescriptor desc : removeList )
            {
                removed ( desc );
            }
        }
    }

    private void notifyAdd ( final ItemDescriptor desc )
    {
        final String name = this._nameProvider.getName ( desc );
        if ( name != null )
        {
            this._folder.add ( name, desc.getItem (), desc.getAttributes () );
        }

        // notify childs
        for ( final StorageBasedFolder folder : this._folders )
        {
            folder.added ( desc );
        }
    }

    private void notifyRemove ( final ItemDescriptor desc )
    {
        final String name = this._nameProvider.getName ( desc );
        if ( name != null )
        {
            this._folder.remove ( desc.getItem () );
        }

        // notify childs
        for ( final StorageBasedFolder folder : this._folders )
        {
            folder.removed ( desc );
        }
    }

    public Entry[] list ( final Stack<String> path ) throws NoSuchFolderException
    {
        return this._folder.list ( path );
    }

    public void subscribe ( final Stack<String> path, final FolderListener listener, final Object tag ) throws NoSuchFolderException
    {
        this._folder.subscribe ( path, listener, tag );
    }

    public void unsubscribe ( final Stack<String> path, final Object tag ) throws NoSuchFolderException
    {
        this._folder.unsubscribe ( path, tag );
    }

    public void added ()
    {
        this._folder.added ();
    }

    public void removed ()
    {
        this._folder.removed ();
    }
}
