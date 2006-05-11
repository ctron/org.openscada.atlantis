package org.openscada.da.client.net.test;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import org.apache.log4j.Logger;
import org.openscada.da.client.net.Connection;
import org.openscada.da.client.net.ConnectionInfo;
import org.openscada.da.client.net.ConnectionStateListener;
import org.openscada.da.client.net.ItemUpdateListener;
import org.openscada.da.core.data.NullValueException;
import org.openscada.da.core.data.Variant;
import org.openscada.net.io.IOProcessor;

public class Application
{
    private static Logger _log = Logger.getLogger(Application.class);
    
    public static void main ( String[] args ) throws IOException
    {

        
        ConnectionInfo info = new ConnectionInfo();
        info.setRemote(new InetSocketAddress(
                InetAddress.getLocalHost(),
                Integer.getInteger ( "openscada.da.net.server.port", 1202 )
                ));
        
        final Connection connection = new Connection ( info );
        
        connection.addConnectionStateListener(new ConnectionStateListener(){

            public void connected ( Connection connection )
            {
                _log.info ( "Connection established" );
            }

            public void disconnected ( Connection connection )
            {
                _log.debug ( "Connection lost" );
                
            }});
        
        connection.getItemList().addObserver(new Observer(){

            public void update ( Observable o, Object arg )
            {
                Collection<String> items = connection.getItemList().getItemList();
                _log.debug("START - Item list");
                for ( String item : items )
                {
                    _log.debug(" - " + item);
                }
                _log.debug("END - Item list");
            }});
        
        connection.addItemUpdateListener("time", true, new ItemUpdateListener(){

            public void notifyValueChange ( Variant value, boolean initial )
            {
                try
                {
                    _log.debug("Value changed: " + value.asString() );
                }
                catch ( NullValueException e )
                {
                   _log.debug("Value changed to null!");
                }
                
            }

            public void notifyAttributeChange ( Map<String, Variant> attributes, boolean initial )
            {
                // TODO Auto-generated method stub
                
            }});
        
        connection.start();
        
        while ( true )
        {
            try
            {
                Thread.sleep(1000);
            }
            catch ( InterruptedException e )
            {
            }
        }
    }
}
