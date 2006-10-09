package org.openscada.da.client.viewer.model;

public interface Connector
{
    void setInput ( InputDefinition input ) throws AlreadyConnectedException;
    void setOutput ( OutputDefinition output );
}
