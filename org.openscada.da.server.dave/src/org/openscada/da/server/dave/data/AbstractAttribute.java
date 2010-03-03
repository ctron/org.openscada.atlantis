package org.openscada.da.server.dave.data;

import org.openscada.da.server.dave.DaveDevice;
import org.openscada.da.server.dave.DaveRequestBlock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AbstractAttribute
{
    private final static Logger logger = LoggerFactory.getLogger ( AbstractAttribute.class );

    protected final String name;

    protected int offset;

    protected DaveDevice device;

    protected DaveRequestBlock block;

    public AbstractAttribute ( final String name )
    {
        super ();
        this.name = name;
    }

    public String getName ()
    {
        return this.name;
    }

    public void start ( final DaveDevice device, final DaveRequestBlock block, final int offset )
    {
        this.device = device;
        this.block = block;
        this.offset = offset;
    }

    public void stop ()
    {
        logger.info ( "Stopping attribute: {}", this.name );

        this.device = null;
        this.block = null;
    }

    protected int toAddress ( final int localAddress )
    {
        return localAddress + this.offset - this.block.getRequest ().getStart ();
    }

}