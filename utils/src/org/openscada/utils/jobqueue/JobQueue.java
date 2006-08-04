package org.openscada.utils.jobqueue;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Random;

public class JobQueue
{
    public class Job
    {
        private Long _id;
        private Runnable _runnable;
        
        public Job ( Long id, Runnable runnable )
        {
            _id = id;
            _runnable = runnable;
        }

        public Long getId ()
        {
            return _id;
        }

        public Runnable getRunnable ()
        {
            return _runnable;
        }
    }
    private Map<Long, Job> _jobMap = new HashMap<Long, Job> ();
    private Queue<Job> _jobQueue = new LinkedList<Job> ();
    
    public long addJob ( Runnable runnable )
    {
        Job job = null;
        Long id;
        synchronized ( _jobMap )
        {
            Random r = new Random ();
    
            do
            {
                id = r.nextLong ();
            } while ( _jobMap.containsKey ( id ) );
            
            job = new Job ( id, runnable );
            _jobMap.put ( id, job );
        }
        synchronized ( _jobQueue )
        {
            _jobQueue.add ( job );
            _jobQueue.notify ();
        }
        return id;
    }
    
    public boolean removeJob ( long id )
    {
        Job job = null;
        synchronized ( _jobMap )
        {
            if ( _jobMap.containsKey ( id ) )
            {
                job = _jobMap.remove ( id );
            }
            
            if ( job != null )
            {
                synchronized ( _jobQueue )
                {
                    _jobQueue.remove ( job );
                    return true;
                }
            }
            return false;
        }
    }
    
    public Job getNext ()
    {
        synchronized ( _jobQueue )
        {
            return _jobQueue.peek ();
        }
    }
    
    public Job getNextWait () throws InterruptedException
    {
        synchronized ( _jobQueue )
        {
            while ( _jobQueue.isEmpty () )
            {
                _jobQueue.wait ();
            }
            
            return _jobQueue.peek ();
        }
    }
    
    public void completeJob ( long id )
    {
        removeJob ( id );
    }
}
