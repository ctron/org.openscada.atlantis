package org.openscada.da.client.viewer.model;

import org.openscada.da.client.viewer.configurator.ConfigurationError;

public interface ObjectFactory
{
    public abstract DynamicObject create () throws ConfigurationError; 
}
