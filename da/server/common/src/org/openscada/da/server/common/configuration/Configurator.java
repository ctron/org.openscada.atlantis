package org.openscada.da.server.common.configuration;

public interface Configurator
{

    public abstract void configure ( ConfigurableHive configurableHive ) throws ConfigurationError;

}