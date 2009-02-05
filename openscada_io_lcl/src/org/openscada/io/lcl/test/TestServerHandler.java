package org.openscada.io.lcl.test;

import org.openscada.io.lcl.ServerHandler;
import org.openscada.io.lcl.ServerLineHandler;
import org.openscada.io.lcl.data.Request;
import org.openscada.io.lcl.data.Response;

public class TestServerHandler implements ServerHandler
{

    private ServerLineHandler _lineHandler = null;
    
    public Response handleRequest ( Request request )
    {
        Response response = new Response ( 0, "ECHO: '" + request.getCommand () + "' / '" + request.getData () + "'" );
        _lineHandler.sendResponse ( response );

        if ( 
                request.getCommand ().equals ( "CLOSE" ) ||
                request.getCommand ().equals ( "QUIT" ) ||
                request.getCommand ().equals ( "EXIT" ) ||
                request.getCommand ().equals ( "BYE" )
        )
        {
            _lineHandler.sendResponse ( new Response ( 0, "Bye" ) );
            _lineHandler.close ();
            return null;
        }
        else if ( request.getCommand ().equals ( "PING" ) )
        {
            return new Response ( 1, "PONG" );
        }
        return null;
    }

    public void setServerHandler ( ServerLineHandler lineHandler )
    {
        _lineHandler = lineHandler;
        _lineHandler.setTimeout ( 10 * 1000 );
    }

    public void closed ()
    {
        _lineHandler = null;
    }

    public void connected ()
    {
        Response response = new Response ( 0, "Welcome" );
       _lineHandler.sendResponse ( response );
    }

    public void connectionFailed ( Throwable throwable )
    {
        _lineHandler = null;
    }

}
