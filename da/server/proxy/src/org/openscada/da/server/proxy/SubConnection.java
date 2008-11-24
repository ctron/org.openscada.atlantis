/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2008 inavare GmbH (http://inavare.com)
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

package org.openscada.da.server.proxy;

import org.openscada.da.client.Connection;

/**
 * @author Juergen Rose &lt;juergen.rose@inavare.net&gt;
 */
public class SubConnection
{
    private final Connection connection;

    private final String id;

    private final String prefix;

    public SubConnection ( final Connection connection, final String id, final String prefix )
    {
        super ();
        this.connection = connection;
        this.id = id;
        this.prefix = prefix;
    }

    public Connection getConnection ()
    {
        return this.connection;
    }

    public String getId ()
    {
        return this.id;
    }

    public String getPrefix ()
    {
        return this.prefix;
    }
}
