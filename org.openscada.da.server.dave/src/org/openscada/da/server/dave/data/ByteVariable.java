package org.openscada.da.server.dave.data;

import java.util.Map;
import java.util.concurrent.Executor;

import org.apache.mina.core.buffer.IoBuffer;
import org.openscada.core.Variant;
import org.openscada.utils.osgi.pool.ObjectPoolImpl;

public class ByteVariable extends ScalarVariable
{
    public ByteVariable ( final String name, final int index, final Executor executor, final ObjectPoolImpl itemPool, final Attribute... attributes )
    {
        super ( name, index, executor, itemPool, attributes );
    }

    @Override
    protected Variant extractValue ( final IoBuffer data, final Map<String, Variant> attributes )
    {
        return new Variant ( data.get ( toAddress ( this.index ) ) );
    }

}
