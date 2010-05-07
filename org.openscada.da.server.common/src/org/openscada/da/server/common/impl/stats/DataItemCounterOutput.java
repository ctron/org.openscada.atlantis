/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2010 inavare GmbH (http://inavare.com)
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
