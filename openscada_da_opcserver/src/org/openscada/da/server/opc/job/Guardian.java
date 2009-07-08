/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2008 inavare GmbH (http://inavare.com)
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package org.openscada.da.server.opc.job;

import org.apache.log4j.Logger;

/**
 * The guardian takes a job and guards it execution time. If the job takes too long
 * to execute will be interrupted.
 * <p>
 * Note that the guardian can only what one job at a time. Also must the guardian's {@link #run()} method
 * must be started somehow so that it can perform its duties until {@link #shutdown()} is invoked. The most
 * common scenario would be to start the guardian in a separate thread.
 * 
 * @author Jens Reimann &lt;jens.reimann@inavare.net&gt;
 *
 */
public class Guardian implements Runnable
{

    private static Logger log = Logger.getLogger ( Guardian.class );

    private volatile boolean running = true;

    private Job currentJob;

    private GuardianHandler handler;

    private boolean completed = false;

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
                log.warn ( "Failed to wait for job", e );
            }
            finally
            {
                cleanUp ();
            }
        }
        log.info ( "Guardian is shut down" );
    }

    private synchronized void doLoop () throws InterruptedException
    {
        // wait for a job
        this.wait ();
        log.debug ( "Woke up" );

        if ( this.currentJob == null )
        {
            log.info ( "Woke up without a job" );
            return;
        }

        // acknowledge
        this.notifyAll ();

        // and wait again .. until the timeout is arrived or the job is completed

        final long timeout = this.currentJob.getTimeout ();

        final long start = System.currentTimeMillis ();
        log.debug ( String.format ( "Job timeout: %d", timeout ) );

        while ( !this.completed && System.currentTimeMillis () - start < timeout )
        {
            this.wait ( 10 );
        }

        log.debug ( String.format ( "Stopped waiting: completed: %s, diff: %d", this.completed, System.currentTimeMillis () - start ) );

        if ( !this.completed )
        {
            cancel ();
        }

        this.notifyAll ();
    }

    public synchronized void startJob ( final Job job, final GuardianHandler handler )
    {
        if ( !this.running )
        {
            throw new RuntimeException ( "Guardian is already shut down" );
        }

        if ( this.currentJob == null )
        {
            log.debug ( "Starting new job" );
            this.currentJob = job;
            this.handler = handler;
            this.completed = false;

            this.notifyAll ();
        }
        else
        {
            throw new RuntimeException ( "Guardian already running" );
        }
    }

    public synchronized void jobCompleted ()
    {
        log.debug ( "current job completed" );
        if ( this.currentJob != null )
        {
            log.debug ( "mark job as complete" );
            this.completed = true;
            this.notifyAll ();

            // wait until the guardian got our request
            try
            {
                this.wait ();
            }
            catch ( final InterruptedException e )
            {
                log.error ( "Failed to wait for guardian completion" );
            }
        }
        else
        {
            log.debug ( "There is no job active .. maybe guardian already knows that the job is completed since he canceled it" );
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
            log.error ( "Failed to cancel operation", e );
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
        log.debug ( "Signalize that we are up" );
        this.notifyAll ();
    }

    /**
     * triggers the guardian to shutdown. Note that the guardian
     * might still run when the method returns.
     */
    public synchronized void shutdown ()
    {
        log.info ( "Shutting down guardian" );
        this.running = false;
        this.notifyAll ();
    }

}
