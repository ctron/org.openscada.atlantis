package org.openscada.net.test;

import java.io.IOException;
import java.net.InetSocketAddress;

import org.openscada.net.io.IOProcessor;
import org.openscada.net.line.LineBasedClient;
import org.openscada.net.line.LineBasedServer;
import org.openscada.net.line.LineHandler;

public class LineApplication
{
    public static void main ( String[] args ) throws IOException
    {
        IOProcessor io = new IOProcessor ();
        new LineBasedServer ( io, 1202, new LineBasedServer.HandlerFactory () {

            public LineHandler createHandler ()
            {
                return new TestLineHandler ( true );
            }} ); 
        
        final LineBasedClient client = new LineBasedClient ( io, new TestLineHandler ( false ) );
        
        io.getScheduler ().executeJobAsync ( new Runnable () {

            public void run ()
            {
                client.connect ( new InetSocketAddress ( "localhost", 1202 ) );
            }} );
        
        io.run ();
    }
}
