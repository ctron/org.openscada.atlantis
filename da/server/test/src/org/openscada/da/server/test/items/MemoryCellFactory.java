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

package org.openscada.da.server.test.items;

import org.openscada.da.core.common.DataItem;
import org.openscada.da.core.common.chain.ChainProcessEntry;
import org.openscada.da.core.common.factory.DataItemFactory;
import org.openscada.da.core.common.factory.DataItemFactoryRequest;
import org.openscada.da.server.test.Hive;

public class MemoryCellFactory implements DataItemFactory
{
    private Hive _hive = null;
    
    public MemoryCellFactory ( Hive hive )
    {
        _hive = hive;
    }
    
    public boolean canCreate ( DataItemFactoryRequest request )
    {
        return request.getId ().matches ( "memory\\..*" );
    }

    public DataItem create ( DataItemFactoryRequest request )
    {
        FactoryMemoryCell item = new FactoryMemoryCell ( _hive, request.getId () );
        
        for ( ChainProcessEntry entry : request.getItemChain () )
        {
            item.addChainElement ( entry.getWhen (), entry.getWhat () );
        }
        
        item.setAttributes ( request.getItemAttributes () );
        _hive.addMemoryFactoryItem ( item, request.getBrowserAttributes () );
        return item;
    }

}
