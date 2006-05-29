package org.openscada.da.client.net.test;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import org.apache.log4j.Logger;
import org.openscada.da.client.net.Connection;
import org.openscada.da.client.net.ConnectionInfo;
import org.openscada.da.client.net.ConnectionStateListener;
import org.openscada.da.client.net.ItemUpdateListener;
import org.openscada.da.client.net.Connection.State;
import org.openscada.da.core.DataItemInformation;
import org.openscada.da.core.IODirection;
import org.openscada.da.core.data.NullValueException;
import org.openscada.da.core.data.Variant;
import org.openscada.utils.timing.Scheduler;

public class Application
{
    private static Logger _log = Logger.getLogger ( Application.class );
    
    public static void main ( String[] args ) throws IOException
    {
   
        ConnectionInfo info = new ConnectionInfo();
        info.setRemote(new InetSocketAddress(
                InetAddress.getLocalHost(),
                Integer.getInteger ( "openscada.da.net.server.port", 1202 )
                ));
        
        final Connection connection = new Connection ( info );
        
        connection.addConnectionStateListener(new ConnectionStateListener() {

            public void stateChange ( Connection connection, State state, Throwable error )
            {
                if ( error != null )
                    _log.warn ( "State changed to: " + state.toString () + " (" + error.getMessage () + ")" );
                else
                    _log.info ( "State changed to: " + state.toString () );
            }

            });
        
        connection.getItemList().addObserver(new Observer(){

            public void update ( Observable o, Object arg )
            {
                Collection<DataItemInformation> items = connection.getItemList().getItemList();
                _log.debug("START - Item list");
                for ( DataItemInformation item : items )
                {
                    String io = "";
                    if ( item.getIODirection ().contains ( IODirection.INPUT ) )
                        io += "I";
                    if ( item.getIODirection ().contains ( IODirection.OUTPUT ) )
                        io += "O";
                    _log.debug ( " - " + item.getName () + " " + io );
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
        
        connection.addItemUpdateListener("memory", true, new ItemUpdateListener(){

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
        
        // add a job for writing
        Scheduler scheduler = new Scheduler();
        scheduler.addJob ( new Runnable(){

            public void run ()
            {
                try
                {
                    _log.debug ( "Writing..." );
                    connection.write ( "memory", new Variant("Test: " + System.currentTimeMillis()) );
                    connection.write ( "command", new Variant(System.currentTimeMillis()) );
                    _log.debug ( "Writing...complete!" );
                }
                catch ( Exception e )
                {
                    e.printStackTrace();
                }
            }}, 10*1000, true );
        
        connection.connect ();
        
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
