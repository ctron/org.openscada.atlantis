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

package org.openscada.da.server.snmp;

public class ConnectionInformation implements Cloneable
{
    public enum Version
    {
        V1,
        V2C,
        V3,
    };

    private Version _version = Version.V2C;

    private String _name = null;

    private String _address = null;

    private String _community = null;

    public ConnectionInformation ( final Version version, final String name )
    {
        this._version = version;
        this._name = name;
    }

    public ConnectionInformation ( final ConnectionInformation arg0 )
    {
        if ( arg0._address != null )
        {
            this._address = new String ( arg0._address );
        }
        if ( arg0._name != null )
        {
            this._name = new String ( arg0._name );
        }
        if ( arg0._version != null )
        {
            this._version = arg0._version;
        }

        if ( arg0._community != null )
        {
            this._community = new String ( arg0._community );
        }
    }

    public String getAddress ()
    {
        return this._address;
    }

    public void setAddress ( final String address )
    {
        this._address = address;
    }

    @Override
    public Object clone ()
    {
        return new ConnectionInformation ( this );
    }

    public String getName ()
    {
        return this._name;
    }

    public void setName ( final String name )
    {
        this._name = name;
    }

    public String getCommunity ()
    {
        return this._community;
    }

    public void setCommunity ( final String community )
    {
        this._community = community;
    }

    public Version getVersion ()
    {
        return this._version;
    }

    public void setVersion ( final Version version )
    {
        this._version = version;
    }
}
