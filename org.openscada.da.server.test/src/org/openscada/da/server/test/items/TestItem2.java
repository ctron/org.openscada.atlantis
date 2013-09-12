/*
 * This file is part of the OpenSCADA project
 * 
 * Copyright (C) 2006-2012 TH4 SYSTEMS GmbH (http://th4-systems.com)
 * Copyright (C) 2013 Jens Reimann (ctron@dentrassi.de)
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

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.scada.core.InvalidOperationException;
import org.eclipse.scada.core.NotConvertableException;
import org.eclipse.scada.core.NullValueException;
import org.eclipse.scada.core.Variant;
import org.eclipse.scada.utils.concurrent.InstantErrorFuture;
import org.eclipse.scada.utils.concurrent.InstantFuture;
import org.eclipse.scada.utils.concurrent.NotifyFuture;
import org.openscada.core.server.OperationParameters;
import org.openscada.da.core.WriteResult;
import org.openscada.da.data.IODirection;
import org.openscada.da.server.common.AttributeMode;
import org.openscada.da.server.common.DataItemInformationBase;
import org.openscada.da.server.common.chain.MemoryItemChained;
import org.openscada.da.server.common.chain.item.SumErrorChainItem;
import org.openscada.da.server.common.impl.HiveCommon;

public class TestItem2 extends MemoryItemChained
{

    public TestItem2 ( final HiveCommon hive, final String id )
    {
        super ( new DataItemInformationBase ( id, EnumSet.of ( IODirection.INPUT, IODirection.OUTPUT ) ) );
        addChainElement ( IODirection.INPUT, new SumErrorChainItem () );
    }

    @Override
    protected NotifyFuture<WriteResult> startWriteCalculatedValue ( final Variant value, final OperationParameters operationParameters )
    {
        try
        {
            performWriteValue ( value, operationParameters );
            return new InstantFuture<WriteResult> ( new WriteResult () );
        }
        catch ( final Throwable e )
        {
            return new InstantErrorFuture<WriteResult> ( e );
        }
    }

    protected synchronized void performWriteValue ( final Variant value, final OperationParameters operationParameters ) throws InvalidOperationException, NullValueException, NotConvertableException
    {
        final Map<String, Variant> attr = new HashMap<String, Variant> ();

        if ( value.isNull () )
        {
            attr.put ( "test.error", null );
        }
        else
        {
            attr.put ( "test.error", Variant.valueOf ( value ) );
        }
        updateData ( null, attr, AttributeMode.UPDATE );
    }

}
