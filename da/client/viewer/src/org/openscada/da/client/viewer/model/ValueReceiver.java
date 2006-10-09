package org.openscada.da.client.viewer.model;

public interface ValueReceiver
{
    public void update ( Type type, Object value );
}
