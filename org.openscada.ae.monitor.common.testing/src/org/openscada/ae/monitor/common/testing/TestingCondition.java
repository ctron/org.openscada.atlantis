package org.openscada.ae.monitor.common.testing;

import java.util.Date;
import java.util.Random;
import java.util.concurrent.Executor;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.openscada.ae.event.EventProcessor;
import org.openscada.ae.monitor.common.AbstractStateMachineMonitorService;
import org.openscada.ae.server.common.akn.AknHandler;
import org.openscada.core.Variant;
import org.openscada.sec.UserInformation;
import org.osgi.framework.BundleContext;

public class TestingCondition extends AbstractStateMachineMonitorService implements AknHandler
{

    private final ScheduledExecutorService scheduler = new ScheduledThreadPoolExecutor ( 1 );

    private final Random r = new Random ();

    public TestingCondition ( final BundleContext context, final Executor executor, final EventProcessor eventProcessor, final String sourceName )
    {
        super ( context, executor, eventProcessor, sourceName );
        this.scheduler.scheduleAtFixedRate ( new Runnable () {

            public void run ()
            {
                TestingCondition.this.tick ();
            }
        }, 1000, 1000, TimeUnit.MILLISECONDS );
    }

    protected void tick ()
    {
        if ( this.r.nextBoolean () )
        {
            setOk ( new Variant ( true ), new Date () );
        }
        else
        {
            setFailure ( new Variant ( false ), new Date () );
        }
    }

    public void stop ()
    {
        this.scheduler.shutdown ();
    }

    public boolean acknowledge ( final String conditionId, final UserInformation aknUser, final Date aknTimestamp )
    {
        if ( getId ().equals ( conditionId ) )
        {
            akn ( aknUser, aknTimestamp );
            return true;
        }
        return false;
    }

}
