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

package org.openscada.ae.slave.inject;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Properties;

import org.openscada.ae.Event;
import org.openscada.ae.server.storage.jdbc.AbstractJdbcStorageDao;
import org.openscada.utils.osgi.BundleObjectInputStream;
import org.openscada.utils.osgi.jdbc.data.SingleColumnRowMapper;
import org.openscada.utils.osgi.jdbc.task.CommonConnectionTask;
import org.openscada.utils.osgi.jdbc.task.ConnectionContext;
import org.openscada.utils.osgi.jdbc.task.RowCallback;
import org.osgi.service.jdbc.DataSourceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Interner;

public class JdbcStorageDao extends AbstractJdbcStorageDao
{

    public class RowHandler implements RowCallback
    {
        private final ConnectionContext connectionContext;

        private int count;

        public RowHandler ( final ConnectionContext connectionContext )
        {
            this.connectionContext = connectionContext;
        }

        @Override
        public void processRow ( final ResultSet resultSet ) throws SQLException
        {
            this.count++;
            JdbcStorageDao.this.processRow ( this.connectionContext, resultSet );
        }

        public int getCount ()
        {
            return this.count;
        }
    }

    private final boolean deleteFailed = Boolean.getBoolean ( "org.openscada.ae.slave.inject.deleteFailed" );

    private final static Logger logger = LoggerFactory.getLogger ( JdbcStorageDao.class );

    public JdbcStorageDao ( final DataSourceFactory dataSourceFactory, final Properties properties, final boolean usePool, final Interner<String> stringInterner ) throws SQLException
    {
        super ( dataSourceFactory, properties, usePool, stringInterner );
    }

    private String getReplicationSelectSql ()
    {
        return System.getProperty ( "org.openscada.ae.slave.inject.selectSql", String.format ( "SELECT ID, ENTRY_TIMESTAMP, NODE_ID, DATA FROM %sOPENSCADA_AE_REP", getReplicationSchema () ) );
    }

    private String getReplicationDeleteSql ()
    {
        return System.getProperty ( "org.openscada.ae.slave.inject.deleteSql", String.format ( "DELETE FROM %sOPENSCADA_AE_REP where ID=?", getReplicationSchema () ) );
    }

    private String getReplicationSchema ()
    {
        return System.getProperty ( "org.openscada.ae.slave.inject.schema", "" );
    }

    protected int runOnce ()
    {
        return this.accessor.doWithConnection ( new CommonConnectionTask<Integer> () {

            @Override
            protected Integer performTask ( final ConnectionContext connectionContext ) throws Exception
            {
                connectionContext.setAutoCommit ( false );
                final int result = processOnce ( connectionContext );
                connectionContext.commit ();
                return result;
            }
        } );
    }

    protected int processOnce ( final ConnectionContext connectionContext ) throws SQLException
    {
        final String selectSql = getReplicationSelectSql ();

        final RowHandler rowHandler = new RowHandler ( connectionContext );
        connectionContext.query ( rowHandler, selectSql );
        return rowHandler.getCount ();
    }

    protected void processRow ( final ConnectionContext connectionContext, final ResultSet resultSet ) throws SQLException
    {
        final String id = resultSet.getString ( 1 );

        logger.debug ( "Processing event {}", id );

        if ( entryExists ( connectionContext, id ) )
        {
            logger.debug ( "Entry exists ... only delete" );
            deleteReplicationEntry ( connectionContext, id );
            return;
        }

        final Timestamp entryTimestamp = resultSet.getTimestamp ( 2 );
        final String nodeId = resultSet.getString ( 3 );

        final byte[] data = resultSet.getBytes ( 4 );

        logger.debug ( "Injecting event {} from node {}, timeDiff: {} ms, dataSize: {}", new Object[] { id, nodeId, System.currentTimeMillis () - entryTimestamp.getTime (), data.length } );

        try
        {
            logger.debug ( "Storing event" );
            storeEvent ( deserializeEvent ( data ) );
            deleteReplicationEntry ( connectionContext, id );
        }
        catch ( final Exception e )
        {
            logger.warn ( "Failed to decode and store event", e );
            if ( this.deleteFailed )
            {
                deleteReplicationEntry ( connectionContext, id );
            }
        }
    }

    private Event deserializeEvent ( final byte[] data ) throws IOException, ClassNotFoundException
    {
        logger.debug ( "Deserialize event" );

        final BundleObjectInputStream stream = new BundleObjectInputStream ( new ByteArrayInputStream ( data ), Activator.getContext ().getBundle () );
        try
        {
            final Object o = stream.readObject ();
            if ( o instanceof Event )
            {
                return (Event)o;
            }
            else if ( o == null )
            {
                logger.warn ( "Found null event" );
                return null;
            }
            else
            {
                logger.warn ( "Expected event type {} but found {}. Discarding...", Event.class, o.getClass () );
                return null;
            }
        }
        finally
        {
            stream.close ();
        }
    }

    private void deleteReplicationEntry ( final ConnectionContext connectionContext, final String id ) throws SQLException
    {
        connectionContext.update ( getReplicationDeleteSql (), id );
    }

    private boolean entryExists ( final ConnectionContext connectionContext, final String id ) throws SQLException
    {
        logger.debug ( "Checking if entry already exists" );

        final List<Number> result = connectionContext.query ( new SingleColumnRowMapper<Number> ( Number.class ), String.format ( "SELECT COUNT(*) FROM %sOPENSCADA_AE_EVENTS WHERE ID=?", getSchema () ), id );
        if ( result.isEmpty () )
        {
            return false;
        }

        return result.get ( 0 ).intValue () > 0;
    }
}
