/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2010 TH4 SYSTEMS GmbH (http://th4-systems.com)
 *
 * OpenSCADA is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 3
 * only, as published by the Free Software Foundation.
 *
 * OpenSCADA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License version 3 for more details
 * (a copy is included in the LICENSE file that accompanied this code).
 *
 * You should have received a copy of the GNU Lesser General Public License
 * version 3 along with OpenSCADA. If not, see
 * <http://opensource.org/licenses/lgpl-3.0.html> for a copy of the LGPLv3 License.
 */

package org.openscada.da.server.dave;

import org.apache.commons.collections.buffer.CircularFifoBuffer;
import org.apache.mina.core.buffer.IoBuffer;
import org.openscada.core.Variant;
import org.openscada.da.server.common.chain.DataItemInputChained;
import org.openscada.da.server.common.osgi.factory.DataItemFactory;
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

    private boolean disposed;

    private static class Statistics
    {
        private final DataItemInputChained lastUpdateItem;

        private final DataItemInputChained lastTimeDiffItem;

        private long lastUpdate;

        private final CircularFifoBuffer diffBuffer;

        private final DataItemInputChained avgDiffItem;

        private final DataItemInputChained stateItem;

        private final DataItemInputChained sizeItem;

        public Statistics ( final DataItemFactory itemFactory, final Request request )
        {
            this.stateItem = itemFactory.createInput ( "state", null );
            this.lastUpdateItem = itemFactory.createInput ( "lastUpdate", null );
            this.lastTimeDiffItem = itemFactory.createInput ( "lastDiff", null );
            this.avgDiffItem = itemFactory.createInput ( "avgDiff", null );

            this.sizeItem = itemFactory.createInput ( "size", null );
            this.sizeItem.updateData ( new Variant ( request.getCount () ), null, null );

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
        }

        public void receivedUpdate ( final long now )
        {
            tickNow ( now );
            this.stateItem.updateData ( Variant.TRUE, null, null );
        }

        private void tickNow ( final long now )
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
            this.statistics = new Statistics ( this.blockItemFactory, request );
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

    public synchronized void handleFailure ()
    {
        if ( this.disposed )
        {
            return;
        }

        this.lastUpdate = System.currentTimeMillis ();

        if ( this.statistics != null )
        {
            this.statistics.receivedError ( this.lastUpdate );
        }

        if ( this.variables != null )
        {
            for ( final Variable reg : this.variables )
            {
                reg.handleFailure ( new RuntimeException ( "Wrong reply" ).fillInStackTrace () );
            }
        }
    }

    /**
     * Handle a response from the device
     * @param response the response to handle
     */
    public synchronized void handleResponse ( final Result response )
    {
        if ( this.disposed )
        {
            return;
        }

        this.lastUpdate = System.currentTimeMillis ();

        if ( this.statistics != null )
        {
            this.statistics.receivedUpdate ( this.lastUpdate );
        }

        if ( response.isError () )
        {
            if ( this.variables != null )
            {
                for ( final Variable reg : this.variables )
                {
                    reg.handleError ( response.getError () );
                }
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
        if ( this.disposed )
        {
            return;
        }

        logger.info ( "Disposing: {}", this );
        this.disposed = true;

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
                var.start ( this.device.getVarItemId ( this.name ), this.context, this.device, this, this.request.getStart () );
            }
        }

        this.settingVariablesItem.updateData ( Variant.FALSE, null, null );
    }

    @Override
    public String toString ()
    {
        return String.format ( "[Request - %s]", this.request );
    }

}
