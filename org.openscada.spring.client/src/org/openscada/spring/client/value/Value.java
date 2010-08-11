package org.openscada.spring.client.value;

import java.util.Calendar;

import org.openscada.utils.lang.Immutable;

@Immutable
public class Value<T>
{
    private final T value;

    private final boolean manual;

    private final boolean alarm;

    private final Calendar timestamp;

    public Value ( final T value, final boolean manual, final boolean alarm, final Calendar timestamp )
    {
        this.value = value;
        this.manual = manual;
        this.alarm = alarm;
        if ( timestamp != null )
        {
            this.timestamp = (Calendar)timestamp.clone ();
        }
        else
        {
            this.timestamp = null;
        }
    }

    public Value ( final T value, final Value<?> valueProperties )
    {
        this ( value, valueProperties.isManual (), valueProperties.isAlarm (), valueProperties.getTimestamp () );
    }

    public T getValue ()
    {
        return this.value;
    }

    public boolean isManual ()
    {
        return this.manual;
    }

    public boolean isAlarm ()
    {
        return this.alarm;
    }

    public Calendar getTimestamp ()
    {
        return this.timestamp;
    }

    /**
     * Return a <q>default value</q> with not flags and current timestamp
     * @param <T> the type of the value
     * @param defaultValue the value 
     * @return the newly created value, returns never <code>null</code>
     */
    public static <T> Value<T> createDefault ( final T defaultValue )
    {
        return new Value<T> ( defaultValue, false, false, Calendar.getInstance () );
    }

}
