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
public class BitAttribute extends AbstractAttribute implements Attribute
{

    private final int index;

    private final int subIndex;

    private Boolean lastValue;

    private Variant lastTimestamp;

    private final boolean enableTimestamp;

    public BitAttribute ( final String name, final int index, final int subIndex, final boolean enableTimestamp )
    {
        super ( name );
        this.index = index;
        this.subIndex = subIndex;
        this.enableTimestamp = enableTimestamp;
    }

    public void handleData ( final IoBuffer data, final Map<String, Variant> attributes )
    {
        final byte b = data.get ( toAddress ( this.index ) );
        final boolean flag = ( b & 1 << this.subIndex ) != 0;
        attributes.put ( this.name, Variant.valueOf ( flag ) );

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
        final DaveDevice device = this.device;

        if ( device == null )
        {
            throw new IllegalStateException ( "Device is not connected" );
        }

        device.writeBit ( this.block, this.offset + this.index, this.subIndex, value.asBoolean () );
    }

}
