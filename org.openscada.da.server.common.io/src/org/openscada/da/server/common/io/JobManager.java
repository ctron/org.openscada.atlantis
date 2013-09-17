/*******************************************************************************
 * Copyright (c) 2013 IBH SYSTEMS GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBH SYSTEMS GmbH - initial API and implementation
 *******************************************************************************/
package org.openscada.da.server.common.io;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JobManager
{
    private final static Logger logger = LoggerFactory.getLogger ( JobManager.class );

    private IoSession session;

    private final Map<String, PollRequest> blocks = new HashMap<String, PollRequest> ();

    private final ScheduledExecutorService executor;

    private ScheduledFuture<?> job;

    private Job currentJob;

    private final Queue<Job> writeQueue = new ConcurrentLinkedQueue<Job> ();

    private static interface Job
    {
        public void handleMessage ( final Object message );

        public void start ( IoSession session );
    }

    private static class ReadJob implements Job
    {
        private final PollRequest block;

        public ReadJob ( final PollRequest block )
        {
            this.block = block;
        }

        @Override
        public void handleMessage ( final Object message )
        {
            logger.debug ( "Result: {}", message );

            if ( !this.block.handleMessage ( message ) )
            {
                logger.warn ( "Got wrong message as reply: {}", message );
                this.block.handleFailure ();
            }
        }

        @Override
        public void start ( final IoSession session )
        {
            final Object request = this.block.createPollRequest ();
            logger.debug ( "Start request: {}", request );
            session.write ( request );
        }
    }

    private static class WriteJob implements Job
    {
        private final Object request;

        public WriteJob ( final Object request )
        {
            this.request = request;
        }

        @Override
        public void start ( final IoSession session )
        {
            session.write ( this.request );
        }

        @Override
        public void handleMessage ( final Object message )
        {
            // TODO: no-op for now
        }
    }

    public JobManager ( final String threadName )
    {
        this.executor = Executors.newSingleThreadScheduledExecutor ( new NamedThreadFactory ( threadName ) );
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
                    JobManager.this.tick ();
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

    public synchronized void messageReceived ( final Object message )
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
     * 
     * @return the next read job or <code>null</code> if no blocks need to be
     *         refreshed
     */
    private Job getNextReadJob ()
    {
        final List<PollRequest> blocks = new ArrayList<PollRequest> ( this.blocks.values () );

        final long now = System.currentTimeMillis ();

        Collections.sort ( blocks, new Comparator<PollRequest> () {

            @Override
            public int compare ( final PollRequest o1, final PollRequest o2 )
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
     * 
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
            for ( final PollRequest block : this.blocks.values () )
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
        for ( final PollRequest block : this.blocks.values () )
        {
            block.handleDisconnect ();
        }
    }

    public synchronized void addBlock ( final String id, final PollRequest block )
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

        final PollRequest oldBlock = this.blocks.remove ( id );
        if ( oldBlock != null )
        {
            oldBlock.dispose ();
        }
    }

    public void addWriteRequest ( final Object request )
    {
        this.writeQueue.add ( new WriteJob ( request ) );
    }

}
