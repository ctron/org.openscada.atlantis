package org.openscada.da.client.viewer.model;

import org.openscada.da.client.viewer.configurator.ConfigurationError;

public interface ConnectorFactory
{
    public abstract Connector create () throws ConfigurationError;
}
