package org.openscada.da.server.simulation.scriptomatic.configuration;

import org.openscada.da.server.simulation.scriptomatic.Hive;

public interface Configurator
{
    public void configure ( Hive hive ) throws ConfigurationException;
}
