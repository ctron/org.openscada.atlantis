package org.openscada.da.core.common.configuration.xml;

import org.openscada.da.core.common.factory.DataItemFactory;

public class Factory
{
    private DataItemFactory _factory = null;

    public DataItemFactory getFactory ()
    {
        return _factory;
    }

    public void setFactory ( DataItemFactory factory )
    {
        _factory = factory;
    }
}
