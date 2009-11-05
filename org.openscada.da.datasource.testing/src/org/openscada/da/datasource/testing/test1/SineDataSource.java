package org.openscada.da.datasource.testing.test1;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;

import org.openscada.core.Variant;
import org.openscada.core.subscription.SubscriptionState;
import org.openscada.da.client.DataItemValue;
import org.openscada.da.datasource.testing.DefaultDataSource;

public class SineDataSource extends ScheduledDataSource implements DefaultDataSource
{

    private double factor1;

    private double factor2;

    private double amp1;

    private double amp2;

    public SineDataSource ( final ScheduledExecutorService scheduler )
    {
        super ( scheduler );
    }

    @Override
    protected void tick ()
    {
        final double time = System.currentTimeMillis () / 1000.0;

        final double value = this.amp1 * Math.sin ( time / this.factor1 ) + this.amp2 * Math.sin ( time / this.factor2 );
        setValue ( new Variant ( value ) );
    }

    @Override
    public void update ( final Map<String, String> properties )
    {
        this.timeDiff = getInteger ( properties, "time.diff", 0 );
        this.factor1 = getDouble ( properties, "factor1", 2000.0 );
        this.factor2 = getDouble ( properties, "factor2", 20.0 );

        this.amp1 = getDouble ( properties, "amp1", 100 );
        this.amp2 = getDouble ( properties, "amp2", 5 );

        super.update ( properties );
    }

    private void setValue ( final Variant variant )
    {
        final Map<String, Variant> attributes = new HashMap<String, Variant> ();
        attributes.put ( "timestamp", new Variant ( System.currentTimeMillis () + this.timeDiff ) );
        updateData ( new DataItemValue ( variant, attributes, SubscriptionState.CONNECTED ) );
    }

}
