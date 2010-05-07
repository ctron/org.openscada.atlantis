/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2010 inavare GmbH (http://inavare.com)
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

package org.openscada.da.server.browser.common.query;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import org.apache.log4j.Logger;
import org.openscada.core.Variant;
import org.openscada.da.core.browser.Entry;
import org.openscada.da.core.server.browser.NoSuchFolderException;
import org.openscada.da.server.browser.common.Folder;
import org.openscada.da.server.browser.common.FolderCommon;
import org.openscada.da.server.browser.common.FolderListener;
import org.openscada.da.server.common.DataItem;
import org.openscada.utils.collection.MapBuilder;

public class GroupSubFolder implements Folder
{
    @SuppressWarnings ( "unused" )
    private static Logger _log = Logger.getLogger ( GroupSubFolder.class );

    private GroupSubFolder _parent = null;

    private NameProvider _nameProvider = null;

    private final FolderCommon _folder = new FolderCommon ();

    private final Map<String, GroupSubFolder> _subFolders = new HashMap<String, GroupSubFolder> ();

    public GroupSubFolder ( final NameProvider nameProvider )
    {
        this ( null, nameProvider );
    }

    private GroupSubFolder ( final GroupSubFolder parent, final NameProvider nameProvider )
    {
        this._parent = parent;
        this._nameProvider = nameProvider;
    }

    public GroupSubFolder add ( final Stack<String> path, final ItemDescriptor descriptor )
    {
        if ( path.isEmpty () )
        {
            return insertItem ( descriptor ) ? this : null;
        }
        else
        {
            final String next = path.pop ();
            final GroupSubFolder subFolder = getSubFolder ( next );
            return subFolder.add ( path, descriptor );
        }
    }

    private boolean insertItem ( final ItemDescriptor descriptor )
    {
        final String name = this._nameProvider.getName ( descriptor );

        if ( name == null )
        {
            return false;
        }

        this._folder.add ( name, descriptor.getItem (), descriptor.getAttributes () );

        return true;
    }

    private GroupSubFolder getSubFolder ( final String name )
    {
        if ( !this._subFolders.containsKey ( name ) )
        {
            final GroupSubFolder folder = new GroupSubFolder ( this, this._nameProvider );
            this._subFolders.put ( name, folder );
            final MapBuilder<String, Variant> builder = new MapBuilder<String, Variant> ();
            this._folder.add ( name, folder, builder.getMap () );
        }
        return this._subFolders.get ( name );
    }

    public void remove ( final ItemDescriptor descriptor )
    {
        final DataItem item = descriptor.getItem ();

        if ( !this._folder.remove ( item ) )
        {
            return;
        }

        if ( this._folder.size () <= 0 )
        {
            if ( this._parent != null )
            {
                this._parent.removeSubFolder ( this );
            }
        }
    }

    private void removeSubFolder ( final GroupSubFolder subFolder )
    {
        final String folderName = this._folder.findEntry ( subFolder );
        if ( folderName == null )
        {
            return;
        }

        subFolder.clearSubscribers ();
        this._folder.remove ( folderName );
        this._subFolders.remove ( folderName );

        if ( this._folder.size () <= 0 )
        {
            if ( this._parent != null )
            {
                this._parent.removeSubFolder ( this );
            }
        }
    }

    synchronized public Entry[] list ( final Stack<String> path ) throws NoSuchFolderException
    {
        return this._folder.list ( path );
    }

    synchronized public void subscribe ( final Stack<String> path, final FolderListener listener, final Object tag ) throws NoSuchFolderException
    {
        this._folder.subscribe ( path, listener, tag );
    }

    synchronized public void unsubscribe ( final Stack<String> path, final Object tag ) throws NoSuchFolderException
    {
        this._folder.unsubscribe ( path, tag );
    }

    public void clearSubscribers ()
    {
        this._folder.clearListeners ();
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
