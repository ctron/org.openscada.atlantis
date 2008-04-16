package org.openscada.io.lcl;

import java.io.IOException;
import java.net.SocketAddress;

import org.apache.log4j.Logger;
import org.openscada.net.io.IOProcessor;

public class AutoReconnectClient extends Client
{
    private static Logger _log = Logger.getLogger ( AutoReconnectClient.class );
    
    private static int _reconnectDelay = Integer.getInteger ( "openscada.io.lcl.reconnectDelay", 5 * 1000 );
    
    private IOProcessor _processor = null;
    private SocketAddress _remote = null;
    
    private boolean _scheduled = false;
        
    public AutoReconnectClient ( IOProcessor processor, SocketAddress remote, ClientHandler handler ) throws IOException
    {
        super ( processor, handler );
        _remote = remote;
        
        _processor = processor;
        
        scheduleReconnect ( 0 );
    }
    
    @Override
    public void closed ()
    {
        super.closed ();
        scheduleReconnect ( _reconnectDelay );
    }
    
    @Override
    public void connectionFailed ( Throwable throwable )
    {
        super.connectionFailed ( throwable );
        scheduleReconnect ( _reconnectDelay );
    }
    
    private void processReconnect ()
    {
        _log.info ( "Re-connect" );
        
        connect ( _remote );
    }
    
    synchronized private void scheduleReconnect ( int delay )
    {
        if ( _scheduled )
            return;
        
        _log.info ( String.format ( "Scheduled re-connect in %d milli-seconds", delay ) );
        
        _scheduled = true;
        
        _processor.getScheduler ().scheduleJob ( new Runnable () {

            public void run ()
            {
                _scheduled = false;
                processReconnect ();
            }},
            delay );
    }

}
