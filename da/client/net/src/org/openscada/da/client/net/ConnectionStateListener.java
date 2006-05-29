package org.openscada.da.client.net;

public interface ConnectionStateListener
{
    /**
     * Notify a state change
     * @param connection the connection that changed
     * @param state the new state of the connection
     * @param error some error information that lead to the new state (can be <code>null</code> if it was not an error)
     */
    public void stateChange ( Connection connection, Connection.State state, Throwable error );
}
