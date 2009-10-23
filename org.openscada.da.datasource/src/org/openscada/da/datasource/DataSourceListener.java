package org.openscada.da.datasource;

import org.openscada.da.client.DataItemValue;

public interface DataSourceListener
{
    public void stateChanged ( DataItemValue value );
}
