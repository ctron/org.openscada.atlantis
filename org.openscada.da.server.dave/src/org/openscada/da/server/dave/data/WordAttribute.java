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
import org.eclipse.scada.core.Variant;
import org.openscada.da.server.dave.DaveDevice;

/**
 * Implement a single bit attribute
 * @author Jens Reimann
 *
 */
public class WordAttribute extends AbstractAttribute implements Attribute
{
    private final int index;

    private Short lastValue;

    private Variant lastTimestamp;

    private final boolean enableTimestamp;

    public WordAttribute ( final String name, final int index, final boolean enableTimestamp )
    {
        super ( name );
        this.index = index;
        this.enableTimestamp = enableTimestamp;
    }

    public void handleData ( final IoBuffer data, final Map<String, Variant> attributes, final Variant timestamp )
    {
        final short s = data.getShort ( toAddress ( this.index ) );
        attributes.put ( this.name, Variant.valueOf ( s ) );

        if ( !Short.valueOf ( s ).equals ( this.lastValue ) )
        {
            this.lastValue = s;
            this.lastTimestamp = timestamp;
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

        final Integer i = value.asInteger ( null );
        if ( i != null )
        {
            device.writeWord ( this.block, this.offset + this.index, i.shortValue () );
        }
    }

}
