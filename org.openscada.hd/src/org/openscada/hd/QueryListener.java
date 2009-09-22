package org.openscada.hd;

import java.util.Map;
import java.util.Set;

public interface QueryListener
{
    public void updateState ( QueryState state );

    public void updateParameters ( QueryParameters parameters, Set<String> valueTypes );

    public void updateData ( int index, Map<String, Value[]> values, ValueInformation[] valueInformation );
}
