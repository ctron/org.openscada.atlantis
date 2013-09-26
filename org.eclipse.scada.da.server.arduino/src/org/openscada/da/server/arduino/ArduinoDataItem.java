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

package org.openscada.da.server.arduino;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;

import org.eclipse.scada.core.Variant;
import org.eclipse.scada.protocol.arduino.ArduinoDevice;
import org.eclipse.scada.utils.concurrent.InstantErrorFuture;
import org.eclipse.scada.utils.concurrent.InstantFuture;
import org.eclipse.scada.utils.concurrent.NotifyFuture;
import org.openscada.core.server.OperationParameters;
import org.openscada.da.core.DataItemInformation;
import org.openscada.da.core.WriteResult;
import org.openscada.da.server.common.AttributeMode;
import org.openscada.da.server.common.chain.DataItemInputOutputChained;

public class ArduinoDataItem extends DataItemInputOutputChained
{

    private final ArduinoDevice device;

    private final short itemIndex;

    public ArduinoDataItem ( final ArduinoDevice device, final short itemIndex, final DataItemInformation information, final Executor executor )
    {
        super ( information, executor );
        this.device = device;
        this.itemIndex = itemIndex;

        final Map<String, Variant> attributes = new HashMap<String, Variant> ( 1 );
        attributes.put ( "itemIndex", Variant.valueOf ( itemIndex ) );
        updateData ( null, attributes, AttributeMode.SET );
    }

    @Override
    protected NotifyFuture<WriteResult> startWriteCalculatedValue ( final Variant value, final OperationParameters operationParameters )
    {
        try
        {
            this.device.sendWrite ( this.itemIndex, value.as ( null ) );
        }
        catch ( final Exception e )
        {
            return new InstantErrorFuture<WriteResult> ( e );
        }
        return new InstantFuture<WriteResult> ( WriteResult.OK );
    }

}
