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

package org.openscada.da.server.common.impl;

import org.openscada.core.ConnectionInformation;
import org.openscada.da.core.server.Hive;

public abstract class ExporterBase implements HiveExporter
{
    protected Hive hive = null;

    protected ConnectionInformation connectionInformation;

    public ExporterBase ( final Hive hive, final ConnectionInformation connectionInformation ) throws Exception
    {
        this.hive = hive;
        this.connectionInformation = connectionInformation;
    }

    @Deprecated
    public ExporterBase ( final Class<?> hiveClass, final ConnectionInformation connectionInformation ) throws Exception
    {
        this ( createInstance ( hiveClass ), connectionInformation );
    }

    @Deprecated
    public ExporterBase ( final String hiveClassName, final ConnectionInformation connectionInformation ) throws Exception
    {
        this ( createInstance ( Class.forName ( hiveClassName ) ), connectionInformation );
    }

    @Deprecated
    private static Hive createInstance ( final Class<?> hiveClass ) throws Exception
    {
        return (Hive)hiveClass.newInstance ();
    }

    public Class<?> getHiveClass ()
    {
        return this.hive.getClass ();
    }

    public ConnectionInformation getConnectionInformation ()
    {
        return this.connectionInformation;
    }
}
