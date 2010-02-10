package org.openscada.ca.client;

import org.openscada.ca.FactoryInformation;

public interface FactoriesListener
{
    public void updateFactories ( FactoryInformation[] factories );
}
