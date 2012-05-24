package org.openscada.core.connection.provider.info;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class StatisticsImpl
{
    private final Map<Object, StatisticEntryImpl> entries = new HashMap<Object, StatisticEntryImpl> ();

    public synchronized Collection<StatisticEntry> getEntries ()
    {
        return new ArrayList<StatisticEntry> ( this.entries.values () );
    }

    public void setLabel ( final Object key, final String label )
    {
        getEntry ( key ).setLabel ( label );
    }

    private synchronized StatisticEntryImpl getEntry ( final Object key )
    {
        StatisticEntryImpl result = this.entries.get ( key );
        if ( result != null )
        {
            return result;
        }
        else
        {
            result = new StatisticEntryImpl ( key );
            this.entries.put ( key, result );
            return result;
        }
    }

    public void setCurrentValue ( final Object key, final double number )
    {
        getEntry ( key ).setCurrentValue ( key, number );
    }

    public void changeCurrentValue ( final Object key, final double offset )
    {
        getEntry ( key ).changeCurrentValue ( key, offset );
    }
}
