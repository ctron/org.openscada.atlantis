package org.openscada.net.line;

public interface ConnectionStateListener
{
    void connected ();
    void closed ();
    void connectionFailed ( Throwable throwable );
}
