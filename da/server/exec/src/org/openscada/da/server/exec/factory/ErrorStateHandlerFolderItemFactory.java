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

/**
 */
package org.openscada.da.server.exec.factory;

import org.openscada.da.core.IODirection;
import org.openscada.da.server.browser.common.FolderCommon;
import org.openscada.da.server.common.chain.DataItemInputChained;
import org.openscada.da.server.common.chain.item.SumErrorChainItem;
import org.openscada.da.server.common.impl.HiveCommon;
import org.openscada.da.server.common.item.factory.FolderItemFactory;
import org.openscada.da.server.common.item.factory.ItemFactory;

/**
 * @author Fabian Biebl
 * (not a nice class)
 */
public class ErrorStateHandlerFolderItemFactory extends FolderItemFactory
{
    /**
     * Remember the hive
     */
    private final HiveCommon theHive;

    /**
     * parent folder name
     */
    private final FolderCommon theParentFolder;

    /**
     * @param hive
     * @param parentFolder
     * @param baseId
     * @param folderName
     */
    public ErrorStateHandlerFolderItemFactory ( HiveCommon hive, FolderCommon parentFolder, String baseId, String folderName )
    {
        super ( hive, parentFolder, baseId, folderName );
        this.theHive = hive;
        this.theParentFolder = parentFolder;
    }

    /**
     * @param parentFactory
     * @param hive
     * @param parentFolder
     * @param baseId
     * @param folderName
     */
    public ErrorStateHandlerFolderItemFactory ( ItemFactory parentFactory, HiveCommon hive, FolderCommon parentFolder, String baseId, String folderName )
    {
        super ( parentFactory, hive, parentFolder, baseId, folderName );
        this.theHive = hive;
        this.theParentFolder = parentFolder;
    }

    /**
     * @param parentFactory
     * @param hive
     * @param parentFolder
     * @param baseId
     * @param folderName
     * @param idDelimiter
     */
    public ErrorStateHandlerFolderItemFactory ( ItemFactory parentFactory, HiveCommon hive, FolderCommon parentFolder, String baseId, String folderName, String idDelimiter )
    {
        super ( parentFactory, hive, parentFolder, baseId, folderName, idDelimiter );
        this.theHive = hive;
        this.theParentFolder = parentFolder;
    }

    /* (non-Javadoc)
     * @see org.openscada.da.server.common.item.factory.FolderItemFactory#constructInput(java.lang.String)
     */
    public DataItemInputChained constructErrorChainInput ( String localId )
    {
        DataItemInputChained item = super.constructInput ( localId );
        item.addChainElement ( IODirection.INPUT, new SumErrorChainItem ( this.theHive ) );
        return item;
    }

    @Override
    public ErrorStateHandlerFolderItemFactory createSubFolderFactory ( String name )
    {
        final ErrorStateHandlerFolderItemFactory factory = new ErrorStateHandlerFolderItemFactory ( this, this.theHive, this.theParentFolder, this.generateId ( name ), name );
        this.addSubFactory ( factory );
        return factory;
    }
}
