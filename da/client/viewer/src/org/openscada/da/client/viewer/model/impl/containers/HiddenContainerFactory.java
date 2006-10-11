package org.openscada.da.client.viewer.model.impl.containers;

import org.openscada.da.client.viewer.configurator.ConfigurationError;
import org.openscada.da.client.viewer.model.Container;
import org.openscada.da.client.viewer.model.ContainerFactory;

public class HiddenContainerFactory implements ContainerFactory
{

    public Container create ( String id ) throws ConfigurationError
    {
        return new HiddenContainer ( id );
    }

}
