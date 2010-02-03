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

package org.openscada.da.server.common.impl.stats;

import java.util.HashMap;
import java.util.Map;

import org.openscada.core.Variant;
import org.openscada.da.server.browser.common.FolderCommon;
import org.openscada.da.server.common.chain.DataItemInputChained;
import org.openscada.da.server.common.impl.HiveCommon;
import org.openscada.utils.concurrent.DirectExecutor;

public class DataItemCounterOutput implements CounterOutput
{

    private final DataItemInputChained _valueItem;

    private final DataItemInputChained _totalItem;

    public DataItemCounterOutput ( final String itemId )
    {
        this._valueItem = new DataItemInputChained ( itemId + ".average", DirectExecutor.INSTANCE );
        this._totalItem = new DataItemInputChained ( itemId + ".total", DirectExecutor.INSTANCE );
    }

    public void register ( final HiveCommon hive, final FolderCommon rootFolder, final String description )
    {
        final Map<String, Variant> attributes = new HashMap<String, Variant> ();

        hive.registerItem ( this._valueItem );
        attributes.put ( "description", new Variant ( description + " - Average value" ) );
        rootFolder.add ( this._valueItem.getInformation ().getName (), this._valueItem, attributes );

        attributes.clear ();

        hive.registerItem ( this._totalItem );
        attributes.put ( "description", new Variant ( description + " - Total counter" ) );
        rootFolder.add ( this._totalItem.getInformation ().getName (), this._totalItem, attributes );
    }

    public void unregister ( final HiveCommon hive, final FolderCommon rootFolder )
    {
        rootFolder.remove ( this._valueItem );
        hive.unregisterItem ( this._valueItem );

        rootFolder.remove ( this._totalItem );
        hive.unregisterItem ( this._totalItem );

    }

    public void setTickValue ( final double average, final long total )
    {
        this._valueItem.updateData ( new Variant ( average ), null, null );
        this._totalItem.updateData ( new Variant ( total ), null, null );
    }

}
