package org.openscada.utils.jobqueue;

import org.apache.log4j.Logger;
import org.openscada.utils.jobqueue.JobQueue.Job;

public class JobQueueRunner implements Runnable
{
    private static Logger _log = Logger.getLogger ( JobQueueRunner.class );
    
    private JobQueue _jobQueue = null;

    public JobQueueRunner ( JobQueue jobQueue )
    {
        _jobQueue = jobQueue;
    }
    
    public void run ()
    {
        while ( true )
        {
            try
            {
                Job job = _jobQueue.getNextWait ();
                runJob ( job );
            }
            catch ( InterruptedException e )
            {
                return;
            }
        }
    }
    
    private void runJob ( Job job )
    {
        _log.debug ( "Running job: " + job );
        try
        {
            job.getRunnable ().run ();
        }
        catch ( Exception e )
        {
            _log.info ( "Job failed", e  );
        }
        _jobQueue.completeJob ( job.getId () );
    }
    
    
}
