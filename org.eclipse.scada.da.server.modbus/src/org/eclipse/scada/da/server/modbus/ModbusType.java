/*******************************************************************************
 * Copyright (c) 2013 TH4 SYSTEMS GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     TH4 SYSTEMS GmbH - initial API and implementation
 *******************************************************************************/
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
