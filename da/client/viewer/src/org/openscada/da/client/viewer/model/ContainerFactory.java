package org.openscada.da.client.viewer.model;

import org.openscada.da.client.viewer.configurator.ConfigurationError;

public interface ContainerFactory
{
    public abstract Container create ( String id ) throws ConfigurationError;
}
