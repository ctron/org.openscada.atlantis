package org.openscada.ae.server.common.condition.testing;

import java.util.Date;
import java.util.Random;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.openscada.ae.ConditionStatus;
import org.openscada.ae.ConditionStatusInformation;
import org.openscada.ae.server.common.condition.ConditionQuery;
import org.openscada.core.Variant;

public class TestConditionQuery extends ConditionQuery
{
    private final ScheduledThreadPoolExecutor scheduler;

    private static final Random r = new Random ();

    public TestConditionQuery ()
    {
        this.scheduler = new ScheduledThreadPoolExecutor ( 1 );
        this.scheduler.scheduleAtFixedRate ( new Runnable () {

            public void run ()
            {
                tick ();
            }
        }, 1000, 1000, TimeUnit.MILLISECONDS );
    }

    protected void tick ()
    {
        updateData ( new ConditionStatusInformation[] { new ConditionStatusInformation ( "test", r.nextBoolean () ? ConditionStatus.OK : ConditionStatus.NOT_OK, new Date (), new Variant (), new Date (), "system", null ) }, null );
    }

    public void stop ()
    {
        this.scheduler.shutdown ();
    }
}
