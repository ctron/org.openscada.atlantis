package org.openscada.da.client.net;

import org.openscada.core.client.Connection;
import org.openscada.core.client.ConnectionInformation;
import org.openscada.core.client.net.ConnectionInfo;

public class DriverInformation implements org.openscada.core.client.DriverInformation
{

    public Connection create ( ConnectionInformation connectionInformation )
    {
        if ( connectionInformation.getSecondaryTarget () == null )
            return null;
        
        ConnectionInfo ci = new ConnectionInfo ();
        
        ci.setHostName ( connectionInformation.getTarget () );
        ci.setPort ( connectionInformation.getSecondaryTarget ().intValue () );
        
        // auto-reconnect
        String autoReconnect = connectionInformation.getProperties ().get ( "auto-reconnect" );
        if ( autoReconnect != null )
        {
            try
            {
                ci.setAutoReconnect ( Boolean.valueOf ( autoReconnect ) );
            }
            catch ( Exception e )
            {}
        }
        
        // reconnect-delay
        String reconnectDelay = connectionInformation.getProperties ().get ( "reconnect-delay" );
        if ( reconnectDelay != null )
        {
            try
            {
                ci.setReconnectDelay ( Integer.valueOf ( reconnectDelay ) );
            }
            catch ( Exception e )
            {}
        }
        
        return new org.openscada.da.client.net.Connection ( ci );
    }

    public Class getConnectionClass ()
    {
        return org.openscada.da.client.net.Connection.class;
    }

}
