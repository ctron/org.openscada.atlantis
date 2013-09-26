/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2010 TH4 SYSTEMS GmbH (http://th4-systems.com)
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

package org.openscada.da.server.common.impl;

import org.eclipse.scada.core.ConnectionInformation;
import org.eclipse.scada.da.core.server.Hive;

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
