package org.openscada.da.server.opc2.connection;

public interface OPCStateListener
{
    public void connectionEstablished ();
    public void connectionLost ();
}
