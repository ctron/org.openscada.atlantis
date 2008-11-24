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

package org.openscada.da.server.jdbc;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Collection;
import java.util.LinkedList;

import org.apache.log4j.Logger;
import org.openscada.utils.timing.Scheduler;

public class Connection
{
    private static Logger logger = Logger.getLogger ( Connection.class );

    private final Collection<Query> queries = new LinkedList<Query> ();

    private Throwable globalError = null;

    private final String username;

    private final String password;

    private final String uri;

    private java.sql.Connection connection;

    public Connection ( final String connectionClass, final String uri, final String username, final String password )
    {
        this.uri = uri;
        this.username = username;
        this.password = password;
        try
        {
            if ( connectionClass != null )
            {
                Class.forName ( connectionClass );
            }
        }
        catch ( final Throwable e )
        {
            logger.error ( "Failed to initialize connection", e );
            this.globalError = e;
        }
    }

    public void add ( final Query query )
    {
        this.queries.add ( query );
    }

    public void register ( final Hive hive, final Scheduler scheduler )
    {
        for ( final Query query : this.queries )
        {
            query.register ( scheduler );
        }
    }

    public void unregister ( final Hive hive )
    {
        for ( final Query query : this.queries )
        {
            query.unregister ();
        }
    }

    protected java.sql.Connection createConnection () throws SQLException
    {
        return DriverManager.getConnection ( this.uri, this.username, this.password );
    }

    public java.sql.Connection getConnection () throws SQLException
    {
        if ( this.connection == null )
        {
            this.connection = createConnection ();
        }

        return this.connection;
    }
}
