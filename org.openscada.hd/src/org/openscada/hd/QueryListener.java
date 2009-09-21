package org.openscada.hd;

import java.util.Map;

public interface QueryListener
{
    public void updateState ( QueryState state );

    public void updateData ( int index, Map<String, Value[]> values, ValueInformation[] valueInformation );
}
