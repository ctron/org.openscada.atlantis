package org.openscada.net.io;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

import org.openscada.net.base.MessageListener;
import org.openscada.net.base.MessageProcessor;
import org.openscada.net.base.ConnectionHandlerFactory;

public class Server implements Runnable {

	private IOProcessor _processor = null;
	private ServerSocket _serverSocket = null;
	
	public Server ( ConnectionHandlerFactory factory, int port ) throws IOException
	{
		this ( factory, new IOProcessor(), port );
	}
    
    public Server ( ConnectionHandlerFactory factory, IOProcessor processor, int port  ) throws IOException
    {
        _processor = processor;
        
        _serverSocket = new ServerSocket(_processor, new InetSocketAddress(port), factory);
    }
	
	public void start ()
	{
		_processor.start();
	}
	
	public void run ()
	{
		_processor.run();
	}
}
