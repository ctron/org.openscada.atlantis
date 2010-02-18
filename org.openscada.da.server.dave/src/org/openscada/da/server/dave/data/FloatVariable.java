package org.openscada.da.server.dave.data;

import java.util.Map;
import java.util.concurrent.Executor;

import org.apache.mina.core.buffer.IoBuffer;
import org.openscada.core.Variant;
import org.openscada.da.core.WriteResult;
import org.openscada.utils.concurrent.InstantErrorFuture;
import org.openscada.utils.concurrent.InstantFuture;
import org.openscada.utils.concurrent.NotifyFuture;
import org.openscada.utils.osgi.pool.ObjectPoolImpl;

public class FloatVariable extends ScalarVariable
{
    public FloatVariable ( final String name, final int index, final Executor executor, final ObjectPoolImpl itemPool, final Attribute... attributes )
    {
        super ( name, index, executor, itemPool, attributes );
    }

    @Override
    protected NotifyFuture<WriteResult> handleWrite ( final Variant value )
    {
        final Double d = value.asDouble ( null );
        if ( d != null )
        {
            this.device.writeFloat ( this.block, toGlobalAddress ( this.index ), d.floatValue () );
            return new InstantFuture<WriteResult> ( new WriteResult () );
        }
        else
        {
            return new InstantErrorFuture<WriteResult> ( new IllegalArgumentException ( String.format ( "Can only write doubles: %s is not a double", value ) ) );
        }
    }

    @Override
    protected Variant extractValue ( final IoBuffer data, final Map<String, Variant> attributes )
    {
        return new Variant ( data.getFloat ( toAddress ( this.index ) ) );
    }
}
