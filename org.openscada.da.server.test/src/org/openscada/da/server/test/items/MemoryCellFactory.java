/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2010 inavare GmbH (http://inavare.com)
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

package org.openscada.da.server.test.items;

import org.openscada.da.server.common.DataItem;
import org.openscada.da.server.common.chain.ChainProcessEntry;
import org.openscada.da.server.common.factory.DataItemFactory;
import org.openscada.da.server.common.factory.DataItemFactoryRequest;
import org.openscada.da.server.test.Hive;

public class MemoryCellFactory implements DataItemFactory
{
    private Hive hive = null;

    public MemoryCellFactory ( final Hive hive )
    {
        this.hive = hive;
    }

    public boolean canCreate ( final DataItemFactoryRequest request )
    {
        return request.getId ().matches ( "memory\\..*" );
    }

    public DataItem create ( final DataItemFactoryRequest request )
    {
        final FactoryMemoryCell item = new FactoryMemoryCell ( this.hive, request.getId () );

        for ( final ChainProcessEntry entry : request.getItemChain () )
        {
            item.addChainElement ( entry.getWhen (), entry.getWhat () );
        }

        try
        {
            item.startSetAttributes ( null, request.getItemAttributes () ).get ();
        }
        catch ( final Throwable e )
        {
        }
        this.hive.addMemoryFactoryItem ( item, request.getBrowserAttributes () );
        return item;
    }

}
