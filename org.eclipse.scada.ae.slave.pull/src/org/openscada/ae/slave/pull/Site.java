/*
 * This file is part of the openSCADA project
 * Copyright (C) 2011-2012 TH4 SYSTEMS GmbH (http://th4-systems.com)
 *
 * openSCADA is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 3
 * only, as published by the Free Software Foundation.
 *
 * openSCADA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License version 3 for more details
 * (a copy is included in the LICENSE file that accompanied this code).
 *
 * You should have received a copy of the GNU Lesser General Public License
 * version 3 along with openSCADA. If not, see
 * <http://opensource.org/licenses/lgpl-3.0.html> for a copy of the LGPLv3 License.
 */

package org.openscada.ae.slave.pull;

import java.util.Properties;

public class Site
{
    private final String id;

    private final String driverName;

    private final Properties properties;

    private final long delay;

    private long lastProcess;

    private final String schema;

    private final String customSelectSql;

    private final String customDeleteSql;

    public Site ( final String id, final String driverName, final Properties properties, final String schema, final long delay, final String customSelectSql, final String customDeleteSql )
    {
        this.id = id;
        this.driverName = driverName;
        this.properties = properties;
        this.schema = schema;
        this.delay = delay;
        this.customSelectSql = customSelectSql;
        this.customDeleteSql = customDeleteSql;
    }

    public String getCustomDeleteSql ()
    {
        return this.customDeleteSql;
    }

    public String getCustomSelectSql ()
    {
        return this.customSelectSql;
    }

    public String getSchema ()
    {
        return this.schema;
    }

    public long nextStart ()
    {
        return this.lastProcess + this.delay;
    }

    public boolean isDue ()
    {
        return System.currentTimeMillis () - this.lastProcess > this.delay;
    }

    public void markProcessed ()
    {
        this.lastProcess = System.currentTimeMillis ();
    }

    public void setLastProcess ( final long lastProcess )
    {
        this.lastProcess = lastProcess;
    }

    public long getLastProcess ()
    {
        return this.lastProcess;
    }

    public String getId ()
    {
        return this.id;
    }

    public String getDriverName ()
    {
        return this.driverName;
    }

    public Properties getProperties ()
    {
        return this.properties;
    }

    @Override
    public int hashCode ()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ( this.id == null ? 0 : this.id.hashCode () );
        return result;
    }

    @Override
    public boolean equals ( final Object obj )
    {
        if ( this == obj )
        {
            return true;
        }
        if ( obj == null )
        {
            return false;
        }
        if ( getClass () != obj.getClass () )
        {
            return false;
        }
        final Site other = (Site)obj;
        if ( this.id == null )
        {
            if ( other.id != null )
            {
                return false;
            }
        }
        else if ( !this.id.equals ( other.id ) )
        {
            return false;
        }
        return true;
    }
}