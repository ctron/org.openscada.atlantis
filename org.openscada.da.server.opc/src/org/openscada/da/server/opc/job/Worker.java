/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2010 inavare GmbH (http://inavare.com)
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

package org.openscada.da.server.opc.job;

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

    private volatile WorkUnit currentWorkUnit;

    private final Guardian guardian;

    private final Thread guardianThread;

    protected static class OPCResultJobHandler<T> implements JobHandler
    {
        private Throwable error;

        private T result;

        private final JobResult<T> jobResult;

        public OPCResultJobHandler ( final JobResult<T> jobResult )
        {
            this.jobResult = jobResult;
        }

        public void handleFailure ( final Throwable e )
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
            // FIXME: isn't there a check for null necessary?
            this.result = this.jobResult.getResult ();
        }

        public Throwable getError ()
        {
            return this.error;
        }

        public T getResult ()
        {
            return this.result;
        }

    }

    protected static class OPCRunnableJobHandler implements JobHandler
    {

        private Throwable error;

        private final Runnable runnable;

        public OPCRunnableJobHandler ( final Runnable runnable )
        {
            this.runnable = runnable;
        }

        public void handleFailure ( final Throwable e )
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
            return this.error;
        }

    }

    public Worker ()
    {
        this.guardian = new Guardian ();

        synchronized ( this.guardian )
        {
            this.guardianThread = new Thread ( this.guardian, "OPCGuardian" );
            this.guardianThread.setDaemon ( true );
            this.guardianThread.start ();

            try
            {
                log.info ( "Waiting for guardian..." );
                this.guardian.wait ();
                log.info ( "Guardian is up..." );
            }
            catch ( final InterruptedException e )
            {
                throw new RuntimeException ( "Failed to initialize OPC guardian", e );
            }
        }
    }

    @Override
    protected void finalize () throws Throwable
    {
        this.guardian.shutdown ();
        super.finalize ();
    }

    public <T> T execute ( final Job job, final JobResult<T> result ) throws InvocationTargetException
    {
        final OPCResultJobHandler<T> handler = new OPCResultJobHandler<T> ( result );
        final WorkUnit workUnit = new WorkUnit ( job, handler );
        execute ( workUnit );
        if ( handler.getError () != null )
        {
            throw new InvocationTargetException ( handler.getError (), "Failed to call DCOM method" );
        }
        return handler.getResult ();
    }

    /**
     * Execute the job and run the runnable if the job was completed without error
     * @param job the job to run
     * @param runnable the runnable to run in the case of no error
     * @throws InvocationTargetException an exception if something went wrong
     */
    public void execute ( final Job job, final Runnable runnable ) throws InvocationTargetException
    {
        final OPCRunnableJobHandler handler = new OPCRunnableJobHandler ( runnable );
        final WorkUnit workUnit = new WorkUnit ( job, handler );
        execute ( workUnit );
        if ( handler.getError () != null )
        {
            throw new InvocationTargetException ( handler.getError (), "Failed to call DCOM method" );
        }
    }

    public void execute ( final WorkUnit currentWorkUnit )
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
            this.guardian.startJob ( this.currentWorkUnit.getJob (), this );
            log.debug ( "Run job" );
            this.currentWorkUnit.getJob ().run ();
            log.debug ( "Run job finished" );
        }
        catch ( final Throwable e )
        {
            log.warn ( "Job failed", e );
            return e;
        }
        finally
        {
            log.debug ( "Notify guardian that job is complete" );
            this.guardian.jobCompleted ();
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
        catch ( final Throwable e )
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
        this.currentWorkUnit = null;
    }

    public void performCancel ()
    {
        final WorkUnit workUnit = this.currentWorkUnit;
        if ( workUnit != null )
        {
            workUnit.getJob ().interrupt ();
        }
    }

}
