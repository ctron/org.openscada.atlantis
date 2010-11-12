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

package org.openscada.da.server.dave.data;

import java.util.Map;

import org.apache.mina.core.buffer.IoBuffer;
import org.openscada.core.Variant;
import org.openscada.da.server.dave.DaveDevice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implement a single bit attribute
 * @author Jens Reimann
 *
 */
public class TriBitAttribute extends AbstractAttribute implements Attribute
{

    private final static Logger logger = LoggerFactory.getLogger ( TriBitAttribute.class );

    private final int readIndex;

    private final int readSubIndex;

    private final int writeTrueIndex;

    private final int writeTrueSubIndex;

    private final int writeFalseIndex;

    private final int writeFalseSubIndex;

    private final boolean invertRead;

    private Boolean lastValue;

    private Variant lastTimestamp;

    private final boolean enableTimestamp;

    private long stopped;

    public TriBitAttribute ( final String name, final int readIndex, final int readSubIndex, final int writeTrueIndex, final int writeTrueSubIndex, final int writeFalseIndex, final int writeFalseSubIndex, final boolean invertRead, final boolean enableTimestamp )
    {
        super ( name );
        this.readIndex = readIndex;
        this.readSubIndex = readSubIndex;
        this.writeTrueIndex = writeTrueIndex;
        this.writeTrueSubIndex = writeTrueSubIndex;
        this.writeFalseIndex = writeFalseIndex;
        this.writeFalseSubIndex = writeFalseSubIndex;
        this.invertRead = invertRead;
        this.enableTimestamp = enableTimestamp;
    }

    public void handleData ( final IoBuffer data, final Map<String, Variant> attributes )
    {
        final byte b = data.get ( toAddress ( this.readIndex ) );
        final boolean flag = ( b & 1 << this.readSubIndex ) != 0;
        if ( this.invertRead )
        {
            attributes.put ( this.name, flag ? Variant.FALSE : Variant.TRUE );
        }
        else
        {
            attributes.put ( this.name, flag ? Variant.TRUE : Variant.FALSE );
        }

        if ( !Boolean.valueOf ( flag ).equals ( this.lastValue ) )
        {
            this.lastValue = flag;
            this.lastTimestamp = new Variant ( System.currentTimeMillis () );
        }

        if ( this.enableTimestamp )
        {
            attributes.put ( this.name + ".timestamp", this.lastTimestamp );
        }
    }

    @Override
    public void stop ()
    {
        this.stopped = System.currentTimeMillis ();
        super.stop ();
    }

    public void handleError ( final Map<String, Variant> attributes )
    {
        this.lastValue = null;
        this.lastTimestamp = null;
    }

    public void handleWrite ( final Variant value )
    {
        final DaveDevice device = this.device;

        if ( device == null )
        {
            logger.warn ( "Was stopped: {}", this.stopped );
            throw new IllegalStateException ( "Device is not connected" );
        }

        final boolean flag = value.asBoolean ();
        if ( flag )
        {
            device.writeBit ( this.block, this.offset + this.writeTrueIndex, this.writeTrueSubIndex, true );
        }
        else
        {
            device.writeBit ( this.block, this.offset + this.writeFalseIndex, this.writeFalseSubIndex, true );
        }
    }

}
