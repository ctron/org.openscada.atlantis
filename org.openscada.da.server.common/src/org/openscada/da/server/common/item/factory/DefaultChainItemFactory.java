/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2012 TH4 SYSTEMS GmbH (http://th4-systems.com)
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

package org.openscada.da.server.common.item.factory;

import org.openscada.da.server.browser.common.FolderCommon;
import org.openscada.da.server.common.chain.DataItemBaseChained;
import org.openscada.da.server.common.chain.DataItemInputChained;
import org.openscada.da.server.common.chain.WriteHandler;
import org.openscada.da.server.common.chain.WriteHandlerItem;
import org.openscada.da.server.common.chain.item.ChainCreator;
import org.openscada.da.server.common.impl.HiveCommon;

/**
 * An item factory with a default chain of item entries
 * <p>
 * In order to apply your own chain you may override {@link #applyChain(DataItemBaseChained)}
 * @author Jens Reimann
 *
 */
public class DefaultChainItemFactory extends FolderItemFactory
{

    public DefaultChainItemFactory ( final HiveCommon hive, final FolderCommon parentFolder, final String baseId, final String folderName )
    {
        super ( hive, parentFolder, baseId, folderName );
    }

    public DefaultChainItemFactory ( final CommonItemFactory parentFactory, final HiveCommon hive, final FolderCommon parentFolder, final String baseId, final String folderName, final String idDelimiter )
    {
        super ( parentFactory, hive, parentFolder, baseId, folderName, idDelimiter );
    }

    public DefaultChainItemFactory ( final CommonItemFactory parentFactory, final HiveCommon hive, final FolderCommon parentFolder, final String baseId, final String folderName )
    {
        super ( parentFactory, hive, parentFolder, baseId, folderName );
    }

    /**
     * Create a new sub factory
     * <p>
     * Sub-factories get disposed when the parent factory is disposed
     * </p>
     * @param name the name of the sub-factory, this will be the folder name and added to the item id as local part
     */
    @Override
    public DefaultChainItemFactory createSubFolderFactory ( final String name )
    {
        final DefaultChainItemFactory factory = new DefaultChainItemFactory ( this, this.hive, this.folder, name, name );
        addSubFactory ( factory );
        return factory;
    }

    @Override
    public DataItemInputChained constructInput ( final String localId )
    {
        final DataItemInputChained item = super.constructInput ( localId );

        applyChain ( item );

        return item;
    }

    @Override
    public WriteHandlerItem constructInputOutput ( final String localId, final WriteHandler writeHandler )
    {
        final WriteHandlerItem item = super.constructInputOutput ( localId, writeHandler );

        applyChain ( item );

        return item;
    }

    /**
     * Apply our chain of elements
     * @param item the item to which the chain should be applied
     */
    protected void applyChain ( final DataItemBaseChained item )
    {
        ChainCreator.applyDefaultInputChain ( item, this.hive );
    }

}
