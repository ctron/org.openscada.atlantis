package org.openscada.da.client.viewer.model.impl;

import org.openscada.da.client.viewer.model.DynamicObject;
import org.openscada.da.client.viewer.model.ObjectFactory;

public class DataItemObjectFactory implements ObjectFactory
{

    public DynamicObject create ( String id )
    {
        return new DataItemObject ( id );
    }

}
