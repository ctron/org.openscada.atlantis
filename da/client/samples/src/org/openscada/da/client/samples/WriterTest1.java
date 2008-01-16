package org.openscada.da.client.samples;

import java.util.Calendar;
import java.util.Random;

import org.apache.log4j.Logger;
import org.openscada.core.ConnectionInformation;
import org.openscada.core.Variant;
import org.openscada.core.client.ConnectionFactory;
import org.openscada.da.client.Connection;
import org.openscada.da.client.WriteOperationCallback;

public class WriterTest1
{
    private static Logger logger = Logger.getLogger ( WriterTest1.class );
    
    public static void main ( String[] args ) throws ClassNotFoundException
    {
        final String className = "org.openscada.da.client.net.Connection";
        final String uri = "da:net://127.0.0.1:1202?auto-reconnect=true";
        final String itemName = "172.16.148.128:F8582CF2-88FB-11D0-B850-00C0F0104305.Bucket Brigade.Int4";

        if ( className != null )
        {
            Class.forName ( className );
        }

        ConnectionInformation ci = ConnectionInformation.fromURI ( uri );

        final Connection connection = (Connection)ConnectionFactory.create ( ci );
        if ( connection == null )
        {
            throw new RuntimeException ( "Unable to find a connection driver for specified URI" );
        }

        connection.connect ();

        new Thread ( new Runnable () {

            public void run ()
            {
                while ( true )
                {
                    try
                    {
                        Thread.sleep ( 1000 );
                    }
                    catch ( InterruptedException e )
                    {
                        return;
                    }
                    doWrite ( connection, itemName );
                }
            }
        } ).start ();
    }

    private static Random random = new Random ();
    
    public static void doWrite ( Connection connection, String itemName )
    {
        final Variant value = new Variant ( random.nextInt () );
        logger.info ( "Start write: " + value );
        final Calendar c = Calendar.getInstance ();
        connection.write ( itemName, value, new WriteOperationCallback () {

            public void complete ()
            {
                logger.info ( String.format ( "Wrote: %s, Started: %tc", value, c ) );
            }

            public void error ( Throwable e )
            {
                logger.info ( "Error", e );
            }

            public void failed ( String error )
            {
                logger.info ( "Failed: " + error );
            }} );
    }
}
