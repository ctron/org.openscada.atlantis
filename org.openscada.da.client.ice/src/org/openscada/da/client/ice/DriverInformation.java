/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2009 inavare GmbH (http://inavare.com)
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

package org.openscada.da.client.ice;

import org.openscada.core.ConnectionInformation;
import org.openscada.core.client.Connection;

public class DriverInformation implements org.openscada.core.client.DriverInformation
{

    public Connection create ( final ConnectionInformation connectionInformation )
    {
        return new org.openscada.da.client.ice.Connection ( connectionInformation );
    }

    public Class<?> getConnectionClass ()
    {
        return org.openscada.da.client.ice.Connection.class;
    }

    public void validate ( final ConnectionInformation connectionInformation ) throws Throwable
    {
        final String str = connectionInformation.getProperties ().get ( connectionInformation.getTarget () );
        if ( str == null )
        {
            throw new Exception ( String.format ( "Property with key of target is missing (target: '%s')", connectionInformation.getTarget () ) );
        }
    }

}
