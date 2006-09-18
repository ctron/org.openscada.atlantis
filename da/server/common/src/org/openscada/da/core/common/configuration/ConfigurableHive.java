package org.openscada.da.core.common.configuration;

import org.openscada.da.core.common.DataItem;
import org.openscada.da.core.common.DataItemFactory;

public interface ConfigurableHive
{

    // data item
    public abstract void registerItem ( DataItem item );

    public abstract void addItemFactory ( DataItemFactory factory );

}