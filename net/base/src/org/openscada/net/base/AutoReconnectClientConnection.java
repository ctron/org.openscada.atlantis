package org.openscada.net.base;

import java.net.SocketAddress;

import org.apache.log4j.Logger;
import org.openscada.net.io.Client;
import org.openscada.net.io.IOProcessor;

public class AutoReconnectClientConnection extends ConnectionHandlerBase
{
	private static Logger _log = Logger.getLogger ( AutoReconnectClientConnection.class );
	
	private Client _client = null;
    private IOProcessor _processor = null;
    private SocketAddress _remote = null;
    
	public AutoReconnectClientConnection ( IOProcessor processor, SocketAddress remote )
	{
        _processor = processor;
	    _remote = remote;   
	}
    
    /**
     * start connecting to the server
     *
     */
    public void start ()
    {
        _client = new Client ( _processor, getMessageProcessor(), this, true );
        setConnection ( _client.getConnection () );
        _client.connect ( _remote );
    }
	
	@Override
	public void opened ()
	{
	    setConnection ( _client.getConnection() );
	    super.opened ();
	}
}
