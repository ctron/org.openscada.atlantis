/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006 inavare GmbH (http://inavare.com)
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package org.openscada.da.client.test.config;

import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;

public class HiveConnectionInformation implements Serializable
{
    /**
     * Serializeable ID 
     */
    private static final long serialVersionUID = 4226848319802862860L;
    
    private String _host;
    private int _port;
    private boolean _autoReconnect;
    
    public HiveConnectionInformation ()
    {
        _host = "";
        _port = 0;
        _autoReconnect = false;
    }
    
    public String getHost ()
    {
        return _host;
    }
    
    public void setHost ( String host )
    {
        _host = host;
    }
    
    public int getPort ()
    {
        return _port;
    }
    
    public void setPort ( int port )
    {
        _port = port;
    }

    public boolean isAutoReconnect ()
    {
        return _autoReconnect;
    }

    public void setAutoReconnect ( boolean autoReconnect )
    {
        _autoReconnect = autoReconnect;
    }
    
    public URI toUri ()
    {
        
            try
            {
                return new URI ( "net", null, _host, _port, null, null, null );
            }
            catch ( URISyntaxException e )
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return null;
            }
        
    }
    
    public static HiveConnectionInformation fromUri ( URI uri )
    {
        HiveConnectionInformation ci =  new HiveConnectionInformation ();
        
        if ( !uri.getScheme ().equals ( "net" ) )
            return null;
        
        ci.setHost ( uri.getHost () );
        ci.setPort ( uri.getPort () );
        
        return ci;
    }

    @Override
    public int hashCode ()
    {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + ( ( _host == null ) ? 0 : _host.hashCode () );
        result = PRIME * result + _port;
        return result;
    }

    @Override
    public boolean equals ( Object obj )
    {
        if ( this == obj )
            return true;
        if ( obj == null )
            return false;
        if ( getClass () != obj.getClass () )
            return false;
        final HiveConnectionInformation other = (HiveConnectionInformation)obj;
        if ( _host == null )
        {
            if ( other._host != null )
                return false;
        }
        else
            if ( !_host.equals ( other._host ) )
                return false;
        if ( _port != other._port )
            return false;
        return true;
    }
}
