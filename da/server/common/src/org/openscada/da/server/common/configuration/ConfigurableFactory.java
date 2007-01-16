package org.openscada.da.server.common.configuration;

import org.w3c.dom.Node;

public interface ConfigurableFactory
{

    public abstract void configure ( Node node );

}
