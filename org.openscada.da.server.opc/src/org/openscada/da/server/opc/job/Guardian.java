/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2011 TH4 SYSTEMS GmbH (http://th4-systems.com)
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openscada.da.server.opc.job;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The guardian takes a job and guards it execution time. If the job takes too long
 * to execute will be interrupted.
 * <p>
 * Note that the guardian can only what one job at a time. Also must the guardian's {@link #run()} method
 * must be started somehow so that it can perform its duties until {@link #shutdown()} is invoked. The most
 * common scenario would be to start the guardian in a separate thread.
 * 
 * @author Jens Reimann &lt;jens.reimann@th4-systems.com&gt;
 *
 */
public class Guardian implements Runnable
{

    private final static Logger logger = LoggerFactory.getLogger ( Guardian.class );

    private volatile boolean running = true;

    private Job currentJob;

    private GuardianHandler handler;

    private boolean completed = false;

    @Override
    public synchronized void run ()
    {
        signalInitialized ();

        while ( this.running )
        {
            try
            {
                doLoop ();
            }
            catch ( final Throwable e )
            {
                logger.warn ( "Failed to wait for job", e );
            }
            finally
            {
                cleanUp ();
            }
        }
        logger.info ( "Guardian is shut down" );
    }

    private synchronized void doLoop () throws InterruptedException
    {
        // wait for a job
        this.wait ();
        logger.debug ( "Woke up" );

        if ( this.currentJob == null )
        {
            logger.info ( "Woke up without a job" );
            return;
        }

        // acknowledge
        notifyAll ();

        // and wait again .. until the timeout is arrived or the job is completed

        final long timeout = this.currentJob.getTimeout ();

        final long start = System.currentTimeMillis ();
        logger.debug ( "Job timeout: {}", timeout );

        while ( !this.completed && System.currentTimeMillis () - start < timeout )
        {
            this.wait ( 10 );
        }

        logger.debug ( "Stopped waiting: completed: {}, diff: {}", this.completed, System.currentTimeMillis () - start );

        if ( !this.completed )
        {
            cancel ();
        }

        notifyAll ();
    }

    public synchronized void startJob ( final Job job, final GuardianHandler handler )
    {
        if ( !this.running )
        {
            throw new RuntimeException ( "Guardian is already shut down" );
        }

        if ( this.currentJob == null )
        {
            logger.debug ( "Starting new job" );
            this.currentJob = job;
            this.handler = handler;
            this.completed = false;

            notifyAll ();
        }
        else
        {
            throw new RuntimeException ( "Guardian already running" );
        }
    }

    public synchronized void jobCompleted ()
    {
        logger.debug ( "current job completed" );
        if ( this.currentJob != null )
        {
            logger.debug ( "mark job as complete" );
            this.completed = true;
            notifyAll ();

            // wait until the guardian got our request
            try
            {
                this.wait ();
            }
            catch ( final InterruptedException e )
            {
                logger.error ( "Failed to wait for guardian completion" );
            }
        }
        else
        {
            logger.debug ( "There is no job active .. maybe guardian already knows that the job is completed since he canceled it" );
        }
    }

    private void cancel ()
    {
        try
        {
            this.handler.performCancel ();
        }
        catch ( final Throwable e )
        {
            logger.error ( "Failed to cancel operation", e );
        }
    }

    private void cleanUp ()
    {
        this.currentJob = null;
        this.handler = null;
    }

    /**
     * Notify the starter that the guardian is initialized
     */
    private void signalInitialized ()
    {
        logger.debug ( "Signalize that we are up" );
        notifyAll ();
    }

    /**
     * triggers the guardian to shutdown. Note that the guardian
     * might still run when the method returns.
     */
    public synchronized void shutdown ()
    {
        logger.info ( "Shutting down guardian" );
        this.running = false;
        notifyAll ();
    }

}
