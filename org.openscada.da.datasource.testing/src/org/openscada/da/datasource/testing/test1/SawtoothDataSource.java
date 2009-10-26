package org.openscada.da.datasource.testing.test1;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;

import org.openscada.core.Variant;
import org.openscada.core.subscription.SubscriptionState;
import org.openscada.da.client.DataItemValue;
import org.openscada.da.datasource.testing.DefaultDataSource;

public class SawtoothDataSource extends ScheduledDataSource implements DefaultDataSource
{
    private Long startIndex;

    private double factor;

    private int cap;

    private int precision;

    private Variant lastValue;

    public SawtoothDataSource ( final ScheduledExecutorService scheduler )
    {
        super ( scheduler );
    }

    @Override
    protected void tick ()
    {
        if ( this.startIndex == null )
        {
            this.startIndex = System.currentTimeMillis ();
        }

        final long now = System.currentTimeMillis ();
        final long ms = ( now - this.startIndex ) / this.precision % this.cap;

        final double value = ms * this.factor;

        setValue ( new Variant ( value ) );
    }

    @Override
    public void update ( final Map<String, String> properties )
    {
        this.factor = getDouble ( properties, "factor", 1 );
        this.cap = getInteger ( properties, "cap", 1000 );
        this.precision = getInteger ( properties, "precision", 1000 );

        super.update ( properties );
    }

    private void setValue ( final Variant variant )
    {
        if ( this.lastValue == null || !this.lastValue.equals ( variant ) )
        {
            this.lastValue = variant;
            final Map<String, Variant> attributes = new HashMap<String, Variant> ();
            attributes.put ( "timestamp", new Variant ( System.currentTimeMillis () + this.timeDiff ) );
            updateData ( new DataItemValue ( variant, attributes, SubscriptionState.CONNECTED ) );
        }
    }

}
