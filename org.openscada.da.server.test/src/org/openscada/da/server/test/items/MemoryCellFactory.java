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
