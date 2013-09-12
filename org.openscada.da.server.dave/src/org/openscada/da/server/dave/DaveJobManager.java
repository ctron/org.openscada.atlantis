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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.apache.mina.core.session.IoSession;
import org.eclipse.scada.utils.concurrent.NamedThreadFactory;
import org.openscada.protocols.dave.DaveMessage;
import org.openscada.protocols.dave.DaveReadRequest;
import org.openscada.protocols.dave.DaveReadResult;
import org.openscada.protocols.dave.DaveReadResult.Result;
import org.openscada.protocols.dave.DaveWriteRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DaveJobManager
{
    private final static Logger logger = LoggerFactory.getLogger ( DaveJobManager.class );

    private IoSession session;

    private final Map<String, DaveRequestBlock> blocks = new HashMap<String, DaveRequestBlock> ();

    private final ScheduledExecutorService executor;

    private ScheduledFuture<?> job;

    private Job currentJob;

    private final Queue<Job> writeQueue = new ConcurrentLinkedQueue<Job> ();

    private static interface Job
    {
        public void handleMessage ( final DaveMessage message );

        public void start ( IoSession session );
    }

    private static class ReadJob implements Job
    {
        private final DaveRequestBlock block;

        public ReadJob ( final DaveRequestBlock block )
        {
            this.block = block;
        }

        @Override
        public void handleMessage ( final DaveMessage message )
        {
            logger.debug ( "Result: {}", message );

            if ( message instanceof DaveReadResult )
            {
                for ( final Result result : ( (DaveReadResult)message ).getResult () )
                {
                    this.block.handleResponse ( result );
                }
            }
            else
            {
                logger.warn ( "Got wrong message as reply: {}", message );
                this.block.handleFailure ();
            }
        }

        @Override
        public void start ( final IoSession session )
        {
            logger.debug ( "Start request: " + this.block.getRequest () );

            final DaveReadRequest request = new DaveReadRequest ();
            request.addRequest ( this.block.getRequest () );
            session.write ( request );
        }

    }

    private static class WriteJob implements Job
    {
        private final DaveWriteRequest request;

        public WriteJob ( final DaveWriteRequest request )
        {
            this.request = request;
        }

        @Override
        public void start ( final IoSession session )
        {
            session.write ( this.request );
        }

        @Override
        public void handleMessage ( final DaveMessage message )
        {
            // TODO: no-op for now
        }
    }

    public DaveJobManager ( final DaveDevice device )
    {
        this.executor = Executors.newSingleThreadScheduledExecutor ( new NamedThreadFactory ( "DaveJobManager/" + device.getId () ) );
    }

    public synchronized void setSession ( final IoSession session )
    {
        logger.debug ( "Setting session: {}", session );

        this.session = session;
        setTimerState ( session != null );
        if ( session == null )
        {
            this.currentJob = null;
            // discard write requests
            this.writeQueue.clear ();
            // handle data disconnect
            handleDataDisconnected ();
        }
    }

    private void setTimerState ( final boolean flag )
    {
        final boolean currentState = this.job != null;

        if ( currentState == flag )
        {
            logger.info ( "Timer is in correct state: {} / {}", new Object[] { currentState, flag } );
            return;
        }

        if ( flag )
        {
            logger.info ( "Starting timer" );
            this.job = this.executor.scheduleWithFixedDelay ( new Runnable () {

                @Override
                public void run ()
                {
                    DaveJobManager.this.tick ();
                }
            }, 0, 1000, TimeUnit.MILLISECONDS );
        }
        else
        {
            logger.info ( "Stopping timer" );
            this.job.cancel ( false );
            this.job = null;
        }
    }

    public synchronized void messageReceived ( final DaveMessage message )
    {
        if ( this.currentJob != null )
        {
            try
            {
                this.currentJob.handleMessage ( message );
            }
            finally
            {
                this.currentJob = null;
                startNextJob ();
            }
        }
        else
        {
            logger.warn ( "Message without a job: {}", message );
        }
    }

    protected synchronized void tick ()
    {
        if ( this.currentJob != null )
        {
            logger.debug ( "Ticked with current job" );
            return;
        }

        logger.info ( "No job active when ticking... adding job!" );
        startNextJob ();
        logger.info ( "New job: {}", this.currentJob );
    }

    private void startNextJob ()
    {
        this.currentJob = getNextWriteJob ();
        if ( this.currentJob == null )
        {
            this.currentJob = getNextReadJob ();
        }

        logger.debug ( "Next job: {}", this.currentJob );

        if ( this.currentJob != null )
        {
            this.currentJob.start ( this.session );
        }
    }

    /**
     * Get the next read job
     * @return the next read job or <code>null</code> if no blocks need to be refreshed
     */
    private Job getNextReadJob ()
    {
        final List<DaveRequestBlock> blocks = new ArrayList<DaveRequestBlock> ( this.blocks.values () );

        final long now = System.currentTimeMillis ();

        Collections.sort ( blocks, new Comparator<DaveRequestBlock> () {

            @Override
            public int compare ( final DaveRequestBlock o1, final DaveRequestBlock o2 )
            {
                final long l1 = o1.updatePriority ( now );
                final long l2 = o2.updatePriority ( now );
                return Long.valueOf ( l2 ).compareTo ( Long.valueOf ( l1 ) );
            }
        } );

        if ( !blocks.isEmpty () )
        {
            return new ReadJob ( blocks.get ( 0 ) );
        }
        else
        {
            return null;
        }
    }

    /**
     * Get the next job from the write queue if there is any
     * @return the next write job or <code>null</code> if there is none
     */
    private Job getNextWriteJob ()
    {
        return this.writeQueue.poll ();
    }

    public void dispose ()
    {
        synchronized ( this )
        {
            for ( final DaveRequestBlock block : this.blocks.values () )
            {
                block.dispose ();
            }

            if ( this.job != null )
            {
                this.job.cancel ( false );
            }
        }

        this.executor.shutdown ();
    }

    protected void handleDataDisconnected ()
    {
        for ( final DaveRequestBlock block : this.blocks.values () )
        {
            block.handleDisconnect ();
        }
    }

    public synchronized void addBlock ( final String id, final DaveRequestBlock block )
    {
        logger.debug ( "Adding block: {}", id );

        if ( this.blocks.containsKey ( id ) )
        {
            throw new IllegalArgumentException ( String.format ( "Block '%s' is already registered with device", id ) );
        }

        this.blocks.put ( id, block );
    }

    public synchronized void removeBlock ( final String id )
    {
        logger.debug ( "Removing block: {}", id );

        final DaveRequestBlock oldBlock = this.blocks.remove ( id );
        if ( oldBlock != null )
        {
            oldBlock.dispose ();
        }
    }

    public void addWriteRequest ( final DaveWriteRequest request )
    {
        this.writeQueue.add ( new WriteJob ( request ) );
    }

}
