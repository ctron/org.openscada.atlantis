package org.openscada.da.net.client;

public class ConnectorLoader implements org.openscada.da.client.connector.ConnectorLoader
{

    public void load ()
    {
        try
        {
            Class.forName ( "org.openscada.da.client.net.Connection" );
        }
        catch ( final ClassNotFoundException e )
        {
            // TODO Auto-generated catch block
            e.printStackTrace ();
        }
    }

}
