package org.openscada.da.client.ice;

public class ConnectorLoader implements org.openscada.rcp.da.client.ConnectorLoader
{

    public void load ()
    {
        try
        {
            Class.forName ( "org.openscada.da.client.ice.Connection" );
        }
        catch ( ClassNotFoundException e )
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
