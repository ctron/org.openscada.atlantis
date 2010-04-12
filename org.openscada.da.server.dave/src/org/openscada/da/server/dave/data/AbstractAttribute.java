package org.openscada.da.server.dave.data;

import org.openscada.da.server.dave.DaveDevice;
import org.openscada.da.server.dave.DaveRequestBlock;
import org.openscada.protocols.dave.DaveReadRequest.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AbstractAttribute
{
    private final static Logger logger = LoggerFactory.getLogger ( AbstractAttribute.class );

    protected final String name;

    protected int offset;

    protected DaveDevice device;

    protected DaveRequestBlock block;

    private boolean stopped;

    public AbstractAttribute ( final String name )
    {
        super ();
        this.name = name;
        this.stopped = true;
    }

    public String getName ()
    {
        return this.name;
    }

    public void start ( final DaveDevice device, final DaveRequestBlock block, final int offset )
    {
        logger.debug ( "Starting attribute: {}", this.name );
        this.stopped = false;

        assert device != null;
        assert block != null;

        this.device = device;
        this.block = block;
        this.offset = offset;
    }

    public void stop ()
    {
        logger.debug ( "Stopping attribute: {}", this.name );

        this.stopped = true;

        this.device = null;
        this.block = null;
    }

    protected int toAddress ( final int localAddress )
    {
        if ( this.stopped )
        {
            logger.error ( "isStopped" );
        }

        final Request request = this.block.getRequest ();

        return localAddress + this.offset - this.block.getRequest ().getStart ();
    }

}