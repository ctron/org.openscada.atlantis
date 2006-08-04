package org.openscada.io.lcl;

import org.openscada.io.lcl.data.Request;
import org.openscada.io.lcl.data.Response;
import org.openscada.net.line.ConnectionStateListener;

public interface ServerHandler extends ConnectionStateListener
{
    void setServerHandler ( ServerLineHandler lineHandler );
    Response handleRequest ( Request request );
}
