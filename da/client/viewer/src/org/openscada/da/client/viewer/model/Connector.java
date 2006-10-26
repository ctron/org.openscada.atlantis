package org.openscada.da.client.viewer.model;

public interface Connector
{
    public abstract void setInput ( InputDefinition input ) throws AlreadyConnectedException;
    public abstract void setOutput ( OutputDefinition output );
    public abstract void dispose ();
}
