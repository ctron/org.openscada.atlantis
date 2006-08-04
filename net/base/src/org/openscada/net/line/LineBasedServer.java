package org.openscada.net.line;

import java.io.IOException;
import java.net.InetSocketAddress;

import org.openscada.net.io.IOProcessor;
import org.openscada.net.io.ServerSocket;
import org.openscada.net.io.SocketConnection;
import org.openscada.net.io.ServerSocket.ConnectionFactory;

public class LineBasedServer
{
    public interface HandlerFactory
    {
        LineHandler createHandler (); 
    }
    
    private HandlerFactory _factory = null;
    
    @SuppressWarnings("unused")
    private ServerSocket _server = null;
    
    public LineBasedServer ( IOProcessor processor, int port, HandlerFactory factory ) throws IOException
    {
        _factory = factory;
        
        _server = new ServerSocket ( processor, new InetSocketAddress ( port ), new ConnectionFactory () {

            public void accepted ( SocketConnection connection )
            {
                if ( _factory != null )
                {
                    LineBasedConnection newConnection = new LineBasedConnection ( connection, _factory.createHandler () );
                    newConnection.connected ();
                }
            }}
        );
    }
}
