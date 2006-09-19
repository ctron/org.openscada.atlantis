package org.openscada.da.core.common.configuration;

import org.w3c.dom.Node;

public interface ConfigurableFactory
{

    public abstract void configure ( Node node );

}
