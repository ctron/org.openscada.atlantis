package org.openscada.da.server.opc;

import org.apache.log4j.Logger;
import org.openscada.da.server.opc2.job.ThreadJob;

public class SampleJob extends ThreadJob
{
    private static Logger logger = Logger.getLogger ( SampleJob.class );

    private long sleep;
    private boolean fails;

    public SampleJob ( long timeout, long sleep, boolean fails )
    {
        super ( timeout );

        this.sleep = sleep;
        this.fails = fails;
    }

    @Override
    protected void perform () throws Exception
    {
        logger.info ( String.format ( "Sleeping: %d ms", sleep ) );
        if ( fails )
        {
            throw new Exception ( "Sample job failed ... as expected!" );
        }
        Thread.sleep ( sleep );
    }
}
