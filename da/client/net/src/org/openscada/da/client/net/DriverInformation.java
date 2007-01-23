/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2007 inavare GmbH (http://inavare.com)
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

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

    public void validate ( ConnectionInformation connectionInformation ) throws Throwable
    {
    }

}
