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
public class TriBitAttribute implements Attribute
{
    private final String name;

    private int offset;

    private DaveDevice device;

    private DaveRequestBlock block;

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

    public TriBitAttribute ( final String name, final int readIndex, final int readSubIndex, final int writeTrueIndex, final int writeTrueSubIndex, final int writeFalseIndex, final int writeFalseSubIndex, final boolean invertRead, final boolean enableTimestamp )
    {
        this.name = name;
        this.readIndex = readIndex;
        this.readSubIndex = readSubIndex;
        this.writeTrueIndex = writeTrueIndex;
        this.writeTrueSubIndex = writeTrueSubIndex;
        this.writeFalseIndex = writeFalseIndex;
        this.writeFalseSubIndex = writeFalseSubIndex;
        this.invertRead = invertRead;
        this.enableTimestamp = enableTimestamp;
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

    public void handleError ( final Map<String, Variant> attributes )
    {
        this.lastValue = null;
        this.lastTimestamp = null;
    }

    public void handleWrite ( final Variant value )
    {
        final boolean flag = value.asBoolean ();
        if ( flag )
        {
            this.device.writeBit ( this.block, this.offset + this.writeTrueIndex, this.writeTrueSubIndex, true );
        }
        else
        {
            this.device.writeBit ( this.block, this.offset + this.writeFalseIndex, this.writeFalseSubIndex, true );
        }
    }

    public String getName ()
    {
        return this.name;
    }

}
