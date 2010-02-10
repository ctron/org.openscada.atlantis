package org.openscada.ca.client;

import org.openscada.ca.FactoryInformation;
import org.openscada.utils.concurrent.NotifyFuture;

public interface Connection extends org.openscada.core.client.Connection
{
    public void addFactoriesListener ( FactoriesListener listener );

    public void removeFactoriesListener ( FactoriesListener listener );

    public NotifyFuture<FactoryInformation[]> getFactories ();

    public NotifyFuture<FactoryInformation> getFactoryWithData ( String factoryId );
}
