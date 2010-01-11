package org.openscada.da.server.dave.data;

import java.util.Map;

import org.apache.mina.core.buffer.IoBuffer;
import org.openscada.core.Variant;
import org.openscada.da.server.dave.DaveDevice;
import org.openscada.da.server.dave.DaveRequestBlock;

/**
 * Implement a single bit attribute
 * @author Jens Reimann
 *
 */
public class BitAttribute implements Attribute
{
    private final String name;

    private final int index;

    private final int subIndex;

    private int offset;

    private DaveDevice device;

    private DaveRequestBlock block;

    public BitAttribute ( final String name, final int index, final int subIndex )
    {
        this.name = name;
        this.index = index;
        this.subIndex = subIndex;
    }

    public void start ( final DaveDevice device, final DaveRequestBlock block, final int offset )
    {
        this.device = device;
        this.block = block;
        this.offset = offset;
    }

    public void stop ()
    {
        this.device = null;
        this.block = null;
    }

    protected int toAddress ( final int localAddress )
    {
        return localAddress + this.offset - this.block.getRequest ().getStart ();
    }

    public void handleData ( final IoBuffer data, final Map<String, Variant> attributes )
    {
        final byte b = data.get ( toAddress ( this.index ) );
        final boolean flag = ( b & 1 << this.subIndex ) != 0;
        attributes.put ( this.name, flag ? Variant.TRUE : Variant.FALSE );
    }

    public void handleError ( final Map<String, Variant> attributes )
    {
    }

    public void handleWrite ( final Variant value )
    {
        this.device.writeBit ( this.block, this.offset + this.index, this.subIndex, value.asBoolean () );
    }

    public String getName ()
    {
        return this.name;
    }

}
