package org.openscada.net.test;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;

import org.apache.log4j.Logger;
import org.openscada.net.base.AutoReconnectClientConnection;
import org.openscada.net.base.ConnectionHandler;
import org.openscada.net.base.ConnectionHandlerBase;
import org.openscada.net.base.MessageListener;
import org.openscada.net.base.MessageProcessor;
import org.openscada.net.base.ConnectionHandlerFactory;
import org.openscada.net.base.data.Message;
import org.openscada.net.base.handlers.PingHandler;
import org.openscada.net.base.handlers.PongHandler;
import org.openscada.net.io.Client;
import org.openscada.net.io.Connection;
import org.openscada.net.io.ConnectionStateListener;
import org.openscada.net.io.IOProcessor;
import org.openscada.net.io.Server;
import org.openscada.net.utils.MessageCreator;


public class Application {
	
	private static Logger _log = Logger.getLogger(Application.class);
	
	public static void main(String[] args) {
		try {
			IOProcessor processor = new IOProcessor();
			
			Server server = new Server(new ConnectionHandlerFactory(){

				public ConnectionHandler createConnectionHandler() {
					return new ConnectionHandlerBase();
				}},1202);
			server.start();
			
			AutoReconnectClientConnection client = new AutoReconnectClientConnection ( processor, new InetSocketAddress(InetAddress.getLocalHost(),1202) );
			
			processor.run();
		}
		catch ( Exception e )
		{
			e.printStackTrace();
		}
	}
}
