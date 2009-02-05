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
import java.util.Timer;

import org.apache.log4j.Logger;
import org.openscada.da.server.browser.common.FolderCommon;

public class Connection
{
    private static Logger logger = Logger.getLogger ( Connection.class );

    private final Collection<Query> queries = new LinkedList<Query> ();

    private final String username;

    private final String password;

    private final String uri;

    private final String id;

    private DataItemFactory itemFactory;

    private final Integer timeout;

    public Connection ( final String id, final Integer timeout, final String connectionClass, final String uri, final String username, final String password )
    {
        this.uri = uri;
        this.username = username;
        this.password = password;
        this.id = id;
        this.timeout = timeout;
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
        }
    }

    public void add ( final Query query )
    {
        this.queries.add ( query );
    }

    public void register ( final Hive hive, final FolderCommon rootFolder, final Timer timer )
    {
        this.itemFactory = new DataItemFactory ( hive, rootFolder, this.id, this.id );

        for ( final Query query : this.queries )
        {
            query.register ( timer, this.itemFactory );
        }
    }

    public void unregister ( final Hive hive )
    {
        for ( final Query query : this.queries )
        {
            query.unregister ();
        }

        this.itemFactory.dispose ();
        this.itemFactory = null;
    }

    protected java.sql.Connection createConnection () throws SQLException
    {
        if ( this.timeout != null )
        {
            DriverManager.setLoginTimeout ( this.timeout / 1000 );
        }

        final java.sql.Connection connection = DriverManager.getConnection ( this.uri, this.username, this.password );
        return connection;
    }

    public java.sql.Connection getConnection () throws SQLException
    {
        return createConnection ();
    }

    public Integer getTimeout ()
    {
        return this.timeout;
    }
}
