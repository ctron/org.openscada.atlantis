package org.openscada.da.server.exporter;

import org.openscada.da.core.server.Hive;

/**
 * A hive factory which needs to create a new hive based on its implementation. 
 * @author Jens Reimann
 *
 */
public interface HiveFactory
{
    /**
     * Create a new hive based on the reference and the configuration
     * @param reference the reference to the hive
     * @param configuration the configuration of the hive that is provided in the master configuration
     * @return the new hive
     * @throws ConfigurationException
     */
    public abstract Hive createHive ( String reference, HiveConfigurationType configuration ) throws ConfigurationException;
}
