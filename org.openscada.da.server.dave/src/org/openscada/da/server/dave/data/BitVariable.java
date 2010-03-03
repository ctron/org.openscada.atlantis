package org.openscada.da.server.dave.data;

import java.util.Map;
import java.util.concurrent.Executor;

import org.apache.mina.core.buffer.IoBuffer;
import org.openscada.core.OperationException;
import org.openscada.core.Variant;
import org.openscada.da.core.WriteResult;
import org.openscada.da.server.dave.DaveDevice;
import org.openscada.utils.concurrent.InstantErrorFuture;
import org.openscada.utils.concurrent.InstantFuture;
import org.openscada.utils.concurrent.NotifyFuture;
import org.openscada.utils.osgi.pool.ObjectPoolImpl;

public class BitVariable extends ScalarVariable
{
    private final int subIndex;

    public BitVariable ( final String name, final int index, final int subIndex, final Executor executor, final ObjectPoolImpl itemPool, final Attribute... attributes )
    {
        super ( name, index, executor, itemPool, attributes );
        this.subIndex = subIndex;
    }

    @Override
    protected Variant extractValue ( final IoBuffer data, final Map<String, Variant> attributes )
    {
        final byte b = data.get ( toAddress ( this.index ) );
        final boolean flag = ( b & 1 << this.subIndex ) != 0;
        return Variant.valueOf ( flag );
    }

    @Override
    protected NotifyFuture<WriteResult> handleWrite ( final Variant value )
    {
        final DaveDevice dave = this.device;

        if ( dave == null )
        {
            return new InstantErrorFuture<WriteResult> ( new OperationException ( "Device not connected" ).fillInStackTrace () );
        }

        this.device.writeBit ( this.block, toGlobalAddress ( this.index ), this.subIndex, value.asBoolean () );

        return new InstantFuture<WriteResult> ( new WriteResult () );
    }
}
