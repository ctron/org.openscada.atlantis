package org.openscada.da.datasource.testing.test1;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ScheduledExecutorService;

import org.openscada.core.Variant;
import org.openscada.core.subscription.SubscriptionState;
import org.openscada.da.client.DataItemValue;
import org.openscada.da.datasource.testing.DefaultDataSource;

public class RandomLongDataSource extends ScheduledDataSource implements DefaultDataSource
{
    private final Random r;

    private long variance;

    private long lastSwitch = System.currentTimeMillis ();

    private long offset;

    private long switchDelay = 10 * 1000;

    public RandomLongDataSource ( final ScheduledExecutorService scheduler )
    {
        super ( scheduler );
        this.r = new Random ();
    }

    @Override
    protected void tick ()
    {

        final long now = System.currentTimeMillis ();

        if ( now - this.lastSwitch > this.switchDelay )
        {
            this.lastSwitch = now;
            this.offset += this.r.nextLong () % this.variance;
        }

        setValue ( new Variant ( this.r.nextLong () % this.variance ) );
    }

    @Override
    public void update ( final Map<String, String> properties )
    {
        this.variance = getLong ( properties, "variance", 100 );
        this.switchDelay = getLong ( properties, "switchDelay", 10 * 1000 );

        super.update ( properties );
    }

    private void setValue ( final Variant variant )
    {
        final Map<String, Variant> attributes = new HashMap<String, Variant> ();
        attributes.put ( "timestamp", new Variant ( System.currentTimeMillis () + this.timeDiff ) );
        updateData ( new DataItemValue ( variant, attributes, SubscriptionState.CONNECTED ) );
    }

}
