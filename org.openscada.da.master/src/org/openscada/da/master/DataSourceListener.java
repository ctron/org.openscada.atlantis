package org.openscada.da.master;

import org.openscada.da.client.DataItemValue;

public interface DataSourceListener
{
    public void stateChanged ( DataItemValue value );
}
