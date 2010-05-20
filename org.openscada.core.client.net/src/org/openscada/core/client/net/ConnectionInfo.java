/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2010 inavare GmbH (http://inavare.com)
 *
 * OpenSCADA is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 3
 * only, as published by the Free Software Foundation.
 *
 * OpenSCADA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License version 3 for more details
 * (a copy is included in the LICENSE file that accompanied this code).
 *
 * You should have received a copy of the GNU Lesser General Public License
 * version 3 along with OpenSCADA. If not, see
 * <http://opensource.org/licenses/lgpl-3.0.html> for a copy of the LGPLv3 License.
 */

package org.openscada.core.client.net;

import java.net.URI;
import java.net.URISyntaxException;

public class ConnectionInfo
{
    private String _hostName = "";

    private int _port = 0;

    private boolean _autoReconnect = false;

    private int _reconnectDelay = Integer.getInteger ( "org.openscada.da.net.client.reconnect_delay", 10 * 1000 );

    public ConnectionInfo ()
    {
    }

    public ConnectionInfo ( final String hostName, final int port )
    {
        super ();
        this._hostName = hostName;
        this._port = port;
    }

    public boolean isValid ()
    {
        if ( this._hostName == null )
        {
            return false;
        }
        if ( this._hostName.equals ( "" ) )
        {
            return false;
        }

        if ( this._port <= 0 )
        {
            return false;
        }

        return true;
    }

    public boolean isAutoReconnect ()
    {
        return this._autoReconnect;
    }

    public void setAutoReconnect ( final boolean autoReconnect )
    {
        this._autoReconnect = autoReconnect;
    }

    public int getReconnectDelay ()
    {
        return this._reconnectDelay;
    }

    public void setReconnectDelay ( final int reconnectDelay )
    {
        this._reconnectDelay = reconnectDelay;
    }

    public String getHostName ()
    {
        return this._hostName;
    }

    public void setHostName ( final String hostName )
    {
        this._hostName = hostName;
    }

    public int getPort ()
    {
        return this._port;
    }

    public void setPort ( final int port )
    {
        this._port = port;
    }

    public URI toUri ()
    {

        try
        {
            return new URI ( "net", null, this._hostName, this._port, null, null, null );
        }
        catch ( final URISyntaxException e )
        {
            // TODO Auto-generated catch block
            e.printStackTrace ();
            return null;
        }

    }

    public static ConnectionInfo fromUri ( final URI uri )
    {
        final ConnectionInfo ci = new ConnectionInfo ();

        if ( !uri.getScheme ().equals ( "net" ) )
        {
            return null;
        }

        ci.setHostName ( uri.getHost () );
        ci.setPort ( uri.getPort () );

        return ci;
    }

}
