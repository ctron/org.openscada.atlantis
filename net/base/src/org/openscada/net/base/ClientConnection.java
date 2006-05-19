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
    private SocketAddress _remote = null;
    
    public ClientConnection ( IOProcessor processor, SocketAddress remote )
    {
        _processor = processor;
        _remote = remote;
        
        _client = new Client ( _processor, getMessageProcessor(), this, _remote, false );
        setConnection ( _client.getConnection () );
    }
    
    public void connect ()
    {
        _client.connect ();
    }
    
    public void disconnect ()
    {
        _client.getConnection ().close ();
    }
    
    @Override
    public void opened ()
    {
        setConnection ( _client.getConnection() );
        super.opened ();
    }

    
}
