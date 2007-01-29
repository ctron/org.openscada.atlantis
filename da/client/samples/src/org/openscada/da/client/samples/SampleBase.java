package org.openscada.da.client.samples;

import org.openscada.core.ConnectionInformation;
import org.openscada.core.client.ConnectionFactory;
import org.openscada.da.client.Connection;

public class SampleBase
{
    protected String _uri = null;
    protected Connection _connection = null;

    public SampleBase ( String uri, String className ) throws ClassNotFoundException
    {
        super ();

        _uri = uri;
        
        // If we got a class name load it
        if ( className != null )
            Class.forName ( className );

        if ( _uri == null )
            _uri = "da:net://localhost:1202";

    }

    public void connect () throws Exception
    {
        ConnectionInformation ci = ConnectionInformation.fromURI ( _uri );
        
        _connection = (Connection)ConnectionFactory.create ( ci );
        if ( _connection == null )
            throw new Exception ( "Unable to find a connection driver for specified URI" );
        
        // trigger the connection
        _connection.connect ();
        try
        {
            // wait until the connection is established. If it already is the call
            // will return immediately.
            // If the connect attempt fails an exception is thrown.
            _connection.waitForConnection ();
        }
        catch ( Throwable e )
        {
            // we were unlucky
            throw new Exception ( "Unable to create connection", e );
        }
    }

    public void disconnect ()
    {
        _connection.disconnect ();
    }

}