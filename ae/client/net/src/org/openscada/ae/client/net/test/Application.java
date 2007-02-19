package org.openscada.ae.client.net.test;

import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.openscada.ae.client.net.Connection;
import org.openscada.ae.core.QueryDescription;
import org.openscada.core.OperationException;
import org.openscada.core.Variant;
import org.openscada.core.client.ConnectWaitController;
import org.openscada.core.client.net.ConnectionInfo;

public class Application
{
    private static Logger _log = Logger.getLogger ( Application.class );
    
    public static void list ( Connection connection ) throws InterruptedException, OperationException
    {
        Set<QueryDescription> queries = connection.list ();
        System.out.println ( "Listing queries:" );
        for ( QueryDescription description : queries )
        {
            System.out.println ( description.getId () );
            for ( Map.Entry<String, Variant> entry : description.getAttributes ().entrySet () )
            {
                System.out.println ( "\t'" + entry.getKey () + "'=>'" + entry.getValue ().asString ( "<null>" ) + "'" );
            }
        }
    }
    
    public static void main ( String[] args ) throws Throwable
    {
        ConnectionInfo ci = new ConnectionInfo ();
        ci.setAutoReconnect ( false );
        ci.setHostName ( "localhost" );
        ci.setPort ( 1302 );
        
        Connection connection = new Connection ( ci );
        _log.debug ( "Initiating connection..." );
        new ConnectWaitController ( connection ).connect ();
        _log.debug ( "Connection established" );
        
        list ( connection );
        
        connection.subscribe ( "all", new DumpListener (), 10, 10 );
        
        while ( true )
        {
            Thread.sleep ( 1000 );
        }
    }
}
