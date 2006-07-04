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

package org.openscada.da.server.snmp;

public class ConnectionInformation implements Cloneable
{
    private String _name = null;
    private String _address = null;
    
    private String _community = null;
   
    public ConnectionInformation ( String name )
    {
        _name = name;
    }
    
    public ConnectionInformation ( ConnectionInformation arg0 )
    {
        if ( arg0._address != null )
            _address = new String ( arg0._address );
        if ( arg0._name != null )
            _name = new String ( arg0._name );
        
        if ( arg0._community != null )
            _community = new String ( arg0._community );
    }

    public String getAddress ()
    {
        return _address;
    }

    public void setAddress ( String address )
    {
        _address = address;
    }
    
    @Override
    public Object clone ()
    {
        return new ConnectionInformation ( this ); 
    }

    public String getName ()
    {
        return _name;
    }

    public void setName ( String name )
    {
        _name = name;
    }

    public String getCommunity ()
    {
        return _community;
    }

    public void setCommunity ( String community )
    {
        _community = community;
    }
}
