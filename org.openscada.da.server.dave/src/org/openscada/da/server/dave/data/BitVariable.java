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

package org.openscada.da.server.dave.data;

import java.util.Map;
import java.util.concurrent.Executor;

import org.apache.mina.core.buffer.IoBuffer;
import org.eclipse.scada.core.OperationException;
import org.eclipse.scada.core.Variant;
import org.eclipse.scada.utils.concurrent.InstantErrorFuture;
import org.eclipse.scada.utils.concurrent.InstantFuture;
import org.eclipse.scada.utils.concurrent.NotifyFuture;
import org.eclipse.scada.utils.osgi.pool.ManageableObjectPool;
import org.openscada.da.core.WriteResult;
import org.openscada.da.server.common.DataItem;
import org.openscada.da.server.dave.DaveDevice;

public class BitVariable extends ScalarVariable
{
    private final int subIndex;

    public BitVariable ( final String name, final int index, final int subIndex, final Executor executor, final ManageableObjectPool<DataItem> itemPool, final Attribute... attributes )
    {
        super ( name, index, executor, itemPool, attributes );
        this.subIndex = subIndex;
    }

    @Override
    protected Variant extractValue ( final IoBuffer data, final Map<String, Variant> attributes )
    {
        final byte b = data.get ( toAddress ( this.index ) );
        final boolean flag = ( b & 1 << this.subIndex ) != 0;
        return Variant.valueOf ( flag );
    }

    @Override
    protected NotifyFuture<WriteResult> handleWrite ( final Variant value )
    {
        final DaveDevice dave = this.device;

        if ( dave == null )
        {
            return new InstantErrorFuture<WriteResult> ( new OperationException ( "Device not connected" ).fillInStackTrace () );
        }

        this.device.writeBit ( this.block, toGlobalAddress ( this.index ), this.subIndex, value.asBoolean () );

        return new InstantFuture<WriteResult> ( new WriteResult () );
    }
}
