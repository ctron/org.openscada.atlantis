package org.openscada.ae.monitor.dataitem;

import java.util.Calendar;
import java.util.Date;

import org.openscada.ae.event.EventProcessor;
import org.openscada.da.client.DataItemValue;
import org.openscada.da.client.DataItemValue.Builder;
import org.osgi.framework.BundleContext;

public abstract class AbstractBooleanMonitor extends AbstractDataItemMonitor
{
    protected Boolean value;

    protected abstract void update ();

    protected Date timestamp;

    public AbstractBooleanMonitor ( final BundleContext context, final EventProcessor eventProcessor, final String id, final String prefix )
    {
        super ( context, eventProcessor, id, prefix );
    }

    @Override
    protected void performDataUpdate ( final Builder builder )
    {
        final DataItemValue value = builder.build ();
        this.timestamp = toTimestamp ( value );
        if ( value == null || !value.isConnected () || value.isError () || value.getValue ().isNull () )
        {
            this.value = null;
        }
        else
        {
            this.value = value.getValue ().asBoolean ();
        }

        update ();
    }

    protected static Date toTimestamp ( final DataItemValue value )
    {
        if ( value == null )
        {
            return new Date ();
        }
        final Calendar c = value.getTimestamp ();
        if ( c == null )
        {
            return new Date ();
        }
        else
        {
            return c.getTime ();
        }
    }

}