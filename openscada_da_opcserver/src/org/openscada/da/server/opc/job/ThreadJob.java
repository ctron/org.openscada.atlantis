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
 * A job which depends on a blocking function run in the current thread.
 * <p>
 * Canceling the job works by issuing {@link Thread#interrupt()} on the thread
 * executing the function.
 * @author Jens Reimann &lt;jens.reimann@inavare.net&gt;
 *
 */
public abstract class ThreadJob extends Job
{
    private static Logger logger = Logger.getLogger ( ThreadJob.class );

    protected Thread runningThread;

    public ThreadJob ( final long timeout )
    {
        super ( timeout );
    }

    @Override
    protected synchronized void interrupt ()
    {
        if ( !this.canceled && this.runningThread != null )
        {
            logger.info ( "Interrupting current job" );
            this.canceled = true;
            this.runningThread.interrupt ();
        }
    }

    @Override
    protected void run () throws Exception
    {
        this.runningThread = Thread.currentThread ();

        try
        {
            perform ();
        }
        catch ( final Throwable e )
        {
            this.error = e;
        }
        finally
        {
            synchronized ( this )
            {
                this.runningThread = null;
            }

            if ( Thread.currentThread ().isInterrupted () )
            {
                // catch up interrupted state 
                try
                {
                    Thread.sleep ( 0 );
                }
                catch ( final InterruptedException e )
                {
                    // we expected that
                }
            }
        }
    }

    protected abstract void perform () throws Exception;
}
