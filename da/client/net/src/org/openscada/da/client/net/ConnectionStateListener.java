package org.openscada.da.client.net;

public interface ConnectionStateListener
{
    public void connected ( Connection connection );
    public void disconnected ( Connection connection );
}
