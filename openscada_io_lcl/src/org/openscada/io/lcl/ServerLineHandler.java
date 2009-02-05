package org.openscada.io.lcl;

import java.util.StringTokenizer;

import org.apache.log4j.Logger;
import org.openscada.io.lcl.data.Request;
import org.openscada.io.lcl.data.Response;
import org.openscada.net.line.LineBasedConnection;
import org.openscada.net.line.LineHandler;

public class ServerLineHandler implements LineHandler
{
    private static Logger _log = Logger.getLogger ( ServerLineHandler.class );
    
    private LineBasedConnection _connection = null;
    private ServerHandler _handler = null;
    
    public ServerLineHandler ( ServerHandler handler )
    {
        _handler = handler;
    }
    
    public void handleLine ( String line )
    {
        StringTokenizer tok = new StringTokenizer ( line, "\t\n\r " );
        
        Request request = new Request ();
        
        try
        {
            request.setCommand ( tok.nextToken () );
            try
            {
                request.setData ( tok.nextToken ( "" ).trim () );
            }
            catch ( Exception e )
            {}
            sendResponse ( _handler.handleRequest ( request ) );
        }
        catch ( Exception e )
        {
            _log.warn ( "Handler error:", e );
        }
    }

    public void sendResponse ( Response response )
    {
        if ( response == null )
            return;
        
        synchronized ( this )
        {
            if ( _connection != null )
                _connection.sendLine ( String.format ( "%03d %s", response.getCode (), response.getData () ) );
        }
    }
    
    public void setConnection ( LineBasedConnection connection )
    {
        _connection = connection;
        
        if ( _connection != null )
            _handler.setServerHandler ( this );
        else
            _handler.setServerHandler ( null );
    }

    public void closed ()
    {
        _handler.closed ();
    }

    public void connected ()
    {
        _handler.connected ();
    }

    public void connectionFailed ( Throwable throwable )
    {
        _handler.connectionFailed ( throwable );
    }

    public void close ()
    {
        synchronized ( this )
        {
            if ( _connection != null )
                _connection.close ();
        }
    }

    public void setTimeout ( int timeout )
    {
        synchronized ( this )
        {
            if ( _connection != null )
                _connection.setTimeout ( timeout );
        }
    }

}
