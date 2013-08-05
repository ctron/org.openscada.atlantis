/*
 * This file is part of the OpenSCADA project
 * 
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

package org.openscada.da.server.jdbc;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;

import org.openscada.core.Variant;
import org.openscada.core.server.OperationParameters;
import org.openscada.da.server.common.chain.WriteHandler;
import org.openscada.da.server.common.item.factory.DefaultChainItemFactory;
import org.openscada.da.server.jdbc.TabularExporter.Entry;
import org.openscada.da.server.jdbc.TabularExporter.WriteHandlerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TabularQuery extends AbstractQuery
{
    private final static Logger logger = LoggerFactory.getLogger ( TabularQuery.class );

    private final int idColumn;

    private TabularExporter exporter;

    private final Map<String, String> updateMap;

    private WriteHandlerFactory writeHandlerFactory;

    public TabularQuery ( final String id, final int idColumn, final int period, final String sql, final Connection connection, final Map<Integer, String> columnAliases, final Map<String, String> updateMap )
    {
        super ( id, period, sql, connection, columnAliases );
        this.idColumn = idColumn;
        this.updateMap = updateMap;
        this.writeHandlerFactory = new WriteHandlerFactory () {

            @Override
            public WriteHandler createWriteHandler ( final String columnName )
            {
                return performCreateWriteHandler ( columnName );
            }
        };
    }

    protected WriteHandler performCreateWriteHandler ( final String columnName )
    {
        final String updateSql = this.updateMap.get ( columnName );
        if ( updateSql == null || updateSql.isEmpty () )
        {
            return null;
        }

        return new WriteHandler () {

            @Override
            public void handleWrite ( final Variant value, final OperationParameters operationParameters ) throws Exception
            {
                processUpdate ( updateSql, value, operationParameters );
            }
        };
    }

    protected void processUpdate ( final String updateSql, final Variant value, final OperationParameters operationParameters ) throws Exception
    {
        try (final java.sql.Connection c = this.connection.getConnection ())
        {
            c.setAutoCommit ( true );

            try (PreparedStatement stmt = c.prepareStatement ( updateSql ))
            {
                stmt.setObject ( 1, value.getValue () );
                stmt.executeUpdate ();
            }
        }
    }

    @Override
    public void register ( final ScheduledExecutorService timer, final DefaultChainItemFactory parentItemFactory )
    {
        super.register ( timer, parentItemFactory );
        this.exporter = new TabularExporter ( this.itemFactory, this.writeHandlerFactory );
    }

    @Override
    public void unregister ()
    {
        if ( this.exporter != null )
        {
            this.exporter.dispose ();
            this.exporter = null;
        }
        super.unregister ();
    }

    @Override
    protected void setGlobalError ( final Throwable e )
    {
        logger.error ( "Failed to query", e );
    }

    @Override
    protected void doQuery () throws Exception
    {
        logger.debug ( "Perform query" );
        try (java.sql.Connection connection = this.connection.getConnection ())
        {
            try (final PreparedStatement stmt = connection.prepareStatement ( this.sql ))
            {
                if ( this.connection.getTimeout () != null )
                {
                    stmt.setQueryTimeout ( this.connection.getTimeout () / 1000 );
                }

                try (final ResultSet result = stmt.executeQuery ())
                {
                    processResult ( result );
                }
            }
        }
    }

    private void processResult ( final ResultSet result ) throws SQLException
    {
        final List<Entry> entries = new LinkedList<> ();

        while ( result.next () )
        {
            final int count = result.getMetaData ().getColumnCount ();

            final String idValue = String.format ( "%s", result.getObject ( this.idColumn ) );
            final Entry entry = new Entry ( idValue );
            entries.add ( entry );

            for ( int i = 1; i <= count; i++ )
            {
                final String field = mapFieldName ( i, result );
                entry.put ( field, Variant.valueOf ( result.getObject ( i ) ) );
            }
        }

        this.exporter.update ( entries );
    }

}
