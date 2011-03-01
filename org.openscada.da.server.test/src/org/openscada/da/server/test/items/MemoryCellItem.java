/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2011 TH4 SYSTEMS GmbH (http://th4-systems.com)
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

import java.util.HashMap;
import java.util.Map;

import org.openscada.core.Variant;
import org.openscada.da.core.OperationParameters;
import org.openscada.da.core.WriteAttributeResults;
import org.openscada.da.core.WriteResult;
import org.openscada.da.server.browser.common.FolderCommon;
import org.openscada.da.server.common.AttributeManager;
import org.openscada.da.server.common.DataItemOutput;
import org.openscada.da.server.common.WriteAttributesHelper;
import org.openscada.da.server.common.chain.MemoryItemChained;
import org.openscada.da.server.test.Hive;
import org.openscada.utils.collection.MapBuilder;
import org.openscada.utils.concurrent.InstantErrorFuture;
import org.openscada.utils.concurrent.InstantFuture;
import org.openscada.utils.concurrent.NotifyFuture;

public class MemoryCellItem extends DataItemOutput
{
    private final Hive hive;

    private Map<Integer, MemoryItemChained> items = new HashMap<Integer, MemoryItemChained> ();

    private AttributeManager attributes = null;

    private FolderCommon folder = null;

    public MemoryCellItem ( final Hive hive, final String name, final FolderCommon folder )
    {
        super ( name );
        this.hive = hive;
        this.folder = folder;

        this.attributes = new AttributeManager ( this );

        updateCells ( 0 );
    }

    @Override
    public Map<String, Variant> getAttributes ()
    {
        return this.attributes.getCopy ();
    }

    @Override
    public NotifyFuture<WriteAttributeResults> startSetAttributes ( final Map<String, Variant> attributes, final OperationParameters operationParameters )
    {
        return new InstantFuture<WriteAttributeResults> ( WriteAttributesHelper.errorUnhandled ( null, attributes ) );
    }

    @Override
    public NotifyFuture<WriteResult> startWriteValue ( final Variant value, final OperationParameters operationParameters )
    {
        int num;
        try
        {
            num = value.asInteger ();
            updateCells ( num );
            return new InstantFuture<WriteResult> ( WriteResult.OK );
        }
        catch ( final Throwable e )
        {
            return new InstantErrorFuture<WriteResult> ( e );
        }
    }

    private void setSizeAttribute ( final int num )
    {
        this.attributes.update ( "size", Variant.valueOf ( num ) );
    }

    private void updateCells ( int num )
    {
        if ( num < 0 )
        {
            num = 0;
        }

        synchronized ( this.items )
        {
            final Map<Integer, MemoryItemChained> newItems = new HashMap<Integer, MemoryItemChained> ( num );

            int pos;
            for ( pos = 0; pos < num && pos < this.items.size (); pos++ )
            {
                if ( this.items.containsKey ( pos ) )
                {
                    newItems.put ( pos, this.items.get ( pos ) );
                    this.items.remove ( pos );
                }
            }

            for ( final Map.Entry<Integer, MemoryItemChained> entry : this.items.entrySet () )
            {
                this.folder.remove ( entry.getKey ().toString () );
                this.hive.unregisterItem ( entry.getValue () );
            }

            for ( int i = pos; i < num; i++ )
            {
                final MemoryItemChained item = new MemoryItemChained ( getInformation ().getName () + "-" + i );

                MemoryChainedItem.applyDefaultInputChain ( this.hive, item );

                this.hive.registerItem ( item );
                this.folder.add ( String.valueOf ( i ), item, new MapBuilder<String, Variant> ().put ( "description", new Variant ( "Cell #" + i + " of " + num + " automaticall provided memory cells." ) ).getMap () );
                newItems.put ( i, item );
            }

            this.items = newItems;

            setSizeAttribute ( num );
        }
    }
}
