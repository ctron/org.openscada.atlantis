package org.openscada.da.client.viewer.model.impl.converter;

import org.openscada.da.client.viewer.configurator.ConfigurationError;
import org.openscada.da.client.viewer.model.DynamicObject;
import org.openscada.da.client.viewer.model.ObjectFactory;

public class SimpleVariantIntegerConverterFactory implements ObjectFactory
{

    public DynamicObject create () throws ConfigurationError
    {
        return new SimpleVariantIntegerConverter ();
    }
    
}
