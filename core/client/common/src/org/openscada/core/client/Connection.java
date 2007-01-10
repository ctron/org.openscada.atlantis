package org.openscada.core.client;

public interface Connection
{
    public void connect ();
    public void disconnect ();
    
    public void waitForConnection () throws Throwable;
}
