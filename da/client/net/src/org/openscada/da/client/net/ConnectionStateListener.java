package org.openscada.da.client.net;

public interface ConnectionStateListener
{
    public void stateChange ( Connection connection, Connection.State state );
}
