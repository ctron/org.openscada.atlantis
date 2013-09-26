/*
 * This file is part of the OpenSCADA project
 * 
 * Copyright (C) 2006-2012 TH4 SYSTEMS GmbH (http://th4-systems.com)
 * Copyright (C) 2013 Jens Reimann (ctron@dentrassi.de)
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

package org.eclipse.scada.da.server.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.openscada.da.server.common.item.factory.DefaultChainItemFactory;
import org.openscada.da.server.common.item.factory.FolderItemFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractQuery
{

    private final static Logger logger = LoggerFactory.getLogger ( AbstractQuery.class );

    private final String id;

    private final int period;

    protected final String sql;

    protected final Connection connection;

    private ScheduledExecutorService timer;

    private final Runnable task;

    protected FolderItemFactory itemFactory;

    protected final Map<Integer, String> columnAliases;

    private ScheduledFuture<?> job;

    public AbstractQuery ( final String id, final int period, final String sql, final Connection connection, final Map<Integer, String> columnAliases )
    {
        this.id = id;
        this.period = period;
        this.sql = sql;
        this.connection = connection;
        this.columnAliases = columnAliases;

        logger.info ( "Created new query: {}", this.id );

        this.task = new Runnable () {

            @Override
            public void run ()
            {
                AbstractQuery.this.tick ();
            }
        };
    }

    public void register ( final ScheduledExecutorService timer, final DefaultChainItemFactory parentItemFactory )
    {
        this.timer = timer;
        this.itemFactory = parentItemFactory.createSubFolderFactory ( this.id );

        this.job = this.timer.scheduleAtFixedRate ( this.task, 0, this.period, TimeUnit.MILLISECONDS );
    }

    public void unregister ()
    {
        if ( this.job != null )
        {
            this.job.cancel ( false );
            this.job = null;
        }
        this.timer = null;

        if ( this.itemFactory != null )
        {
            this.itemFactory.dispose ();
            this.itemFactory = null;
        }
    }

    public void tick ()
    {
        try
        {
            doQuery ();
        }
        catch ( final Throwable e )
        {
            logger.debug ( "Global error", e );
            setGlobalError ( e );
        }
    }

    protected abstract void setGlobalError ( Throwable e );

    protected abstract void doQuery () throws Exception;

    protected String mapFieldName ( final int i, final ResultSet result ) throws SQLException
    {
        final String field;
        if ( this.columnAliases.containsKey ( i ) )
        {
            field = this.columnAliases.get ( i );
        }
        else
        {
            field = result.getMetaData ().getColumnName ( i );
        }
        return field;
    }

}
