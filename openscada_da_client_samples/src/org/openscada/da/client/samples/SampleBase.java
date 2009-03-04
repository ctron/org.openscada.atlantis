package org.openscada.da.client.samples;

import org.apache.log4j.Logger;
import org.openscada.core.ConnectionInformation;
import org.openscada.core.client.ConnectWaitController;
import org.openscada.core.client.ConnectionFactory;
import org.openscada.core.client.ConnectionState;
import org.openscada.core.client.ConnectionStateListener;
import org.openscada.da.client.Connection;

public class SampleBase
{

    private static Logger logger = Logger.getLogger ( SampleBase.class );

    protected String uri = null;

    protected Connection connection = null;

    public SampleBase ( final String uri, final String className ) throws Exception
    {
        super ();

        this.uri = uri;

        // If we got a class name load it
        if ( className != null )
        {
            Class.forName ( className );
        }

        if ( this.uri == null )
        {
            this.uri = "da:net://localhost:1202";
        }

        final ConnectionInformation ci = ConnectionInformation.fromURI ( this.uri );

        this.connection = (Connection)ConnectionFactory.create ( ci );
        if ( this.connection == null )
        {
            throw new Exception ( "Unable to find a connection driver for specified URI" );
        }

        this.connection.addConnectionStateListener ( new ConnectionStateListener () {

            public void stateChange ( final org.openscada.core.client.Connection connection, final ConnectionState state, final Throwable error )
            {
                logger.info ( "Connection state changed: " + state, error );
            }
        } );

    }

    public void connect () throws Exception
    {
        // trigger the connection
        try
        {
            // wait until the connection is established. If it already is the call
            // will return immediately.
            // If the connect attempt fails an exception is thrown.
            new ConnectWaitController ( this.connection ).connect ();
        }
        catch ( final Throwable e )
        {
            // we were unlucky
            throw new Exception ( "Unable to create connection", e );
        }
    }

    public void disconnect ()
    {
        this.connection.disconnect ();
        this.connection = null;
        System.gc ();
    }

    public static void sleep ( final long millis )
    {
        try
        {
            Thread.sleep ( millis );
        }
        catch ( final Throwable e )
        {
            e.printStackTrace ();
        }
    }

}