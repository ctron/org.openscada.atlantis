/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2012 TH4 SYSTEMS GmbH (http://th4-systems.com)
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

package org.openscada.ae.server.storage.jdbc;

import java.sql.SQLException;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.eclipse.scada.utils.concurrent.NamedThreadFactory;
import org.eclipse.scada.utils.osgi.jdbc.task.ConnectionContext;
import org.openscada.ae.Event;
import org.osgi.service.jdbc.DataSourceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Interner;

public class JdbcStorageDao extends AbstractJdbcStorageDao
{

    private final static Logger logger = LoggerFactory.getLogger ( JdbcStorageDao.class );

    private final ScheduledExecutorService executor;

    public JdbcStorageDao ( final DataSourceFactory dataSourceFactory, final Properties properties, final boolean usePool, final Interner<String> stringInterner ) throws SQLException
    {
        super ( dataSourceFactory, properties, usePool, stringInterner );

        this.executor = Executors.newSingleThreadScheduledExecutor ( new NamedThreadFactory ( "org.openscada.ae.server.storage.jdbc/CleanupThread" ) );
        this.executor.scheduleWithFixedDelay ( new Runnable () {

            @Override
            public void run ()
            {
                cleanupArchive ();
            }
        }, getCleanupPeriod (), getCleanupPeriod (), TimeUnit.SECONDS );
    }

    @Override
    public void dispose ()
    {
        logger.info ( "Shutting down" );
        this.executor.shutdown ();

        super.dispose ();
        logger.info ( "Shutdown complete" );
    }

    protected boolean isReplication ()
    {
        return Boolean.getBoolean ( "org.openscada.ae.server.storage.jdbc.enableReplication" );
    }

    public static long getCleanupPeriod ()
    {
        return Long.getLong ( "org.openscada.ae.server.storage.jdbc.cleanupPeriodSeconds", 60 * 60 /* default to one hour */);
    }

    @Override
    protected void performStoreEvent ( final Event event, final ConnectionContext connectionContext ) throws SQLException, Exception
    {
        super.performStoreEvent ( event, connectionContext );
        if ( isReplication () )
        {
            storeReplicationEvent ( event, connectionContext.getConnection () );
        }
    }
}
