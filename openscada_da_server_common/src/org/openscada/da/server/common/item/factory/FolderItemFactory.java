/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2008 inavare GmbH (http://inavare.com)
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

package org.openscada.da.server.common.item.factory;

import java.util.HashMap;
import java.util.Map;

import org.openscada.core.Variant;
import org.openscada.da.server.browser.common.FolderCommon;
import org.openscada.da.server.common.DataItem;
import org.openscada.da.server.common.DataItemCommand;
import org.openscada.da.server.common.chain.DataItemInputChained;
import org.openscada.da.server.common.chain.WriteHandler;
import org.openscada.da.server.common.chain.WriteHandlerItem;
import org.openscada.da.server.common.impl.HiveCommon;

public class FolderItemFactory extends HiveItemFactory
{
    private final String folderName;

    protected FolderCommon folder;

    private final FolderCommon parentFolder;

    public FolderItemFactory ( final HiveCommon hive, final FolderCommon parentFolder, final String baseId, final String folderName )
    {
        this ( null, hive, parentFolder, baseId, folderName, null );
    }

    public FolderItemFactory ( final ItemFactory parentFactory, final HiveCommon hive, final FolderCommon parentFolder, final String baseId, final String folderName )
    {
        this ( parentFactory, hive, parentFolder, baseId, folderName, null );
    }

    public FolderItemFactory ( final ItemFactory parentFactory, final HiveCommon hive, final FolderCommon parentFolder, final String baseId, final String folderName, final String idDelimiter )
    {
        super ( parentFactory, hive, baseId, idDelimiter );
        this.parentFolder = parentFolder;
        this.folderName = folderName;

        if ( this.parentFolder != null && this.folderName != null )
        {
            this.folder = new FolderCommon ();
            parentFolder.add ( this.folderName, this.folder, new HashMap<String, Variant> () );
        }
    }

    @Override
    public void dispose ()
    {
        if ( isDisposed () )
        {
            return;
        }

        if ( this.parentFolder != null && this.folder != null && this.folderName != null )
        {
            this.parentFolder.remove ( this.folderName );
        }
        super.dispose ();
    }

    @Override
    public void disposeItem ( final DataItem item )
    {
        super.disposeItem ( item );
        if ( this.folder != null )
        {
            this.folder.remove ( item );
        }
    }

    @Override
    protected DataItemCommand constructCommand ( final String localId )
    {
        final DataItemCommand item = super.constructCommand ( localId );
        addToFolder ( item, localId, new HashMap<String, Variant> () );
        return item;
    }

    @Override
    protected DataItemInputChained constructInput ( final String localId )
    {
        final DataItemInputChained item = super.constructInput ( localId );
        addToFolder ( item, localId, new HashMap<String, Variant> () );
        return item;
    }

    @Override
    protected WriteHandlerItem constructInputOutput ( final String localId, final WriteHandler writeHandler )
    {
        final WriteHandlerItem item = super.constructInputOutput ( localId, writeHandler );
        addToFolder ( item, localId, new HashMap<String, Variant> () );
        return item;
    }

    protected void addToFolder ( final DataItem item, final String name, final Map<String, Variant> attributes )
    {
        if ( this.folder == null )
        {
            return;
        }
        this.folder.add ( name, item.getInformation (), attributes );
    }

    public FolderCommon getFolder ()
    {
        return this.folder;
    }

    public FolderItemFactory createSubFolderFactory ( final String name )
    {
        final FolderItemFactory factory = new FolderItemFactory ( this, this.hive, this.folder, name, name );
        addSubFactory ( factory );
        return factory;
    }

    @Override
    public void disposeAllItems ()
    {
        this.folder.clear ();
        super.disposeAllItems ();
    }

}
