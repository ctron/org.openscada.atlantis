package org.openscada.net.line;

import java.io.IOException;
import java.net.SocketAddress;

import org.openscada.net.io.IOProcessor;
import org.openscada.net.io.SocketConnection;

public class LineBasedClient implements LineHandler
{
    private LineBasedConnection _connection = null;
    
    private IOProcessor _processor = null;
    private LineHandler _handler = null;
    
    public LineBasedClient ( IOProcessor processor, LineHandler handler )
    {
        _processor = processor;
        _handler = handler;
    }
    
    public void connect ( SocketAddress remote )
    {
        synchronized ( this )
        {
            if ( _connection != null )
                return;
            try
            {
                setConnection ( new LineBasedConnection ( new SocketConnection ( _processor ), this ) );
                _connection.getConnection ().connect ( remote );
            }
            catch ( IOException e )
            {
                connectionFailed ( e );
            }
        }
    }

    public void handleLine ( String line )
    {
        _handler.handleLine ( line );
    }

    public void setConnection ( LineBasedConnection connection )
    {
        _connection = connection;
        _handler.setConnection ( connection );
    }

    public void closed ()
    {
        setConnection ( null );
        _handler.closed ();
    }

    public void connected ()
    {
        _handler.connected ();
    }

    public void connectionFailed ( Throwable throwable )
    {
        _handler.connectionFailed ( throwable );
        setConnection ( null );
    }
}
