package org.openscada.net.base;

import java.net.SocketAddress;

import org.apache.log4j.Logger;
import org.openscada.net.io.Client;
import org.openscada.net.io.IOProcessor;

public class ClientConnection extends ConnectionHandlerBase
{
    private static Logger _log = Logger.getLogger ( ClientConnection.class );
    
    private Client _client = null;
    private IOProcessor _processor = null;
    
    public ClientConnection ( IOProcessor processor )
    {
        _processor = processor;
        
        _client = new Client ( _processor, getMessageProcessor(), this, false );
        setConnection ( _client.getConnection () );
    }
    
    public void connect ( SocketAddress remote )
    {
        _client.connect ( remote );
    }
    
    public void disconnect ()
    {
        if ( _client.getConnection () != null )
            _client.getConnection ().close ();
        else
            _log.warn ( "Client has no connection!" );
    }
    
    @Override
    public void opened ()
    {
        setConnection ( _client.getConnection() );
        super.opened ();
    }

    
}
