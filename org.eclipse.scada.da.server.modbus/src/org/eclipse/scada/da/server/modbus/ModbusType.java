/*
 * This file is part of the openSCADA project
 * 
 * Copyright (C) 2006-2010 TH4 SYSTEMS GmbH (http://th4-systems.com)
 *
 * openSCADA is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 3
 * only, as published by the Free Software Foundation.
 *
 * openSCADA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License version 3 for more details
 * (a copy is included in the LICENSE file that accompanied this code).
 *
 * You should have received a copy of the GNU Lesser General Public License
 * version 3 along with openSCADA. If not, see
 * <http://opensource.org/licenses/lgpl-3.0.html> for a copy of the LGPLv3 License.
 */

package org.eclipse.scada.da.server.modbus;

import java.nio.ByteBuffer;

import org.eclipse.scada.core.NotConvertableException;
import org.eclipse.scada.core.NullValueException;
import org.eclipse.scada.core.Variant;

public enum ModbusType
{
    BOOLEAN ( 1 ),
    INT16 ( 1 ),
    INT32 ( 2 ),
    INT64 ( 4 ),
    FLOAT32 ( 2 ),
    FLOAT64 ( 4 );

    private final int size;

    private ModbusType ( final int size )
    {
        this.size = size;
    }

    /**
     * @return size in no of registers
     */
    public int getSize ()
    {
        return this.size;
    }

    public byte[] convertVariant ( final Variant value ) throws NullValueException, NotConvertableException
    {
        final byte[] result = new byte[getSize () * 2];
        final ByteBuffer buffer = ByteBuffer.wrap ( result );
        switch ( this )
        {
            case BOOLEAN:
                if ( value.asBoolean () )
                {
                    buffer.put ( (byte)0xff );
                    buffer.put ( (byte)0x00 );
                }
                else
                {
                    buffer.put ( (byte)0x00 );
                    buffer.put ( (byte)0x00 );
                }
                break;
            case INT16:
                buffer.putShort ( (short)value.asInteger () );
                break;
            case INT32:
                buffer.putInt ( value.asInteger () );
                break;
            case INT64:
                buffer.putLong ( value.asLong () );
                break;
            case FLOAT32:
                buffer.putFloat ( (float)value.asDouble () );
                break;
            case FLOAT64:
                buffer.putDouble ( value.asDouble () );
                break;
        }
        return result;
    }
}
