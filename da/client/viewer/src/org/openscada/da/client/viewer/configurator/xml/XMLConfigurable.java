package org.openscada.da.client.viewer.configurator.xml;

import org.openscada.da.client.viewer.configurator.ConfigurationError;
import org.w3c.dom.Node;

public interface XMLConfigurable
{
    public abstract void configure ( XMLConfigurationContext context, Node node ) throws ConfigurationError;
}
