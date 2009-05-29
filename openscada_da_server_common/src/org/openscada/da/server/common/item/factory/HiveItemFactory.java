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

import java.util.ArrayList;

import org.openscada.da.server.common.DataItem;
import org.openscada.da.server.common.DataItemCommand;
import org.openscada.da.server.common.chain.DataItemInputChained;
import org.openscada.da.server.common.chain.WriteHandler;
import org.openscada.da.server.common.chain.WriteHandlerItem;
import org.openscada.da.server.common.impl.HiveCommon;

/**
 * This item factory creates the items and registers them in the hive
 * @author jens
 *
 */
public class HiveItemFactory extends CommonItemFactory
{
    protected HiveCommon hive;

    public HiveItemFactory ( final HiveCommon hive )
    {
        super ( hive.getOperationService () );
        this.hive = hive;
    }

    public HiveItemFactory ( final ItemFactory parentItemFactory, final HiveCommon hive, final String baseId, final String idDelimiter )
    {
        super ( hive.getOperationService (), parentItemFactory, baseId, idDelimiter );
        this.hive = hive;
    }

    @Override
    protected DataItemCommand constructCommand ( final String localId )
    {
        final DataItemCommand item = super.constructCommand ( localId );
        this.hive.registerItem ( item );
        return item;
    }

    @Override
    protected DataItemInputChained constructInput ( final String localId )
    {
        final DataItemInputChained item = super.constructInput ( localId );
        this.hive.registerItem ( item );
        return item;
    }

    @Override
    protected WriteHandlerItem constructInputOutput ( final String localId, final WriteHandler writeHandler )
    {
        final WriteHandlerItem item = super.constructInputOutput ( localId, writeHandler );
        this.hive.registerItem ( item );
        return item;
    }

    @Override
    public void disposeItem ( final DataItem item )
    {
        super.disposeItem ( item );
        this.hive.unregisterItem ( item );
    }

    @Override
    public void disposeAllItems ()
    {
        for ( final DataItem item : new ArrayList<DataItem> ( this.itemMap.values () ) )
        {
            this.hive.unregisterItem ( item );
        }
        super.disposeAllItems ();
    }
}
