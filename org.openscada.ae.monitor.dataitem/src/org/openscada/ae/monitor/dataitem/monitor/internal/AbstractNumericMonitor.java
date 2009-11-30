package org.openscada.ae.monitor.dataitem.monitor.internal;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import org.openscada.ae.event.EventProcessor;
import org.openscada.core.NotConvertableException;
import org.openscada.core.NullValueException;
import org.openscada.core.Variant;
import org.openscada.da.client.DataItemValue;
import org.osgi.framework.BundleContext;

public abstract class AbstractNumericMonitor extends AbstractDataItemMonitor
{

    public AbstractNumericMonitor ( final BundleContext context, final EventProcessor eventProcessor, final String id, final String masterId, final Map<String, String> properties )
    {
        super ( context, eventProcessor, id, masterId, properties );
    }

    public DataItemValue dataUpdate ( final DataItemValue value )
    {
        if ( value == null )
        {
            setUnsafe ();
            return null;
        }
        final Variant variant = value.getValue ();
        if ( variant.isNull () )
        {
            setUnsafe ();
            return null;
        }

        final Date timestamp = getTimeFromValue ( value );

        try
        {
            final Number preset = getNumber ( "preset" );
            if ( preset == null )
            {
                setOk ( variant, timestamp );
            }
            else
            {
                final Boolean check = check ( variant, preset );
                if ( check == null )
                {
                    setUnsafe ();
                }
                else if ( check )
                {
                    setOk ( variant, timestamp );
                }
                else
                {
                    setFailure ( variant, timestamp );
                }
            }
        }
        catch ( final Throwable e )
        {
            setUnsafe ();
        }
        return null;
    }

    private Date getTimeFromValue ( final DataItemValue value )
    {
        Date timestamp;
        final Calendar c = value.getTimestamp ();
        if ( c != null )
        {
            timestamp = c.getTime ();
        }
        else
        {
            timestamp = new Date ();
        }
        return timestamp;
    }

    protected Boolean check ( final Variant variant, final Number preset )
    {
        try
        {
            switch ( variant.getType () )
            {
            case BOOLEAN:
            case INT32:
            case INT64:
                return checkLong ( variant.asLong (), preset.longValue () );

            case DOUBLE:
                return checkDouble ( variant.asDouble (), preset.doubleValue () );

            case STRING:
                final String strValue = variant.asString ();
                try
                {
                    final Double d = Double.parseDouble ( strValue );
                    return checkDouble ( d, preset.doubleValue () );
                }
                catch ( final Throwable e )
                {
                    return null;
                }

            case NULL:
            case UNKNOWN:
                return null;
            }
            // Should never be reached since all enum types _should_ be handled
            return null;
        }
        catch ( final NotConvertableException e )
        {
            return null;
        }
        catch ( final NullValueException e )
        {
            return null;
        }
    }

    /**
     * Compare value and preset based on the <code>long</code> type
     * @param value the value
     * @param preset the preset
     * @return <code>true</code> if the condition is good, <code>false</code> otherwise
     * @see #checkLong(long, long)
     */
    protected abstract boolean checkDouble ( final double value, final double preset );

    /**
     * Compare value and preset based on the <code>double</code> type
     * @param value the value
     * @param preset the preset
     * @return <code>true</code> if the condition is good, <code>false</code> otherwise
     * @see #checkDouble(double, double)
     */
    protected abstract boolean checkLong ( final long value, final long preset );
}
