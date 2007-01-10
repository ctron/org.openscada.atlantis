package org.openscada.core.client;

public interface Connection
{
    public void connect ();
    public void disconnect ();
    
    public void waitForConnection () throws Throwable;
    
    public void addConnectionStateListener ( ConnectionStateListener connectionStateListener );
    public void removeConnectionStateListener ( ConnectionStateListener connectionStateListener );
    
    public ConnectionState getState ();
}
