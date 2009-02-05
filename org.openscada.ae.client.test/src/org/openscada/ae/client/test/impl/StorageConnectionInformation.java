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

package org.openscada.ae.client.test.impl;

import java.io.Serializable;

public class StorageConnectionInformation implements Serializable
{

    
    /**
     * 
     */
    private static final long serialVersionUID = 46012900227961405L;
    
    
    private String _host;
    private int _port;
    private boolean _autoReconnect;
    
    public StorageConnectionInformation ()
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
}
