package org.openscada.da.server.spring;

import org.openscada.da.server.common.DataItem;

public class DataItemEntry extends Entry
{
    private DataItem item;

    public DataItem getItem ()
    {
        return item;
    }

    public void setItem ( DataItem item )
    {
        this.item = item;
    }
}
