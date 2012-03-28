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

package org.openscada.da.server.common.osgi.factory;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.Executor;

import org.openscada.da.server.common.DataItem;
import org.openscada.da.server.common.DataItemCommand;
import org.openscada.da.server.common.chain.DataItemInputChained;
import org.openscada.da.server.common.chain.WriteHandler;
import org.openscada.da.server.common.chain.WriteHandlerItem;
import org.openscada.da.server.common.item.factory.ItemFactory;
import org.openscada.utils.osgi.pool.ManageableObjectPool;

public class ObjectPoolDataItemFactory implements ItemFactory
{
    private final ManageableObjectPool objectPool;

    private final Executor executor;

    private final String prefix;

    private final Map<String, DataItem> items = new HashMap<String, DataItem> ();

    public ObjectPoolDataItemFactory ( final Executor executor, final ManageableObjectPool objectPool, final String prefix )
    {
        this.executor = executor;
        this.objectPool = objectPool;
        this.prefix = prefix;
    }

    protected String getId ( final String localId )
    {
        if ( this.prefix == null )
        {
            return localId;
        }
        else
        {
            return this.prefix + localId;
        }
    }

    @Override
    public synchronized DataItemCommand createCommand ( final String localId )
    {
        return registerItem ( new DataItemCommand ( getId ( localId ), this.executor ) );
    }

    @Override
    public synchronized DataItemInputChained createInput ( final String localId )
    {
        return registerItem ( new DataItemInputChained ( getId ( localId ), this.executor ) );
    }

    @Override
    public synchronized WriteHandlerItem createInputOutput ( final String localId, final WriteHandler writeHandler )
    {
        return registerItem ( new WriteHandlerItem ( getId ( localId ), writeHandler, this.executor ) );
    }

    private <T extends DataItem> T registerItem ( final T item )
    {
        final Dictionary<String, String> properties = new Hashtable<String, String> ( 0 );

        final String itemId = item.getInformation ().getName ();

        // remove old item first
        final DataItem oldItem = this.items.remove ( itemId );
        if ( oldItem != null )
        {
            this.objectPool.removeService ( itemId, oldItem );
        }

        this.items.put ( itemId, item );
        this.objectPool.addService ( itemId, item, properties );

        return item;
    }

    protected void unregisterItem ( final DataItem dataItem )
    {
        this.objectPool.removeService ( dataItem.getInformation ().getName (), dataItem );
    }

    @Override
    public synchronized void dispose ()
    {
        disposeAllItems ();
    }

    @Override
    public synchronized void disposeAllItems ()
    {
        for ( final Map.Entry<String, DataItem> entry : this.items.entrySet () )
        {
            unregisterItem ( entry.getValue () );
        }
        this.items.clear ();
    }

    @Override
    public synchronized void disposeItem ( final DataItem dataItem )
    {
        final String itemId = dataItem.getInformation ().getName ();

        if ( this.items.get ( itemId ) != dataItem )
        {
            return;
        }

        this.items.remove ( itemId );
        unregisterItem ( dataItem );
    }

}
