package org.openscada.io.lcl.test;

import java.io.IOException;
import java.net.InetSocketAddress;

import org.openscada.io.lcl.AutoReconnectClient;
import org.openscada.io.lcl.Client;
import org.openscada.net.io.IOProcessor;

public class ClientApplication
{
    public static void main ( String[] args ) throws IOException
    {
        IOProcessor processor = new IOProcessor ();
        
        @SuppressWarnings("unused")
        Client client = new AutoReconnectClient (
                processor,
                new InetSocketAddress ( "localhost", 1202 ),
                new TestClientHandler () );
        
        processor.run ();
    }
}
