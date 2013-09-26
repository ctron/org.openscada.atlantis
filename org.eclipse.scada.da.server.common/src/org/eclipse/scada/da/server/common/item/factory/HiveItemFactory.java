/*
 * This file is part of the OpenSCADA project
 * 
 * Copyright (C) 2006-2012 TH4 SYSTEMS GmbH (http://th4-systems.com)
 * Copyright (C) 2013 Jens Reimann (ctron@dentrassi.de)
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

package org.eclipse.scada.da.server.common.item.factory;

import java.util.ArrayList;

import org.eclipse.scada.da.server.common.DataItem;
import org.eclipse.scada.da.server.common.DataItemCommand;
import org.eclipse.scada.da.server.common.chain.DataItemInputChained;
import org.eclipse.scada.da.server.common.chain.WriteHandler;
import org.eclipse.scada.da.server.common.chain.WriteHandlerItem;
import org.eclipse.scada.da.server.common.impl.HiveCommon;

/**
 * This item factory creates the items and registers them in the hive
 * 
 * @author jens
 */
public class HiveItemFactory extends CommonItemFactory
{
    protected HiveCommon hive;

    public HiveItemFactory ( final HiveCommon hive )
    {
        super ( hive.getOperationService () );
        this.hive = hive;
    }

    public HiveItemFactory ( final CommonItemFactory parentItemFactory, final HiveCommon hive, final String baseId, final String idDelimiter )
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
    protected WriteHandlerItem constructWriteHandler ( final String localId, final WriteHandler writeHandler )
    {
        final WriteHandlerItem item = super.constructWriteHandler ( localId, writeHandler );
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
