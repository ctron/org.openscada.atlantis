package org.openscada.da.server.opc2.connection;

public enum ControllerState
{
    IDLE,
    CONNECTING,
    READING_STATUS,
    READING,
    WRITING,
    ACTIVATING, 
    DISCONNECTING,
    REGISTERING,
}
