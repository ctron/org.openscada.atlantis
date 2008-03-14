/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2008 inavare GmbH (http://inavare.com)
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package org.openscada.da.server.opc2.job;

import java.lang.reflect.InvocationTargetException;

import org.apache.log4j.Logger;

/**
 * The worker take control of a work unit and executes it including guarding it runtime.
 * @author Jens Reimann &lt;jens.reimann@inavare.net&gt;
 *
 */
public class Worker implements GuardianHandler
{
    private static Logger log = Logger.getLogger ( Worker.class );

    private WorkUnit currentWorkUnit;

    private Guardian guardian;
    private Thread guardianThread;

    protected static class OPCResultJobHandler<T> implements JobHandler
    {
        private Throwable error;
        private T result;
        private JobResult<T> jobResult;

        public OPCResultJobHandler ( JobResult<T> jobResult )
        {
            this.jobResult = jobResult;
        }

        public void handleFailure ( Throwable e )
        {
            error = e;
        }

        public void handleInterrupted ()
        {
            this.error = new InterruptedException ( "Job got interrupted" );
            this.error.fillInStackTrace ();
        }

        public void handleSuccess ()
        {
            result = jobResult.getResult ();
        }

        public Throwable getError ()
        {
            return error;
        }

        public T getResult ()
        {
            return result;
        }

    }

    protected static class OPCRunnableJobHandler implements JobHandler
    {

        private Throwable error;
        private Runnable runnable;

        public OPCRunnableJobHandler ( Runnable runnable )
        {
            this.runnable = runnable;
        }

        public void handleFailure ( Throwable e )
        {
            this.error = e;
        }

        public void handleInterrupted ()
        {
            this.error = new InterruptedException ( "Job got interrupted" );
            this.error.fillInStackTrace ();
        }

        public void handleSuccess ()
        {
            this.runnable.run ();
        }

        public Throwable getError ()
        {
            return error;
        }

    }

    public Worker ()
    {
        this.guardian = new Guardian ();

        synchronized ( guardian )
        {
            guardianThread = new Thread ( guardian, "OPCGuardian" );
            guardianThread.setDaemon ( true );
            guardianThread.start ();

            try
            {
                log.info ( "Waiting for guardian..." );
                guardian.wait ();
                log.info ( "Guardian is up..." );
            }
            catch ( InterruptedException e )
            {
                throw new RuntimeException ( "Failed to initialize OPC guardian", e );
            }
        }
    }

    @Override
    protected void finalize () throws Throwable
    {
        guardian.shutdown ();
        super.finalize ();
    }

    public <T> T execute ( Job job, JobResult<T> result ) throws InvocationTargetException
    {
        OPCResultJobHandler<T> handler = new OPCResultJobHandler<T> ( result );
        WorkUnit workUnit = new WorkUnit ( job, handler );
        execute ( workUnit );
        if ( handler.getError () != null )
        {
            throw new InvocationTargetException ( handler.getError (), "Failed to call DCOM method" );
        }
        return handler.getResult ();
    }

    public void execute ( Job job, Runnable runnable ) throws InvocationTargetException
    {
        OPCRunnableJobHandler handler = new OPCRunnableJobHandler ( runnable );
        WorkUnit workUnit = new WorkUnit ( job, handler );
        execute ( workUnit );
        if ( handler.getError () != null )
        {
            throw new InvocationTargetException ( handler.getError (), "Failed to call DCOM method" );
        }
    }

    public void execute ( WorkUnit currentWorkUnit )
    {
        if ( currentWorkUnit == null )
        {
            throw new RuntimeException ( "Work unit must not be null" );
        }
        if ( currentWorkUnit.getJob () == null )
        {
            throw new RuntimeException ( "Job must be set" );
        }
        if ( currentWorkUnit.getJobHandler () == null )
        {
            throw new RuntimeException ( "Job handler must be set" );
        }

        synchronized ( this )
        {
            if ( this.currentWorkUnit != null )
            {
                throw new RuntimeException ( "Already running" );
            }
            this.currentWorkUnit = currentWorkUnit;
        }

        perform ();
    }

    protected Throwable performCancelable ()
    {
        try
        {
            log.debug ( "Start guardian" );
            guardian.startJob ( this.currentWorkUnit.getJob (), this );
            log.debug ( "Run job" );
            this.currentWorkUnit.getJob ().run ();
            log.debug ( "Run job finished" );
        }
        catch ( Throwable e )
        {
            log.warn ( "Job failed", e );
            return e;
        }
        finally
        {
            log.debug ( "Notify guardian that job is complete" );
            guardian.jobCompleted ();
            log.debug ( "guardian knows now" );
        }
        return null;
    }

    protected void perform ()
    {
        try
        {
            log.debug ( "Starting new job" );
            performCancelable ();
            log.debug ( "Job completed" );
        }
        catch ( Throwable e )
        {
            log.warn ( "Failed to process", e );
        }

        // now trigger the result handlers
        if ( this.currentWorkUnit.getJob ().isCanceled () )
        {
            // we got canceled
            this.currentWorkUnit.getJobHandler ().handleInterrupted ();
        }
        else if ( this.currentWorkUnit.getJob ().getError () == null )
        {
            // we succeeded
            this.currentWorkUnit.getJobHandler ().handleSuccess ();
        }
        else
        {
            // we failed
            this.currentWorkUnit.getJobHandler ().handleFailure ( this.currentWorkUnit.getJob ().getError () );
        }

        // we are clear again
        currentWorkUnit = null;
    }

    public void performCancel ()
    {
        WorkUnit workUnit = this.currentWorkUnit;
        if ( workUnit != null )
        {
            workUnit.getJob ().interrupt ();
        }
    }

}
