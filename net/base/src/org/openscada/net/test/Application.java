/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006 inavare GmbH (http://inavare.com)
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package org.openscada.net.test;

import java.net.InetAddress;
import java.net.InetSocketAddress;

import org.apache.log4j.Logger;
import org.openscada.net.base.AutoReconnectClientConnection;
import org.openscada.net.base.ConnectionHandler;
import org.openscada.net.base.ConnectionHandlerBase;
import org.openscada.net.base.ConnectionHandlerFactory;
import org.openscada.net.base.data.DoubleValue;
import org.openscada.net.base.data.IntegerValue;
import org.openscada.net.base.data.ListValue;
import org.openscada.net.base.data.LongValue;
import org.openscada.net.base.data.Message;
import org.openscada.net.base.data.StringValue;
import org.openscada.net.base.data.VoidValue;
import org.openscada.net.io.IOProcessor;
import org.openscada.net.io.net.Connection;
import org.openscada.net.io.net.Server;


public class Application {
	
	@SuppressWarnings("unused")
    private static Logger _log = Logger.getLogger(Application.class);
	
    public static void sendTestMessage ( Connection connection )
    {
        Message message = new Message ();
        message.getValues ().put ( "test", new StringValue ( "nür ün tüst") );
        
        ListValue listValue = new ListValue ();
        listValue.add ( new StringValue ( "test1" ) );
        listValue.add ( new StringValue ( "test2" ) );
        listValue.add ( new StringValue ( "test3" ) );
        message.getValues ().put ( "list", listValue );
        
        message.getValues ().put ( "int", new IntegerValue ( 1202 ) );
        message.getValues ().put ( "long", new LongValue ( 0x0101DEADBEEF0202L ) );
        message.getValues ().put ( "double", new DoubleValue ( 123.456 ) );
        message.getValues ().put ( "void", new VoidValue () );
        
        connection.sendMessage ( message );   
    }
    
	public static void main(String[] args) {
		try {
			IOProcessor processor = new IOProcessor();
			
			Server server = new Server(new ConnectionHandlerFactory(){

				public ConnectionHandler createConnectionHandler() {
					return new ConnectionHandlerBase() {
					    @Override
					    public void opened ()
					    {
					        super.opened ();
                            sendTestMessage ( getConnection () );
					    }
                    };
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
