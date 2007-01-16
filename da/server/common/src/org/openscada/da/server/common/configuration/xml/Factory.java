package org.openscada.da.server.common.configuration.xml;

import org.openscada.da.server.common.factory.DataItemFactory;

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
