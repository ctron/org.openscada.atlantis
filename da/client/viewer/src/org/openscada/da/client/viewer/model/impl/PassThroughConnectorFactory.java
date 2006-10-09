package org.openscada.da.client.viewer.model.impl;

import org.openscada.da.client.viewer.model.Connector;
import org.openscada.da.client.viewer.model.ConnectorFactory;

public class PassThroughConnectorFactory implements ConnectorFactory
{

    public Connector create ()
    {
        return new DisplaySynchronizedConnector ();
    }

}
