package org.openscada.io.lcl;

import org.openscada.io.lcl.data.Response;
import org.openscada.net.line.ConnectionStateListener;

public interface ClientHandler extends ConnectionStateListener
{
    void setClient ( Client client );
    void handleEvent ( Response response );
}
