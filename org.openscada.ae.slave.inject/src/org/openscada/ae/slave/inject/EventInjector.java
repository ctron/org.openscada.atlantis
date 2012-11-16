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

package org.openscada.ae.slave.inject;

import java.sql.SQLException;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.openscada.utils.concurrent.ScheduledExportedExecutorService;
import org.osgi.service.jdbc.DataSourceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EventInjector
{

    private final static Logger logger = LoggerFactory.getLogger ( EventInjector.class );

    private final JdbcStorageDao storage;

    private final ScheduledExportedExecutorService scheduler;

    public EventInjector ( final DataSourceFactory factory, final Properties properties, final int delay ) throws SQLException
    {
        logger.info ( "Starting event injector" ); //$NON-NLS-1$
        this.storage = new JdbcStorageDao ( factory, properties, false, null );
        this.scheduler = new ScheduledExportedExecutorService ( "org.openscada.ae.slave.inject", 1 ); //$NON-NLS-1$

        this.scheduler.scheduleWithFixedDelay ( new Runnable () {

            @Override
            public void run ()
            {
                process ();
            }
        }, 0, delay, TimeUnit.MILLISECONDS );
    }

    public void dispose ()
    {
        logger.info ( "Disposing event injector ..." ); //$NON-NLS-1$

        this.scheduler.shutdown ();
        this.storage.dispose ();

        logger.info ( "Disposing event injector ... done!" ); //$NON-NLS-1$
    }

    private void process ()
    {
        try
        {
            final int result = EventInjector.this.storage.runOnce ();
            if ( result > 0 )
            {
                logger.info ( "Processed {} entries", result );
            }
            else
            {
                logger.debug ( "Processed {} entries", result );
            }
        }
        catch ( final Exception e )
        {
            logger.warn ( "Failed to process", e );
        }
    }

}
