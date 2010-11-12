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

package org.openscada.da.server.snmp;

import org.openscada.da.core.IODirection;
import org.openscada.da.server.browser.common.FolderCommon;
import org.openscada.da.server.common.chain.DataItemInputChained;
import org.openscada.da.server.common.chain.item.LevelAlarmChainItem;
import org.openscada.da.server.common.chain.item.ManualErrorOverrideChainItem;
import org.openscada.da.server.common.chain.item.ManualOverrideChainItem;
import org.openscada.da.server.common.chain.item.ScaleInputItem;
import org.openscada.da.server.common.chain.item.SumAlarmChainItem;
import org.openscada.da.server.common.chain.item.SumErrorChainItem;
import org.openscada.da.server.common.impl.HiveCommon;
import org.openscada.da.server.common.item.factory.FolderItemFactory;

/**
 * our default item factory for folders
 * @author jens Reimann
 *
 */
public class DefaultFolderItemFactory extends FolderItemFactory
{

    public DefaultFolderItemFactory ( final HiveCommon hive, final FolderCommon parentFolder, final String baseId, final String folderName )
    {
        super ( hive, parentFolder, baseId, folderName );
    }

    @Override
    protected DataItemInputChained constructInput ( final String localId )
    {
        final DataItemInputChained item = super.constructInput ( localId );
        applyDefaultInputChain ( this.hive, item );
        return item;
    }

    public static void applyDefaultInputChain ( final HiveCommon hive, final DataItemInputChained item )
    {
        item.addChainElement ( IODirection.INPUT, new SumErrorChainItem ( hive ) );
        item.addChainElement ( IODirection.INPUT, new ScaleInputItem ( hive ) );
        item.addChainElement ( IODirection.INPUT, new ManualOverrideChainItem ( hive ) );
        item.addChainElement ( IODirection.INPUT, new ManualErrorOverrideChainItem () );
        item.addChainElement ( IODirection.INPUT, new LevelAlarmChainItem ( hive ) );
        item.addChainElement ( IODirection.INPUT, new SumAlarmChainItem ( hive ) );
    }

}
