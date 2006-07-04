/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006 inavare GmbH (http://inavare.com)
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


public class ConnectionInfo
{
    private String _hostName = "";
    private int _port = 0;
    private boolean _autoReconnect = true;
    
    private int _reconnectDelay = Integer.getInteger ( "org.openscada.da.net.client.reconnect_delay", 10 * 1000 );

    public ConnectionInfo ()
    {
    }
    
    public ConnectionInfo ( String hostName, int port )
    {
        super ();
        _hostName = hostName;
        _port = port;
    }
    
    public boolean isValid ()
    {
        if ( _hostName == null )
            return false;
        if ( _hostName.equals ( "" ) )
            return false;
        
        if ( _port <= 0 )
            return false;
        
        return true;
    }

    public boolean isAutoReconnect ()
    {
        return _autoReconnect;
    }

    public void setAutoReconnect ( boolean autoReconnect )
    {
        _autoReconnect = autoReconnect;
    }

    public int getReconnectDelay ()
    {
        return _reconnectDelay;
    }

    public void setReconnectDelay ( int reconnectDelay )
    {
        _reconnectDelay = reconnectDelay;
    }

    public String getHostName ()
    {
        return _hostName;
    }

    public void setHostName ( String hostName )
    {
        _hostName = hostName;
    }

    public int getPort ()
    {
        return _port;
    }

    public void setPort ( int port )
    {
        _port = port;
    }
    
}
