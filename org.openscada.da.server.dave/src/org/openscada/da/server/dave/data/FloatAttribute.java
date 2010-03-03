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
public class FloatAttribute implements Attribute
{
    private final String name;

    private final int index;

    private int offset;

    private DaveDevice device;

    private DaveRequestBlock block;

    private Float lastValue;

    private Variant lastTimestamp;

    public FloatAttribute ( final String name, final int index )
    {
        this.name = name;
        this.index = index;
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
        final float f = data.getFloat ( toAddress ( this.index ) );
        attributes.put ( this.name, new Variant ( f ) );

        if ( !Float.valueOf ( f ).equals ( this.lastValue ) )
        {
            this.lastValue = f;
            this.lastTimestamp = new Variant ( System.currentTimeMillis () );
        }

        attributes.put ( this.name + ".timestamp", this.lastTimestamp );
    }

    public void handleError ( final Map<String, Variant> attributes )
    {
        this.lastValue = null;
        this.lastTimestamp = null;
    }

    public void handleWrite ( final Variant value )
    {
        final Double d = value.asDouble ( null );
        if ( d != null )
        {
            this.device.writeFloat ( this.block, this.offset + this.index, d.floatValue () );
        }
    }

    public String getName ()
    {
        return this.name;
    }

}
