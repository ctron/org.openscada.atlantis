package org.openscada.da.master;

import org.openscada.da.client.DataItemValue;

public interface MasterItemListener
{
    public void stateChanged ( DataItemValue value );
}
