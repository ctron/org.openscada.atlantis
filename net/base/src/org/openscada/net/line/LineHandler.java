package org.openscada.net.line;

public interface LineHandler extends ConnectionStateListener
{
    void setConnection ( LineBasedConnection connection );
    void handleLine ( String line );
}
