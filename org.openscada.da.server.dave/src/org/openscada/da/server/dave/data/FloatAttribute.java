/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2010 TH4 SYSTEMS GmbH (http://inavare.com)
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

/**
 * Implement a single bit attribute
 * @author Jens Reimann
 *
 */
public class FloatAttribute extends AbstractAttribute implements Attribute
{
    private final int index;

    private Float lastValue;

    private Variant lastTimestamp;

    private final boolean enableTimestamp;

    public FloatAttribute ( final String name, final int index, final boolean enableTimestamp )
    {
        super ( name );
        this.index = index;
        this.enableTimestamp = enableTimestamp;
    }

    public void handleData ( final IoBuffer data, final Map<String, Variant> attributes )
    {
        final float f = data.getFloat ( toAddress ( this.index ) );
        attributes.put ( this.name, new Variant ( f ) );

        if ( !Float.valueOf ( f ).equals ( this.lastValue ) )
        {
            this.lastValue = f;
            this.lastTimestamp = new Variant ( System.currentTimeMillis () );
        }

        if ( this.enableTimestamp )
        {
            attributes.put ( this.name + ".timestamp", this.lastTimestamp );
        }
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
            throw new IllegalStateException ( "Device is not connected" );
        }

        final Double d = value.asDouble ( null );
        if ( d != null )
        {
            device.writeFloat ( this.block, this.offset + this.index, d.floatValue () );
        }
    }

}
