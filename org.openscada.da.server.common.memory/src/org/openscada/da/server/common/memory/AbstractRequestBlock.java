package org.openscada.da.server.common.memory;

import java.util.concurrent.Executor;

import org.apache.commons.collections.buffer.CircularFifoBuffer;
import org.apache.mina.core.buffer.IoBuffer;
import org.eclipse.scada.core.Variant;
import org.openscada.da.server.common.chain.DataItemInputChained;
import org.openscada.da.server.common.io.PollRequest;
import org.openscada.da.server.common.osgi.factory.DataItemFactory;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractRequestBlock implements PollRequest, MemoryRequestBlock
{

    private final static Logger logger = LoggerFactory.getLogger ( AbstractRequestBlock.class );

    protected static class Statistics
    {
        private final DataItemInputChained lastUpdateItem;

        private final DataItemInputChained lastTimeDiffItem;

        private long lastUpdate;

        private final CircularFifoBuffer diffBuffer;

        private final DataItemInputChained avgDiffItem;

        private final DataItemInputChained stateItem;

        private final DataItemInputChained sizeItem;

        private final DataItemInputChained timeoutStateItem;

        public Statistics ( final DataItemFactory itemFactory, final int size )
        {
            this.stateItem = itemFactory.createInput ( "state", null );
            this.timeoutStateItem = itemFactory.createInput ( "timeout", null );
            this.lastUpdateItem = itemFactory.createInput ( "lastUpdate", null );
            this.lastTimeDiffItem = itemFactory.createInput ( "lastDiff", null );
            this.avgDiffItem = itemFactory.createInput ( "avgDiff", null );

            this.sizeItem = itemFactory.createInput ( "size", null );
            this.sizeItem.updateData ( Variant.valueOf ( size ), null, null );

            this.lastUpdate = System.currentTimeMillis ();
            this.diffBuffer = new CircularFifoBuffer ( 20 );
        }

        public void dispose ()
        {
        }

        public void receivedError ( final long now )
        {
            tickNow ( now );
            this.stateItem.updateData ( Variant.FALSE, null, null );
            this.timeoutStateItem.updateData ( Variant.FALSE, null, null );
        }

        public void receivedUpdate ( final long now )
        {
            tickNow ( now );
            this.stateItem.updateData ( Variant.TRUE, null, null );
            this.timeoutStateItem.updateData ( Variant.FALSE, null, null );
        }

        public void timeout ()
        {
            this.stateItem.updateData ( Variant.FALSE, null, null );
            this.timeoutStateItem.updateData ( Variant.TRUE, null, null );
        }

        private void tickNow ( final long now )
        {
            final long diff = now - this.lastUpdate;
            this.lastUpdate = now;
            this.lastUpdateItem.updateData ( Variant.valueOf ( this.lastUpdate ), null, null );
            this.lastTimeDiffItem.updateData ( Variant.valueOf ( diff ), null, null );

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
            this.avgDiffItem.updateData ( Variant.valueOf ( avgDiff ), null, null );
        }
    }

    private final Statistics statistics;

    private final BundleContext context;

    private final long period;

    private final DataItemFactory blockItemFactory;

    private final DataItemInputChained settingVariablesItem;

    private Variable[] variables;

    private long lastAction;

    private boolean disposed;

    private final String variablePrefix;

    private final String blockPrefix;

    private final RequestBlockConfigurator configurator;

    private final long timeoutQuietPeriod;

    private boolean timeout;

    public AbstractRequestBlock ( final BundleContext context, final Executor executor, final String mainTypeName, final String variablePrefix, final String blockPrefix, final boolean enableStatistics, final long period, final int requestSize, final long timeoutQuietPeriod )
    {
        this.context = context;
        this.variablePrefix = variablePrefix;
        this.blockPrefix = blockPrefix;
        this.timeoutQuietPeriod = timeoutQuietPeriod;

        this.period = period;

        this.blockItemFactory = new DataItemFactory ( context, executor, blockPrefix );

        this.settingVariablesItem = this.blockItemFactory.createInput ( "settingVariables", null );

        if ( enableStatistics )
        {
            this.statistics = new Statistics ( this.blockItemFactory, requestSize );
        }
        else
        {
            this.statistics = null;
        }

        this.configurator = new RequestBlockConfigurator ( this, mainTypeName );
    }

    /**
     * The the update priority used to find the next block to request
     * 
     * @param now
     * @return the update priority, or <code>null</code> if the block does not
     *         want to be
     *         updated right now
     */
    @Override
    public Long updatePriority ( final long now )
    {
        if ( this.timeout )
        {
            return now - this.lastAction - ( this.period + this.timeoutQuietPeriod );
        }
        else
        {
            return now - this.lastAction - this.period;
        }
    }

    /**
     * Handle a device disconnect
     */
    @Override
    public synchronized void handleDisconnect ()
    {
        if ( this.disposed )
        {
            return;
        }

        if ( this.variables != null )
        {
            for ( final Variable reg : this.variables )
            {
                reg.handleDisconnect ();
            }
        }
    }

    @Override
    public void handleTimeout ()
    {
        if ( this.disposed )
        {
            return;
        }

        this.lastAction = System.currentTimeMillis ();
        this.timeout = true;
        this.statistics.timeout ();

        if ( this.variables != null )
        {
            for ( final Variable reg : this.variables )
            {
                reg.handleFailure ( new RuntimeException ( "Timeout" ) );
            }
        }
    }

    @Override
    public synchronized void handleFailure ()
    {
        if ( this.disposed )
        {
            return;
        }

        this.lastAction = System.currentTimeMillis ();

        recordUpdate ( true );

        if ( this.variables != null )
        {
            for ( final Variable reg : this.variables )
            {
                reg.handleFailure ( new RuntimeException ( "Wrong reply" ) );
            }
        }
    }

    @Override
    public synchronized void dispose ()
    {
        if ( this.disposed )
        {
            return;
        }

        logger.info ( "Disposing: {}", this );
        this.disposed = true;

        if ( this.configurator != null )
        {
            this.configurator.dispose ();
        }

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
     * 
     * @param variables
     *            the new variables to set
     */
    public synchronized void setVariables ( final Variable[] variables )
    {
        if ( this.disposed )
        {
            return;
        }

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
                var.start ( this.variablePrefix, this.context, this, getStartAddress () );
            }
        }

        this.settingVariablesItem.updateData ( Variant.FALSE, null, null );
    }

    protected void recordUpdate ( final boolean error )
    {
        this.timeout = false;
        this.lastAction = System.currentTimeMillis ();

        if ( this.statistics != null )
        {
            if ( error )
            {
                this.statistics.receivedError ( this.lastAction );
            }
            else
            {
                this.statistics.receivedUpdate ( this.lastAction );
            }
        }
    }

    public synchronized void handleError ( final int error )
    {
        if ( this.disposed )
        {
            return;
        }

        recordUpdate ( true );

        logger.debug ( "Handle error update - variables: {}", new Object[] { this.variables } );

        if ( this.variables != null )
        {
            for ( final Variable reg : this.variables )
            {
                reg.handleError ( error );
            }
        }

    }

    public synchronized void handleData ( final IoBuffer data )
    {
        if ( this.disposed )
        {
            logger.trace ( "Block is disposed" );
            return;
        }

        recordUpdate ( false );

        logger.debug ( "Handle data update - variables: {}", new Object[] { this.variables } );

        if ( this.variables != null )
        {
            final Variant timestamp = Variant.valueOf ( System.currentTimeMillis () );
            for ( final Variable reg : this.variables )
            {
                try
                {
                    reg.handleData ( data, timestamp );
                }
                catch ( final Exception e )
                {
                    logger.warn ( "Failed in block {}", this.blockPrefix );
                    logger.warn ( "Failed to handle register", e );
                    reg.handleFailure ( e );
                }
            }
        }
    }

}
