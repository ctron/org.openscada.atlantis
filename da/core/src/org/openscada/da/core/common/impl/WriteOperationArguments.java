package org.openscada.da.core.common.impl;

import org.openscada.da.core.common.DataItem;
import org.openscada.da.core.data.Variant;

public class WriteOperationArguments
{
    public DataItem item;
    public Variant value;
    
    public WriteOperationArguments ( DataItem item, Variant value )
    {
        this.item = item;
        this.value = value;
    }
}
