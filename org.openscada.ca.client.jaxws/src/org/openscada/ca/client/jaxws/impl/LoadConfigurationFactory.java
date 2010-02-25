package org.openscada.ca.client.jaxws.impl;

import java.util.concurrent.Callable;

import org.openscada.ca.ConfigurationInformation;
import org.openscada.ca.client.jaxws.RemoteConfigurationClient;

public class LoadConfigurationFactory implements Callable<ConfigurationInformation>
{

    private final RemoteConfigurationClient port;

    private final String factoryId;

    private final String configurationId;

    public LoadConfigurationFactory ( final RemoteConfigurationClient port, final String factoryId, final String configurationId )
    {
        this.factoryId = factoryId;
        this.configurationId = configurationId;
        this.port = port;
    }

    @Override
    public ConfigurationInformation call () throws Exception
    {
        this.port.getPort ().getConfiguration ( this.factoryId, this.configurationId );
        final ConfigurationInformation result = this.port.getPort ().getConfiguration ( this.factoryId, this.configurationId );
        return result;
    }
}
