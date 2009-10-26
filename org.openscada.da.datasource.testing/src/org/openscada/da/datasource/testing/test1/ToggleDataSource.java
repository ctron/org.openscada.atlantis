package org.openscada.da.datasource.testing.test1;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;

import org.openscada.core.Variant;
import org.openscada.core.subscription.SubscriptionState;
import org.openscada.da.client.DataItemValue;
import org.openscada.da.datasource.testing.DefaultDataSource;

public class ToggleDataSource extends ScheduledDataSource implements DefaultDataSource
{
    private boolean toggle;

    private double value;

    public ToggleDataSource ( final ScheduledExecutorService scheduler )
    {
        super ( scheduler );
    }

    @Override
    protected void tick ()
    {
        this.toggle = !this.toggle;

        setValue ( new Variant ( this.toggle ? this.value : -this.value ) );
    }

    @Override
    public void update ( final Map<String, String> properties )
    {
        this.value = getDouble ( properties, "value", 0.0 );

        super.update ( properties );
    }

    private void setValue ( final Variant variant )
    {
        final Map<String, Variant> attributes = new HashMap<String, Variant> ();
        attributes.put ( "timestamp", new Variant ( System.currentTimeMillis () + this.timeDiff ) );
        updateData ( new DataItemValue ( variant, attributes, SubscriptionState.CONNECTED ) );
    }

}
