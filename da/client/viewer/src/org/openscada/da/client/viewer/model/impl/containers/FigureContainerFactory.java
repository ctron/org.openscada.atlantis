package org.openscada.da.client.viewer.model.impl.containers;

import org.openscada.da.client.viewer.configurator.ConfigurationError;
import org.openscada.da.client.viewer.model.Container;
import org.openscada.da.client.viewer.model.ContainerFactory;

public class FigureContainerFactory implements ContainerFactory
{

    public Container create ( String id ) throws ConfigurationError
    {
        return new FigureContainer ( id );
    }

}
