/*
 * This file is part of the openSCADA project
 * 
 * Copyright (C) 2006-2010 TH4 SYSTEMS GmbH (http://th4-systems.com)
 * Copyright (C) 2013 Jens Reimann (ctron@dentrassi.de)
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

import java.util.Collection;
import java.util.LinkedList;
import java.util.concurrent.ScheduledExecutorService;

import org.openscada.da.server.browser.common.FolderCommon;
import org.openscada.da.server.common.item.factory.DefaultChainItemFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Connection
{
    private final static Logger logger = LoggerFactory.getLogger ( Connection.class );

    private final Collection<AbstractQuery> queries = new LinkedList<AbstractQuery> ();

    private final Collection<Update> updates = new LinkedList<Update> ();

    private final String username;

    private final String password;

    private final String uri;

    private final String id;

    private DefaultChainItemFactory itemFactory;

    private final Integer timeout;

    private final ConnectionFactory connectionFactory;

    private final String connectionClass;

    public Connection ( final ConnectionFactory connectionFactory, final String id, final Integer timeout, final String connectionClass, final String uri, final String username, final String password )
    {
        this.connectionFactory = connectionFactory;
        this.uri = uri;
        this.username = username;
        this.password = password;
        this.id = id;
        this.timeout = timeout;
        this.connectionClass = connectionClass;
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

    public void add ( final AbstractQuery query )
    {
        this.queries.add ( query );
    }

    public void register ( final Hive hive, final FolderCommon rootFolder, final ScheduledExecutorService timer )
    {
        this.itemFactory = new DefaultChainItemFactory ( hive, rootFolder, this.id, this.id );

        for ( final AbstractQuery query : this.queries )
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
        for ( final AbstractQuery query : this.queries )
        {
            query.unregister ();
        }

        for ( final Update update : this.updates )
        {
            update.unregister ();
        }

        if ( this.itemFactory != null )
        {
            this.itemFactory.dispose ();
            this.itemFactory = null;
        }
    }

    protected java.sql.Connection createConnection () throws Exception
    {
        return this.connectionFactory.createConnection ( this.connectionClass, this.uri, this.username, this.password, this.timeout );
    }

    public java.sql.Connection getConnection () throws Exception
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
