package org.openscada.ca.client.jaxws.impl;

import java.util.concurrent.Callable;

import org.openscada.ca.FactoryInformation;
import org.openscada.ca.client.jaxws.RemoteConfigurationClient;

public class QueryFactoryList implements Callable<FactoryInformation[]>
{

    private final RemoteConfigurationClient port;

    public QueryFactoryList ( final RemoteConfigurationClient port )
    {
        this.port = port;
    }

    @Override
    public FactoryInformation[] call () throws Exception
    {
        return this.port.getPort ().getFactories ();
    }

}
