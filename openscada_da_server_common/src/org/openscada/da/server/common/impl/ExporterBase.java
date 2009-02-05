/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2008 inavare GmbH (http://inavare.com)
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

import java.io.IOException;

import org.openscada.da.core.server.Hive;

public class ExporterBase
{
    protected Hive _hive = null;

    public ExporterBase ( final Hive hive ) throws IOException
    {
        this._hive = hive;
    }

    public ExporterBase ( final Class<?> hiveClass ) throws InstantiationException, IllegalAccessException, IOException
    {
        this ( createInstance ( hiveClass ) );
    }

    public ExporterBase ( final String hiveClassName ) throws ClassNotFoundException, InstantiationException, IllegalAccessException, IOException
    {
        this ( createInstance ( Class.forName ( hiveClassName ) ) );
    }

    private static Hive createInstance ( final Class<?> hiveClass ) throws InstantiationException, IllegalAccessException
    {
        return (Hive)hiveClass.newInstance ();
    }

    public Class<?> getHiveClass ()
    {
        return this._hive.getClass ();
    }

}
