package org.openscada.da.client.viewer.model.impl.figures;

import org.openscada.da.client.viewer.model.DynamicObject;
import org.openscada.da.client.viewer.model.ObjectFactory;

public class TriangleFactory implements ObjectFactory
{

    public DynamicObject create ()
    {
        return new Triangle ();
    }

}
