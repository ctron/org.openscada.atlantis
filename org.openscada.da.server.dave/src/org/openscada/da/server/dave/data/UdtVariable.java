package org.openscada.da.server.dave.data;

import org.apache.mina.core.buffer.IoBuffer;
import org.openscada.da.server.dave.DaveDevice;
import org.openscada.da.server.dave.DaveRequestBlock;
import org.osgi.framework.BundleContext;

public class UdtVariable implements Variable
{
    private final String name;

    private final Variable[] variables;

    private final int index;

    public UdtVariable ( final String name, final int index, final Variable... variables )
    {
        this.name = name;
        this.index = index;
        this.variables = variables;
        if ( variables == null )
        {
            throw new NullPointerException ( "'variables' must not be null" );
        }
    }

    public void handleData ( final IoBuffer data )
    {
        for ( final Variable var : this.variables )
        {
            var.handleData ( data );
        }
    }

    public void handleError ( final int errorCode )
    {
        for ( final Variable var : this.variables )
        {
            var.handleError ( errorCode );
        }
    }

    public void handleDisconnect ()
    {
        for ( final Variable var : this.variables )
        {
            var.handleDisconnect ();
        }
    }

    public void handleFailure ( final Throwable e )
    {
        for ( final Variable var : this.variables )
        {
            var.handleFailure ( e );
        }
    }

    public void start ( final String parentName, final BundleContext context, final DaveDevice device, final DaveRequestBlock block, final int offset )
    {
        String itemId;
        if ( parentName != null )
        {
            itemId = parentName + "." + this.name;
        }
        else
        {
            itemId = this.name;
        }

        for ( final Variable var : this.variables )
        {
            var.start ( itemId, context, device, block, offset + this.index );
        }
    }

    public void stop ( final BundleContext context )
    {
        for ( final Variable var : this.variables )
        {
            var.stop ( context );
        }
    }

}
