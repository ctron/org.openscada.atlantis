/*******************************************************************************
 * Copyright (c) 2013 IBH SYSTEMS GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBH SYSTEMS GmbH - initial API and implementation
 *******************************************************************************/
package org.openscada.da.server.dave;

import org.openscada.da.server.common.memory.MemoryDevice;
import org.openscada.da.server.common.memory.MemoryRequestBlock;

public class MemoryRequestBlockImpl implements MemoryRequestBlock
{

    private final DaveDevice device;

    private final DaveRequestBlock block;

    private final MemoryDevice memoryDevice;

    private final int startAddress;

    public MemoryRequestBlockImpl ( final DaveDevice device, final DaveRequestBlock block )
    {
        this.device = device;
        this.block = block;

        this.startAddress = block.getRequest ().getStart ();

        this.memoryDevice = new MemoryDevice () {

            @Override
            public void writeBit ( final int globalAddress, final int subIndex, final boolean value )
            {
                MemoryRequestBlockImpl.this.device.writeBit ( MemoryRequestBlockImpl.this.block, globalAddress, subIndex, value );
            }

            @Override
            public void writeFloat ( final int globalAddress, final float value )
            {
                device.writeFloat ( block, globalAddress, value );
            }

            @Override
            public void writeDoubleInteger ( final int globalAddress, final int value )
            {
                device.writeDoubleInteger ( block, globalAddress, value );
            }

            @Override
            public void writeWord ( final int globalAddress, final short value )
            {
                device.writeWord ( block, globalAddress, value );
            }

            @Override
            public void writeByte ( final int globalAddress, final byte value )
            {
                device.writeByte ( block, globalAddress, value );
            }
        };
    }

    @Override
    public int getStartAddress ()
    {
        return this.startAddress;
    }

    @Override
    public MemoryDevice getDevice ()
    {
        return this.memoryDevice;
    }

}
