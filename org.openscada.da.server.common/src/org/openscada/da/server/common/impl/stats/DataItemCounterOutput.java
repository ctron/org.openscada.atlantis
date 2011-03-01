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

    private final DataItemInputChained valueItem;

    private final DataItemInputChained totalItem;

    public DataItemCounterOutput ( final String itemId )
    {
        this.valueItem = new DataItemInputChained ( itemId + ".average", DirectExecutor.INSTANCE );
        this.totalItem = new DataItemInputChained ( itemId + ".total", DirectExecutor.INSTANCE );
    }

    @Override
    public void register ( final HiveCommon hive, final FolderCommon rootFolder, final String description )
    {
        final Map<String, Variant> attributes = new HashMap<String, Variant> ();

        hive.registerItem ( this.valueItem );
        attributes.put ( "description", Variant.valueOf ( description + " - Average value" ) );
        rootFolder.add ( this.valueItem.getInformation ().getName (), this.valueItem, attributes );

        attributes.clear ();

        hive.registerItem ( this.totalItem );
        attributes.put ( "description", Variant.valueOf ( description + " - Total counter" ) );
        rootFolder.add ( this.totalItem.getInformation ().getName (), this.totalItem, attributes );
    }

    @Override
    public void unregister ( final HiveCommon hive, final FolderCommon rootFolder )
    {
        rootFolder.remove ( this.valueItem );
        hive.unregisterItem ( this.valueItem );

        rootFolder.remove ( this.totalItem );
        hive.unregisterItem ( this.totalItem );

    }

    @Override
    public void setTickValue ( final double average, final long total )
    {
        this.valueItem.updateData ( Variant.valueOf ( average ), null, null );
        this.totalItem.updateData ( Variant.valueOf ( total ), null, null );
    }

}
