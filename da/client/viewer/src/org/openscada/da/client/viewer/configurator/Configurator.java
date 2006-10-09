package org.openscada.da.client.viewer.configurator;

import java.util.List;

import org.openscada.da.client.viewer.model.View;

public interface Configurator
{
    public abstract List<View> configure () throws ConfigurationError;
}
