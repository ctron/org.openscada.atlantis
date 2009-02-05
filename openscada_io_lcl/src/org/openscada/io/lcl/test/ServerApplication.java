package org.openscada.io.lcl.test;

import java.io.IOException;

import org.openscada.io.lcl.ServerLineHandler;
import org.openscada.net.io.IOProcessor;
import org.openscada.net.line.LineBasedServer;
import org.openscada.net.line.LineHandler;
import org.openscada.net.line.LineBasedServer.HandlerFactory;

public class ServerApplication
{
    public static void main ( String[] args ) throws IOException
    {
        IOProcessor processor = new IOProcessor ();
        
        @SuppressWarnings("unused")
        LineBasedServer server = new LineBasedServer ( processor, 1202, new HandlerFactory () {

            public LineHandler createHandler ()
            {
                return new ServerLineHandler ( new TestServerHandler () );
            }} );
        
        processor.run ();
    }
}
