/*
 * This file is part of the openSCADA project
 * 
 * Copyright (C) 2006-2010 TH4 SYSTEMS GmbH (http://th4-systems.com)
 * Copyright (C) 2013 Jens Reimann (ctron@dentrassi.de)
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

package org.openscada.da.server.modbus;

import java.io.Serializable;

import org.openscada.da.modbus.configuration.ParityType;
import org.openscada.da.modbus.configuration.StopBitsType;
import org.openscada.utils.lang.Immutable;

@Immutable
public class Rs232Information implements Serializable
{
    private static final long serialVersionUID = 7742677764862312475L;

    private final int baudRate;

    private final ParityType parity;

    private final int dataBits;

    private final StopBitsType stopBits;

    private final long nanoBitLength;

    private final long nanoCharLength;

    public Rs232Information ( final int baudRate, final ParityType parity, final int dataBits, final StopBitsType stopBits )
    {
        // check constraints
        if ( dataBits < 7 || dataBits > 8 )
        {
            throw new IllegalArgumentException ( "dataBits has to be 7 or 8" );
        }

        this.baudRate = baudRate;
        this.parity = parity;
        this.dataBits = dataBits;
        this.stopBits = stopBits;

        this.nanoBitLength = Math.round ( 1000000000.0d / this.baudRate );
        this.nanoCharLength = Math.round ( ( dataBits + getStopBitsValue () + ( parity != ParityType.NONE ? 1 : 0 ) ) * this.nanoBitLength );
    }

    private double getStopBitsValue ()
    {
        if ( this.stopBits == null )
        {
            return 0.0;
        }

        switch ( this.stopBits )
        {
            case _1:
                return 1.0;
            case _15:
                return 1.5;
            case _2:
                return 2;
            default:
                throw new IllegalArgumentException ( "stopBits has to be 1, 1.5 or 2" );
        }
    }

    public long getBitLengthAsNano ()
    {
        return this.nanoBitLength;
    }

    public long getCharLengthAsNano ()
    {
        return this.nanoCharLength;
    }

    public int getBaudRate ()
    {
        return this.baudRate;
    }

    public ParityType getParity ()
    {
        return this.parity;
    }

    public StopBitsType getStopBits ()
    {
        return this.stopBits;
    }

    @Override
    public String toString ()
    {
        final StringBuilder sb = new StringBuilder ();
        sb.append ( this.baudRate );
        sb.append ( "@" );
        sb.append ( this.dataBits );
        switch ( this.parity )
        {
            case NONE:
                sb.append ( "N" );
                break;
            case EVEN:
                sb.append ( "E" );
                break;
            case ODD:
                sb.append ( "O" );
                break;
            case MARK:
                sb.append ( "M" );
                break;
            case SPACE:
                sb.append ( "S" );
                break;
        }
        sb.append ( this.stopBits.getLiteral () );
        return sb.toString ();
    }
}
