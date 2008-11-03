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

package org.openscada.utils.timing;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

public class Scheduler implements Runnable
{
    private static Logger log = Logger.getLogger ( Scheduler.class );

    private Thread thread;

    private final List<Job> jobs = new ArrayList<Job> ();

    private final int logJobs = Integer.getInteger ( "openscada.utils.logJobs", 0 );

    private long lastJobLog = 0;

    private String schedulerName = "Scheduler";

    public class Job
    {
        private final Runnable runnable;

        private int period;

        private long nextTime;

        private boolean once = false;

        public Job ( final Runnable runnable, final int period )
        {
            this.runnable = runnable;
            this.period = period;
            this.nextTime = System.currentTimeMillis () + period;
        }

        public Job ( final Runnable runnable, final int period, final boolean once )
        {
            this.runnable = runnable;
            this.period = period;
            this.nextTime = System.currentTimeMillis () + period;
            this.once = once;
        }

        public boolean isTimeOut ()
        {
            return System.currentTimeMillis () >= this.nextTime;
        }

        public void run ()
        {
            if ( this.period != 0 )
            {
                this.nextTime = ( System.currentTimeMillis () / this.period + 1 ) * this.period;
            }
            else
            {
                this.nextTime = System.currentTimeMillis ();
            }

            if ( this.runnable != null )
            {
                try
                {
                    this.runnable.run ();
                }
                catch ( final Throwable t )
                {
                    log.warn ( "Job failed", t );
                }
            }
        }

        @Override
        public boolean equals ( final Object obj )
        {
            if ( obj == null )
            {
                return false;
            }

            if ( obj == this )
            {
                return true;
            }

            if ( ! ( obj instanceof Job ) )
            {
                return false;
            }

            final Job job = (Job)obj;

            return this.runnable.equals ( job.runnable );
        }

        @Override
        public int hashCode ()
        {
            return this.runnable.hashCode ();
        }

        public int getPeriod ()
        {
            return this.period;
        }

        /**
         * Set the period time. Will be active only for the next period.
         * 
         * @param period
         *            the new period
         */
        public void setPeriod ( final int period )
        {
            this.period = period;
        }

        public boolean isOnce ()
        {
            return this.once;
        }

        /**
         * Triggers to job to run the next time, not mattering when it was
         * executed last
         */
        public void trigger ()
        {
            this.nextTime = System.currentTimeMillis ();
        }

        /**
         * delay the job to be executed at period from <b>now!</b>
         */
        public void bump ()
        {
            this.nextTime = System.currentTimeMillis () + this.period;
        }
    }

    public Scheduler ( final String name )
    {
        this ( true, name );
    }

    /**
     * Create a new scheduler. If it is asynchronous a new start will be started to process
     * things.
     * @param async flag if this scheduler is asynchronous or not
     */
    public Scheduler ( final boolean async, final String threadName )
    {
        this.schedulerName = threadName;

        if ( async )
        {
            this.thread = new Thread ( this, threadName );
            rebindToThread ( this.thread );
            this.thread.setDaemon ( true );
            this.thread.start ();
        }
    }

    public Scheduler ( final Thread thread )
    {
        rebindToThread ( thread );
    }

    /**
     * Schedule a job to be executed periodically The job will be executed first
     * after the period time.
     * 
     * @param runnable
     *            The runnabel to run
     * @param period
     *            the period in milliseconds
     * @return a job handle that can be used to modify the new job
     */
    public Job addJob ( final Runnable runnable, final int period )
    {
        return addJob ( runnable, period, true );
    }

    /**
     * Schedule a job to be executed periodically
     * 
     * @param runnable
     *            The runnabel to run
     * @param period
     *            the period in milliseconds
     * @param initialDelay
     *            if set to true, it will wait the period, otherwise the job
     *            will be executed first as soon as possible
     * @return a job handle that can be used to modify the new job
     */
    public Job addJob ( final Runnable runnable, final int period, final boolean initialDelay )
    {
        final Job job = new Job ( runnable, period );

        if ( !initialDelay )
        {
            job.trigger ();
        }

        synchronized ( job )
        {
            this.jobs.add ( job );
        }
        return job;
    }

    /**
     * Schedule a job to be executed once after the period time.
     *
     * @param runnable
     *            The runnabel to run
     * @param period
     *            the period in milliseconds
     * @return a job handle that can be used to modify the new job
     */
    public Job scheduleJob ( final Runnable runnable, final int period )
    {
        final Job job = new Job ( runnable, period, true );
        synchronized ( this.jobs )
        {
            this.jobs.add ( job );
        }
        return job;
    }

    /**
     * Schedules a job to be executed once as soon as possible on the scheduler
     * thread If the scheduler is bound to the current thread the job will be
     * executed directly. If the scheduler is not bound it will wait until the
     * binding is established.
     * 
     * @param runnable
     *            The runnable to execute
     * @param wait
     *            if <em>true</em> then the method will block until the job
     *            has been processed
     * @throws InterruptedException
     *             if the wait fails
     */
    public void executeJob ( final Runnable runnable, final boolean wait ) throws InterruptedException
    {
        if ( Thread.currentThread ().equals ( this.thread ) )
        {
            runnable.run ();
            return;
        }

        final Job job = new Job ( runnable, 0, true );
        synchronized ( job )
        {
            synchronized ( this.jobs )
            {
                this.jobs.add ( job );
            }

            if ( wait )
            {
                job.wait ();
            }
        }

    }

    /**
     * Schedules a job to be executed once as soon as possible on the scheduler
     * thread If the scheduler is bound to the current thread the job will be
     * executed directly. If the scheduler is not bound it will wait until the
     * binding is established.
     * 
     * @param runnable
     *            The runnable to execute
     * @throws InterruptedException
     *             if the wait fails
     */
    public void executeJob ( final Runnable runnable ) throws InterruptedException
    {
        executeJob ( runnable, true );
    }

    /**
     * Schedules a job to be executed once as soon as possible on the scheduler
     * thread If the scheduler is bound to the current thread the job will be
     * executed directly. If the scheduler is not bound it will <em>not</em>
     * wait until the binding is established.
     * 
     * @param runnable
     *            The runnable to execute
     */
    public void executeJobAsync ( final Runnable runnable )
    {
        try
        {
            executeJob ( runnable, false );
        }
        catch ( final InterruptedException e )
        {
            // may not be thrown since wait is not called
        }
    }

    public void removeJob ( final Runnable job )
    {
        synchronized ( this.jobs )
        {
            if ( log.isDebugEnabled () )
            {
                log.debug ( "Pre remove: " + this.jobs.size () );
            }
            this.jobs.remove ( new Job ( job, 0 ) );
            if ( log.isDebugEnabled () )
            {
                log.debug ( "Post remove: " + this.jobs.size () );
            }
        }
    }

    public void removeJob ( final Job job )
    {
        synchronized ( this.jobs )
        {
            if ( log.isDebugEnabled () )
            {
                log.debug ( "Pre remove: " + this.jobs.size () );
            }
            this.jobs.remove ( job );
            if ( log.isDebugEnabled () )
            {
                log.debug ( "Post remove: " + this.jobs.size () );
            }
        }
    }

    public void runOnce () throws NotBoundException, WrongThreadException
    {
        // check if the caller thread is the thread we are bound to
        ensureIsBound ();

        /*
         * if ( _jobs.size() > 0 ) _log.debug("Running once: " + _jobs.size() + "
         * job(s)");
         */
        if ( this.logJobs > 0 )
        {
            if ( System.currentTimeMillis () - this.lastJobLog > this.logJobs )
            {
                log.debug ( String.format ( "%s: %d jobs", this.schedulerName, this.jobs.size () ) );
                this.lastJobLog = System.currentTimeMillis ();
            }
        }

        // make a working copy
        List<Job> processList;
        synchronized ( this.jobs )
        {
            processList = new ArrayList<Job> ( this.jobs );
        }

        for ( final Iterator<Job> i = processList.iterator (); i.hasNext (); )
        {
            final Job job = i.next ();
            try
            {
                if ( job.isTimeOut () )
                {
                    synchronized ( job )
                    {
                        job.run ();
                        job.notifyAll ();
                    }
                    if ( job.isOnce () )
                    {
                        removeJob ( job );
                    }
                }
            }
            catch ( final Exception e )
            {
                log.warn ( "Error during job execution: ", e );
            }
        }

        processList.clear ();

    }

    public void run ()
    {
        while ( true )
        {
            try
            {
                Thread.sleep ( 100 );
            }
            catch ( final InterruptedException e )
            {
                // TODO Auto-generated catch block
            }

            try
            {
                runOnce ();
            }
            catch ( final NotBoundException e )
            {
                log.error ( "scheduler failed", e );
                this.thread = null;
                return;
            }
            catch ( final WrongThreadException e )
            {
                log.error ( "scheduler failed", e );
                this.thread = null;
                return;
            }
        }
    }

    public synchronized boolean isBound ()
    {
        return this.thread != null;
    }

    public synchronized void unbindFromThread ()
    {
        this.thread = null;
    }

    public synchronized void bindToThread ( final Thread thread ) throws AlreadyBoundException
    {
        if ( this.thread != null && !this.thread.equals ( thread ) )
        {
            throw new AlreadyBoundException ();
        }
        this.thread = thread;
    }

    public synchronized void bindToCurrentThread () throws AlreadyBoundException
    {
        bindToThread ( Thread.currentThread () );
    }

    /**
     * Rebinds to the provided thread This method is similar to the bindToThread
     * method. It only does not care about the fact that the instance is already
     * bound. It rebinds instead breaking the previous binding.
     * 
     * @param thread
     *            The thread to bind to
     */
    public synchronized void rebindToThread ( final Thread thread )
    {
        unbindFromThread ();
        try
        {
            bindToThread ( thread );
        }
        catch ( final AlreadyBoundException e )
        {
            // this should never happen since we previously unbound
        }
    }

    public synchronized void rebindToCurrentThread ()
    {
        rebindToThread ( Thread.currentThread () );
    }

    /**
     * Ensured that the calling thread is the one that we are bound to If the
     * binding is ok the method will return. Otherwise an exception will be
     * thrown.
     * 
     * @throws NotBoundException
     * @throws WrongThreadException
     */
    private synchronized void ensureIsBound () throws NotBoundException, WrongThreadException
    {
        if ( !isBound () )
        {
            throw new NotBoundException ();
        }

        if ( !this.thread.equals ( Thread.currentThread () ) )
        {
            throw new WrongThreadException ();
        }
    }
}
