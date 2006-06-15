package org.openscada.utils.timing;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

public class Scheduler implements Runnable
{
    private static Logger _log = Logger.getLogger(Scheduler.class);
    
	private Thread _thread;
	private List<Job> _jobs = new ArrayList<Job>();
	
	public class Job
	{
		private Runnable _runnable;
		private int _period;
		private long _nextTime;
        private boolean _once = false;
		
		public Job ( Runnable runnable, int period )
		{
			_runnable = runnable;
			_period = period;
            _nextTime = System.currentTimeMillis () + period;
		}
        
        public Job ( Runnable runnable, int period, boolean once )
        {
            _runnable = runnable;
            _period = period;
            _nextTime = System.currentTimeMillis () + period;
            _once = once;
        }
        
		public boolean isTimeOut ()
		{
			return System.currentTimeMillis () >= _nextTime;
		}
		
		public void run ()
		{
			//_nextTime = System.currentTimeMillis () + _period;
            if ( _period != 0 )
                _nextTime = ( ( System.currentTimeMillis () / _period ) + 1 ) * _period;
            else
                _nextTime = System.currentTimeMillis ();
            
			if ( _runnable != null )
				_runnable.run ();
		}
		
		@Override
		public boolean equals ( Object obj )
        {
            if ( obj == null )
                return false;
            
            if ( obj == this )
                return true;
            
			if ( !(obj instanceof Job) )
				return false;
            
			Job job = (Job)obj;
			
			return _runnable.equals ( job._runnable );
		}
		
		@Override
		public int hashCode ()
		{
			return _runnable.hashCode ();
		}

		public int getPeriod ()
        {
			return _period;
		}

        /**
         * Set the period time. Will be active only for the next period.
         * @param period the new period
         */
		public void setPeriod ( int period )
        {
			_period = period;
		}

        public boolean isOnce ()
        {
            return _once;
        }
        
        /**
         * Triggers to job to run the next time, not mattering when it was executed last
         *
         */
        public void trigger ()
        {
            _nextTime = System.currentTimeMillis ();
        }
	}
	
	public Scheduler ()
	{
		this(true);
	}
    
    public Scheduler ( boolean async )
    {
        if ( async )
        {
            _thread = new Thread(this);
            rebindToThread(_thread);
            _thread.setDaemon(true);
            _thread.start ();
        }
    }
    
    public Scheduler ( Thread thread )
    {
        rebindToThread(thread);
    }

    /**
     * Schedule a job to be executed periodically
     * 
     * The job will be executed first after the period time.
     * 
     * @param runnable The runnabel to run
     * @param period the period in milliseconds
     * @return a job handle that can be used to modify the new job
     */
    public Job addJob ( Runnable runnable, int period )
	{
		return addJob ( runnable, period, true );
	}
    
    /**
     * Schedule a job to be executed periodically
     * 
     * @param runnable The runnabel to run
     * @param period the period in milliseconds
     * @param initialDelay if set to true, it will wait the period, otherwise the job will be executed first as soon as possible
     * @return a job handle that can be used to modify the new job
     */
    public Job addJob ( Runnable runnable, int period, boolean initialDelay )
    {
        Job job =  new Job ( runnable, period );
        
        if ( !initialDelay )
            job.trigger();
        
        synchronized ( job )
        {
            _jobs.add ( job );
        }
        return job;
    }
    
    /**
     * Schedule a job to be executed once after the period time
     * @param runnable The runnabel to run
     * @param period the period in milliseconds
     * @return a job handle that can be used to modify the new job
     */
	public Job scheduleJob ( Runnable runnable, int period )
	{
        
	    Job job =  new Job ( runnable, period, true );
        synchronized (_jobs)
        {
            _jobs.add ( job );
        }
	    return job;
	}
    
    /**
     * Schedules a job to be executed as soon as possible on the scheduler thread
     * 
     * If the scheduler is bound to the current thread the job will be executed directly.
     * 
     * If the scheduler is not bound it will wait until the binding is established.
     * 
     * @param runnable The runnable to execute
     * @param wait if <em>true</em> then the method will block until the job has been processed
     * @throws InterruptedException if the wait fails
     */
    public void executeJob ( Runnable runnable, boolean wait ) throws InterruptedException
    {
        if ( Thread.currentThread().equals(_thread) )
        {
            runnable.run();
            return;
        }
        
        Job job =  new Job ( runnable, 0, true );
        synchronized(job)
        {
            synchronized(_jobs)
            {
                _jobs.add ( job );
            }
            
            if ( wait )
                job.wait ();
        }            
        
    }
    
    /**
     * Schedules a job to be executed as soon as possible on the scheduler thread
     * 
     * If the scheduler is bound to the current thread the job will be executed directly.
     * 
     * If the scheduler is not bound it will wait until the binding is established.
     * 
     * @param runnable The runnable to execute
     * @throws InterruptedException if the wait fails
     */
    public void executeJob ( Runnable runnable ) throws InterruptedException
    {
        executeJob ( runnable, true );
    }
    
    /**
     * Schedules a job to be executed as soon as possible on the scheduler thread
     * 
     * If the scheduler is bound to the current thread the job will be executed directly.
     * 
     * If the scheduler is not bound it will <em>not</em> wait until the binding is established.
     * 
     * @param runnable The runnable to execute
     */
    public void executeJobAsync ( Runnable runnable )
    {
        try
        {
            executeJob ( runnable, false );
        }
        catch ( InterruptedException e )
        {
            // may not be thrown since wait is not called
        }
    }
	
	public void removeJob ( Runnable job )
	{
        synchronized ( _jobs )
        {
            _jobs.remove ( new Job ( job, 0 ) );
        }
	}
	
	public void removeJob ( Job job )
	{
        synchronized ( _jobs )
        {
            _log.debug("Pre remove: " + _jobs.size() );
            _jobs.remove ( job );
            _log.debug("Post remove: " + _jobs.size() );
        }
	}

    public void runOnce () throws NotBoundException, WrongThreadException
    {
        // check if the caller thread is the thread we are bound to
        ensureIsBound();
        
        /*
        if ( _jobs.size() > 0 )
            _log.debug("Running once: " + _jobs.size() + " job(s)");
        */
        
        // make a working copy
        List<Job> processList;
        synchronized ( _jobs )
        {
            processList = new ArrayList<Job>(_jobs);
        }
        
        for ( Iterator<Job> i = processList.iterator(); i.hasNext(); )
        {
            Job job = i.next();
            try
            {
                if ( job.isTimeOut() )
                {
                    synchronized(job)
                    {
                        job.run();
                        job.notifyAll();
                    }
                    if ( job.isOnce() )
                        removeJob(job);
                }
            }
            catch ( Exception e )
            {
                _log.warn("Error during job execution: ", e);
            }
        }
        
        processList.clear();

    }
    
	public void run()
    {
		while ( true )
		{
			try
            {
				Thread.sleep(100);
			}
            catch (InterruptedException e)
            {
				// TODO Auto-generated catch block
			}
			
			try
            {
                runOnce ();
            }
            catch ( NotBoundException e )
            {
                _log.error ( "scheduler failed", e );
                _thread = null;
                return;
            }
            catch ( WrongThreadException e )
            {
                _log.error ( "scheduler failed", e );
                _thread = null;
                return;
            }
		}
	}

    
    synchronized public boolean isBound()
    {
        return _thread != null;
    }
    
    synchronized public void unbindFromThread ()
    {
        _thread = null;
    }
    
	synchronized public void bindToThread ( Thread thread ) throws AlreadyBoundException
    {
        if ( (_thread != null) && !_thread.equals(thread) )
            throw new AlreadyBoundException();
        
        _thread = thread;
    }
    
    synchronized public void bindToCurrentThread () throws AlreadyBoundException
    {
        bindToThread ( Thread.currentThread() ); 
    }
    
    /**
     * Rebinds to the provided thread
     * 
     * This method is similar to the bindToThread method. It only does not
     * care about the fact that the instance is already bound. It rebinds instead
     * breaking the previous binding.
     * 
     * @param thread The thread to bind to
     */
    synchronized public void rebindToThread ( Thread thread )
    {
        unbindFromThread();
        try
        {
            bindToThread ( thread );
        }
        catch ( AlreadyBoundException e )
        {
            // this should never happen since we previously unbound
        }
    }
    
    synchronized public void rebindToCurrentThread ()
    {
        rebindToThread ( Thread.currentThread() );
    }
    
    /**
     * Ensured that the calling thread is the one that we are bound to
     * 
     * If the binding is ok the method will return. Otherwise an exception will be thrown.
     * 
     * @throws NotBoundException
     * @throws WrongThreadException
     */
    synchronized private void ensureIsBound (  ) throws NotBoundException, WrongThreadException
    {
        if ( !isBound() )
            throw new NotBoundException();
        
        if ( !_thread.equals(Thread.currentThread()) )
            throw new WrongThreadException();
    }
}
