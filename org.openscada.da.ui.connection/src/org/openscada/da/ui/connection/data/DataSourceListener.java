package org.openscada.da.ui.connection.data;

import org.openscada.da.client.DataItemValue;

public interface DataSourceListener
{
    public void updateData ( DataItemValue value );
}
