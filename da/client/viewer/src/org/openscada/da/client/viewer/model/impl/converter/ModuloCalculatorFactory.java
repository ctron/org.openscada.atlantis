package org.openscada.da.client.viewer.model.impl.converter;

import org.openscada.da.client.viewer.model.DynamicObject;
import org.openscada.da.client.viewer.model.ObjectFactory;

public class ModuloCalculatorFactory implements ObjectFactory
{

    public DynamicObject create ()
    {
        return new ModuloCalculator ();
    }

}
