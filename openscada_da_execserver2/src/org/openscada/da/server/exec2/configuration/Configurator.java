package org.openscada.da.server.exec2.configuration;

import org.openscada.da.server.exec2.Hive;

public interface Configurator
{
    public void configure ( Hive hive ) throws ConfigurationException;
}
