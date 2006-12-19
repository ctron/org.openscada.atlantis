package org.openscada.io.lcl.test;

import org.apache.log4j.Logger;
import org.openscada.io.lcl.DumpStateListener;
import org.openscada.io.lcl.Client;
import org.openscada.io.lcl.ClientHandler;
import org.openscada.io.lcl.data.Request;
import org.openscada.io.lcl.data.Response;

public class TestClientHandler implements ClientHandler
{
    static private Logger _log = Logger.getLogger ( TestClientHandler.class );
    
    private Client _client = null;
    
    public void handleEvent ( Response response )
    {
        _log.info ( "Event: " + response.getCode () + " / " + response.getData () );
    }

    public void closed ()
    {
        _log.info ( "Closed connection" );
    }

    public void connected ()
    {
        _log.info ( "Connected" );
        
        Request request = new Request ( "PING", "" );
        
        _client.sendRequest ( request, new DumpStateListener () );
        _client.sendRequest ( request, new DumpStateListener () );
        
        new Thread ( new Runnable () {

            public void run ()
            {
                Request request = new Request ( "PING", "" );
                DumpStateListener listener = new DumpStateListener ();
                
                synchronized ( listener )
                {
                    _client.sendRequest ( request, listener );
                    try
                    {
                        listener.wait ();
                        _log.info ( "Wait complete" );
                    }
                    catch ( InterruptedException e )
                    {
                        e.printStackTrace();
                    }
                }
            }} ).start ();
    }

    public void connectionFailed ( Throwable throwable )
    {
        _log.info ( "Connection failed", throwable );
    }

    public void setClient ( Client client )
    {
        _client = client;
    }

}
