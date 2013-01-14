/*
 * This file is part of the openSCADA project
 * 
 * Copyright (C) 2006-2010 TH4 SYSTEMS GmbH (http://th4-systems.com)
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

package org.openscada.da.server.jdbc;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.concurrent.ScheduledExecutorService;

import org.openscada.da.server.browser.common.FolderCommon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Connection
{
    private final static Logger logger = LoggerFactory.getLogger ( Connection.class );

    private final Collection<Query> queries = new LinkedList<Query> ();

    private final Collection<Update> updates = new LinkedList<Update> ();

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

    public void register ( final Hive hive, final FolderCommon rootFolder, final ScheduledExecutorService timer )
    {
        this.itemFactory = new DataItemFactory ( hive, rootFolder, this.id, this.id );

        for ( final Query query : this.queries )
        {
            query.register ( timer, this.itemFactory );
        }

        for ( final Update update : this.updates )
        {
            update.register ( this.itemFactory );
        }
    }

    public void unregister ( final Hive hive )
    {
        for ( final Query query : this.queries )
        {
            query.unregister ();
        }

        for ( final Update update : this.updates )
        {
            update.unregister ();
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

    public void add ( final Update update )
    {
        this.updates.add ( update );
    }
}
