package org.openscada.da.server.dave;

import org.apache.commons.collections.buffer.CircularFifoBuffer;
import org.apache.mina.core.buffer.IoBuffer;
import org.openscada.core.Variant;
import org.openscada.da.server.common.chain.DataItemInputChained;
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

    private final DataItemFactory blockItemFactory;

    private final DataItemInputChained settingVariablesItem;

    private final Statistics statistics;

    private final long period;

    private final String name;

    private static class Statistics
    {
        private final DataItemInputChained lastUpdateItem;

        private final DataItemInputChained lastTimeDiffItem;

        private long lastUpdate;

        private final CircularFifoBuffer diffBuffer;

        private final DataItemInputChained avgDiffItem;

        public Statistics ( final DataItemFactory itemFactory )
        {
            this.lastUpdateItem = itemFactory.createInput ( "lastUpdate", null );
            this.lastTimeDiffItem = itemFactory.createInput ( "lastDiff", null );
            this.avgDiffItem = itemFactory.createInput ( "avgDiff", null );

            this.lastUpdate = System.currentTimeMillis ();
            this.diffBuffer = new CircularFifoBuffer ( 20 );
        }

        public void dispose ()
        {
        }

        public void receivedUpdate ( final long now )
        {
            final long diff = now - this.lastUpdate;
            this.lastUpdate = now;
            this.lastUpdateItem.updateData ( new Variant ( this.lastUpdate ), null, null );
            this.lastTimeDiffItem.updateData ( new Variant ( diff ), null, null );

            this.diffBuffer.add ( diff );

            update ();
        }

        /**
         * internal update
         */
        private void update ()
        {
            long sum = 0;
            for ( final Object o : this.diffBuffer )
            {
                sum += ( (Number)o ).longValue ();
            }
            final double avgDiff = (double)sum / (double)this.diffBuffer.size ();
            this.avgDiffItem.updateData ( new Variant ( avgDiff ), null, null );
        }
    }

    public DaveRequestBlock ( final String id, final String name, final DaveDevice device, final BundleContext context, final Request request, final boolean enableStatistics, final long period )
    {
        this.request = request;
        this.device = device;
        this.context = context;
        this.id = id;
        this.name = name;
        this.period = period;
        this.blockItemFactory = new DataItemFactory ( context, device.getExecutor (), device.getItemId ( id ) );

        this.settingVariablesItem = this.blockItemFactory.createInput ( "settingVariables", null );

        if ( enableStatistics )
        {
            this.statistics = new Statistics ( this.blockItemFactory );
        }
        else
        {
            this.statistics = null;
        }
    }

    /**
     * The the update priority used to find the next block to request 
     * @param now 
     * @return the update priority
     */
    public long updatePriority ( final long now )
    {
        return now - this.lastUpdate - this.period;
    }

    /**
     * The the configured request
     * @return the request
     */
    public Request getRequest ()
    {
        return this.request;
    }

    /**
     * Handle a device disconnect
     */
    public synchronized void handleDisconnect ()
    {
        for ( final Variable reg : this.variables )
        {
            reg.handleDisconnect ();
        }
    }

    /**
     * Handle a response from the device
     * @param response the response to handle
     */
    public synchronized void handleResponse ( final Result response )
    {
        this.lastUpdate = System.currentTimeMillis ();

        if ( this.statistics != null )
        {
            this.statistics.receivedUpdate ( this.lastUpdate );
        }

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

            if ( this.variables != null )
            {
                for ( final Variable reg : this.variables )
                {
                    try
                    {
                        reg.handleData ( data );
                    }
                    catch ( final Exception e )
                    {
                        logger.warn ( "Failed in block {}", this.id );
                        logger.warn ( "Failed to handle register", e );
                        reg.handleFailure ( e );
                    }
                }
            }
        }

    }

    public synchronized void dispose ()
    {
        if ( this.statistics != null )
        {
            this.statistics.dispose ();
        }

        if ( this.blockItemFactory != null )
        {
            this.blockItemFactory.dispose ();
        }

        if ( this.variables != null )
        {
            for ( final Variable reg : this.variables )
            {
                reg.stop ( this.context );
            }
        }
    }

    /**
     * Set the new variable configuration
     * @param variables the new variables to set
     */
    public synchronized void setVariables ( final Variable[] variables )
    {
        this.settingVariablesItem.updateData ( Variant.TRUE, null, null );

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
                var.start ( this.device.getItemId ( this.name ), this.context, this.device, this, this.request.getStart () );
            }
        }

        this.settingVariablesItem.updateData ( Variant.FALSE, null, null );
    }
}
