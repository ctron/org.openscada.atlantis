/*
 * This file is part of the OpenSCADA project
 * 
 * Copyright (C) 2006-2011 TH4 SYSTEMS GmbH (http://th4-systems.com)
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

package org.eclipse.scada.da.server.common.chain;

import java.util.Collection;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;

import org.eclipse.scada.core.Variant;
import org.eclipse.scada.core.server.OperationParameters;
import org.eclipse.scada.da.core.DataItemInformation;
import org.eclipse.scada.da.core.WriteResult;
import org.eclipse.scada.da.data.IODirection;
import org.eclipse.scada.da.server.common.DataItemInformationBase;
import org.eclipse.scada.utils.concurrent.NotifyFuture;

public abstract class DataItemInputOutputChained extends DataItemInputChained
{
    public DataItemInputOutputChained ( final DataItemInformation di, final Executor executor )
    {
        super ( di, executor );
    }

    public DataItemInputOutputChained ( final String id, final Executor executor )
    {
        this ( new DataItemInformationBase ( id, EnumSet.of ( IODirection.INPUT, IODirection.OUTPUT ) ), executor );
    }

    @Override
    public NotifyFuture<WriteResult> startWriteValue ( Variant value, final OperationParameters operationParameters )
    {
        synchronized ( this )
        {
            final Collection<ChainProcessEntry> chain = getChainCopy ();

            final Map<String, Variant> primaryAttributes = new HashMap<String, Variant> ( this.primaryAttributes );

            for ( final ChainProcessEntry entry : chain )
            {
                if ( entry.getWhen ().contains ( IODirection.OUTPUT ) )
                {
                    final Variant newValue = entry.getWhat ().process ( value, primaryAttributes );
                    if ( newValue != null )
                    {
                        value = newValue;
                    }
                }
            }
        }
        // FIXME: for the moment output chain item don't show up in the attribute list
        // secondaryAttributes.set ( primaryAttributes );

        return startWriteCalculatedValue ( value, operationParameters );
    }

    protected abstract NotifyFuture<WriteResult> startWriteCalculatedValue ( final Variant value, OperationParameters operationParameters );
}
