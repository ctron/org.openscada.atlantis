package org.openscada.core.client;

public interface Connection
{
    /**
     * Start the connection 
     *
     */
    public void connect ();
    /**
     * Stop the connection
     *
     */
    public void disconnect ();
    
    /**
     * Wait until the connection has been established or it finally could not be established.
     * @throws Throwable The error that occurred when the connection could not be established
     */
    public void waitForConnection () throws Throwable;
    
    public void addConnectionStateListener ( ConnectionStateListener connectionStateListener );
    public void removeConnectionStateListener ( ConnectionStateListener connectionStateListener );
    
    /**
     * Get the current connection state
     * @return The current connection state
     */
    public ConnectionState getState ();
}
