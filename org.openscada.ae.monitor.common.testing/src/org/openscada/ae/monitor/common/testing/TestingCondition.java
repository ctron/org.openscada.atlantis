package org.openscada.ae.monitor.common.testing;

import java.util.Date;
import java.util.Random;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.openscada.ae.event.EventProcessor;
import org.openscada.ae.monitor.common.AbstractMonitorService;
import org.openscada.ae.server.common.akn.AknHandler;
import org.openscada.core.Variant;
import org.openscada.sec.UserInformation;

public class TestingCondition extends AbstractMonitorService implements AknHandler
{

    private final ScheduledExecutorService scheduler = new ScheduledThreadPoolExecutor ( 1 );

    private final Random r = new Random ();

    public TestingCondition ( final EventProcessor eventProcessor, final String sourceName )
    {
        super ( eventProcessor, sourceName );
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
