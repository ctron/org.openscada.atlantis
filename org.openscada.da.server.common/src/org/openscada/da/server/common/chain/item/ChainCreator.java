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

package org.openscada.da.server.common.chain.item;

import org.openscada.da.core.IODirection;
import org.openscada.da.server.common.HiveServiceRegistry;
import org.openscada.da.server.common.chain.DataItemBaseChained;

/**
 * A default chain creator 
 * @author Jens Reimann
 *
 */
public class ChainCreator
{
    public static void applyDefaultInputChain ( final DataItemBaseChained item, final HiveServiceRegistry serviceRegistry )
    {
        item.addChainElement ( IODirection.INPUT, new NegateInputItem ( serviceRegistry ) );
        item.addChainElement ( IODirection.INPUT, new ScaleInputItem ( serviceRegistry ) );
        item.addChainElement ( IODirection.INPUT, new ManualOverrideChainItem ( serviceRegistry ) );
        item.addChainElement ( IODirection.INPUT, new LevelAlarmChainItem ( serviceRegistry ) );
        item.addChainElement ( IODirection.INPUT, new SumAlarmChainItem ( serviceRegistry ) );
        item.addChainElement ( IODirection.INPUT, new SumErrorChainItem ( serviceRegistry ) );
        item.addChainElement ( IODirection.INPUT, new ManualErrorOverrideChainItem () );
    }
}
