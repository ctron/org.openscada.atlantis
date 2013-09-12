/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2011 TH4 SYSTEMS GmbH (http://th4-systems.com)
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

import org.eclipse.scada.core.Variant;
import org.eclipse.scada.utils.collection.MapBuilder;
import org.openscada.da.core.browser.Entry;
import org.openscada.da.core.server.browser.NoSuchFolderException;
import org.openscada.da.server.browser.common.Folder;
import org.openscada.da.server.browser.common.FolderCommon;
import org.openscada.da.server.browser.common.FolderListener;
import org.openscada.da.server.common.DataItem;

public class GroupSubFolder implements Folder
{
    private GroupSubFolder parent = null;

    private NameProvider nameProvider = null;

    private final FolderCommon folder = new FolderCommon ();

    private final Map<String, GroupSubFolder> subFolders = new HashMap<String, GroupSubFolder> ();

    public GroupSubFolder ( final NameProvider nameProvider )
    {
        this ( null, nameProvider );
    }

    private GroupSubFolder ( final GroupSubFolder parent, final NameProvider nameProvider )
    {
        this.parent = parent;
        this.nameProvider = nameProvider;
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
        final String name = this.nameProvider.getName ( descriptor );

        if ( name == null )
        {
            return false;
        }

        this.folder.add ( name, descriptor.getItem (), descriptor.getAttributes () );

        return true;
    }

    private GroupSubFolder getSubFolder ( final String name )
    {
        if ( !this.subFolders.containsKey ( name ) )
        {
            final GroupSubFolder folder = new GroupSubFolder ( this, this.nameProvider );
            this.subFolders.put ( name, folder );
            final MapBuilder<String, Variant> builder = new MapBuilder<String, Variant> ();
            this.folder.add ( name, folder, builder.getMap () );
        }
        return this.subFolders.get ( name );
    }

    public void remove ( final ItemDescriptor descriptor )
    {
        final DataItem item = descriptor.getItem ();

        if ( !this.folder.remove ( item ) )
        {
            return;
        }

        if ( this.folder.size () <= 0 )
        {
            if ( this.parent != null )
            {
                this.parent.removeSubFolder ( this );
            }
        }
    }

    private void removeSubFolder ( final GroupSubFolder subFolder )
    {
        final String folderName = this.folder.findEntry ( subFolder );
        if ( folderName == null )
        {
            return;
        }

        subFolder.clearSubscribers ();
        this.folder.remove ( folderName );
        this.subFolders.remove ( folderName );

        if ( this.folder.size () <= 0 )
        {
            if ( this.parent != null )
            {
                this.parent.removeSubFolder ( this );
            }
        }
    }

    @Override
    synchronized public Entry[] list ( final Stack<String> path ) throws NoSuchFolderException
    {
        return this.folder.list ( path );
    }

    @Override
    synchronized public void subscribe ( final Stack<String> path, final FolderListener listener, final Object tag ) throws NoSuchFolderException
    {
        this.folder.subscribe ( path, listener, tag );
    }

    @Override
    synchronized public void unsubscribe ( final Stack<String> path, final Object tag ) throws NoSuchFolderException
    {
        this.folder.unsubscribe ( path, tag );
    }

    public void clearSubscribers ()
    {
        this.folder.clearListeners ();
    }

    @Override
    public void added ()
    {
        this.folder.added ();
    }

    @Override
    public void removed ()
    {
        this.folder.removed ();
    }
}
