package org.openscada.net.base;

import java.net.SocketAddress;

import org.apache.log4j.Logger;
import org.openscada.net.io.Client;
import org.openscada.net.io.IOProcessor;

public class ClientConnection extends ConnectionHandlerBase
{
	private static Logger _log = Logger.getLogger(ClientConnection.class);
	
	private Client _client = null;
    
	public ClientConnection ( IOProcessor processor, SocketAddress remote )
	{
	    _client = new Client ( processor, getMessageProcessor(), this, remote );
	    setConnection ( _client.getConnection() );
	    _client.connect ();
	}
	
	@Override
	public void opened ()
	{
	    setConnection ( _client.getConnection() );
	    super.opened ();
	}
}
