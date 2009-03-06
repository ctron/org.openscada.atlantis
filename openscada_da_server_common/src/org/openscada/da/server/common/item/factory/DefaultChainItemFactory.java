/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2009 inavare GmbH (http://inavare.com)
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

    public DefaultChainItemFactory ( final ItemFactory parentFactory, final HiveCommon hive, final FolderCommon parentFolder, final String baseId, final String folderName, final String idDelimiter )
    {
        super ( parentFactory, hive, parentFolder, baseId, folderName, idDelimiter );
    }

    public DefaultChainItemFactory ( final ItemFactory parentFactory, final HiveCommon hive, final FolderCommon parentFolder, final String baseId, final String folderName )
    {
        super ( parentFactory, hive, parentFolder, baseId, folderName );
    }

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
