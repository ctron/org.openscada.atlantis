package org.openscada.da.server.dave;

import org.apache.mina.core.buffer.IoBuffer;
import org.openscada.da.server.dave.data.Variable;
import org.openscada.protocols.dave.DaveReadRequest.Request;
import org.openscada.protocols.dave.DaveReadResult.Result;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DaveRequestBlock
{
    private final static Logger logger = LoggerFactory.getLogger ( DaveRequestBlock.class );

    private final Request request;

    private final DaveDevice device;

    private final BundleContext context;

    private final String id;

    private Variable[] variables;

    private long lastUpdate;

    public DaveRequestBlock ( final String id, final DaveDevice device, final BundleContext context, final Request request )
    {
        this.request = request;
        this.device = device;
        this.context = context;
        this.id = id;
    }

    public long updatePriority ()
    {
        return System.currentTimeMillis () - this.lastUpdate;
    }

    public Request getRequest ()
    {
        return this.request;
    }

    public synchronized void handleResponse ( final Result response )
    {
        this.lastUpdate = System.currentTimeMillis ();

        if ( response.isError () )
        {
            for ( final Variable reg : this.variables )
            {
                reg.handleError ( response.getError () );
            }
        }
        else
        {
            final IoBuffer data = response.getData ();

            for ( final Variable reg : this.variables )
            {
                try
                {
                    reg.handleData ( data );
                }
                catch ( final Exception e )
                {
                    logger.warn ( "Failed to handle register", e );
                    reg.handleFailure ( e );
                }
            }
        }
    }

    public synchronized void dispose ()
    {
        for ( final Variable reg : this.variables )
        {
            reg.stop ( this.context );
        }
    }

    public synchronized void setVariables ( final Variable[] variables )
    {
        // dispose old
        if ( this.variables != null )
        {
            for ( final Variable var : this.variables )
            {
                var.stop ( this.context );
            }
        }

        // set new
        this.variables = variables;
        if ( this.variables != null )
        {
            for ( final Variable var : this.variables )
            {
                var.start ( this.device.getItemId ( this.id ), this.context, this.device, this, this.request.getStart () );
            }
        }
    }
}
