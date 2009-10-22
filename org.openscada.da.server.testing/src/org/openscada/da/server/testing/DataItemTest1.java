package org.openscada.da.server.testing;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.openscada.core.Variant;
import org.openscada.da.server.common.chain.DataItemInputChained;

public class DataItemTest1 extends DataItemInputChained implements Runnable
{
    private final ScheduledExecutorService scheduledExecutor;

    private final ScheduledFuture<?> job;

    public DataItemTest1 ( final String id, final ScheduledExecutorService executor )
    {
        super ( id, executor );
        this.scheduledExecutor = executor;

        this.job = this.scheduledExecutor.scheduleAtFixedRate ( this, 1000, 1000, TimeUnit.MILLISECONDS );
    }

    public void dispose ()
    {
        this.job.cancel ( false );
    }

    public void run ()
    {
        updateData ( new Variant ( System.currentTimeMillis () ), null, null );
    }

}
