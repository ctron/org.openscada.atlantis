package org.openscada.da.client.viewer.model;

public interface InputDefinition extends TargetDefinition, ValueReceiver
{
    public abstract void connect ( Connector connector ) throws AlreadyConnectedException;
    public abstract void disconnect ( Connector connector ) throws NotConnectedException;
}
