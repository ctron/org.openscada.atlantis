package org.openscada.da.server.opc;

import java.util.Random;

import org.apache.log4j.Logger;
import org.openscada.da.server.opc2.job.JobHandler;
import org.openscada.da.server.opc2.job.WorkUnit;
import org.openscada.da.server.opc2.job.Worker;

public class JobApp implements JobHandler
{
    private static Logger logger = Logger.getLogger ( JobApp.class );

    public void test1 ()
    {
        Worker worker = new Worker ();
        for ( int i = 0; i < 10; i++ )
        {
            try
            {
                worker.execute ( new WorkUnit ( new SampleJob ( 100, 5000, false ), this ) );
            }
            catch ( Throwable e )
            {
                logger.warn ( String.format ( "Failed in iteration #%d", i ), e );
                return;
            }
        }
        worker = null;
        System.gc ();
    }

    public void test2 ()
    {
        Worker worker = new Worker ();
        for ( int i = 0; i < 10; i++ )
        {
            try
            {
                worker.execute ( new WorkUnit ( new SampleJob ( 100, 5000, true ), this ) );
            }
            catch ( Throwable e )
            {
                logger.warn ( String.format ( "Failed in iteration #%d", i ), e );
                return;
            }
        }
        worker = null;
        System.gc ();
    }

    public void test3 ()
    {
        Random r = new Random ();

        Worker worker = new Worker ();
        for ( int i = 0; i < 1000; i++ )
        {
            try
            {
                boolean peakJob = r.nextInt ( 100 ) > 90;
                long sleep = r.nextInt ( 100 );
                if ( peakJob )
                {
                    sleep *= 100;
                }
                worker.execute ( new WorkUnit ( new SampleJob ( r.nextInt ( 100 ), sleep, r.nextBoolean () ), this ) );
            }
            catch ( Throwable e )
            {
                logger.warn ( String.format ( "Failed in iteration #%d", i ), e );
                return;
            }
        }
        worker = null;
        System.gc ();
    }

    public static void main ( String[] args )
    {
        JobApp app = new JobApp ();
        app.test1 ();
    }

    public void handleFailure ( Throwable e )
    {
        logger.warn ( "Job failed", e );
    }

    public void handleInterrupted ()
    {
        logger.warn ( "Job got interrupted" );
    }

    public void handleSuccess ()
    {
        logger.warn ( "Job succeeded" );
    }
}
