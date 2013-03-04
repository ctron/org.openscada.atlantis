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
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Properties;

import org.openscada.ae.server.storage.Storage;
import org.openscada.utils.interner.InternerHelper;
import org.openscada.utils.osgi.SingleServiceListener;
import org.openscada.utils.osgi.SingleServiceTracker;
import org.openscada.utils.osgi.jdbc.DataSourceHelper;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.Filter;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.jdbc.DataSourceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Interner;

public class Activator implements BundleActivator
{
    private static final Logger logger = LoggerFactory.getLogger ( Activator.class );

    private static BundleContext context;

    private JdbcStorage jdbcStorage;

    private ServiceRegistration<?> jdbcStorageHandle;

    private final int maxLength = 4000;

    private SingleServiceTracker<DataSourceFactory> dataSouceFactoryTracker;

    private Interner<String> stringInterner;

    static BundleContext getContext ()
    {
        return context;
    }

    /*
     * (non-Javadoc)
     * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
     */
    @Override
    public void start ( final BundleContext bundleContext ) throws Exception
    {
        Activator.context = bundleContext;

        this.stringInterner = InternerHelper.makeInterner ( "org.openscada.ae.monitor.dataitem.stringInternerType", "weak" );

        final String driver = DataSourceHelper.getDriver ( "org.openscada.ae.server.storage.jdbc.driver", DataSourceHelper.DEFAULT_PREFIX );

        final Filter filter = context.createFilter ( "(&(objectClass=" + DataSourceFactory.class.getName () + ")(" + DataSourceFactory.OSGI_JDBC_DRIVER_CLASS + "=" + driver + "))" );
        this.dataSouceFactoryTracker = new SingleServiceTracker<DataSourceFactory> ( bundleContext, filter, new SingleServiceListener<DataSourceFactory> () {
            @Override
            public void serviceChange ( final ServiceReference<DataSourceFactory> reference, final DataSourceFactory dsf )
            {
                try
                {
                    deactivate ();
                }
                catch ( final Exception e )
                {
                    logger.error ( "an error occured on deactivating ae jdbc storage", e );
                }
                if ( dsf != null )
                {
                    try
                    {
                        activate ( dsf );
                    }
                    catch ( final Exception e )
                    {
                        logger.error ( "an error occured on activating ae jdbc storage", e );
                    }
                }
            }
        } );
        this.dataSouceFactoryTracker.open ();
    }

    private void activate ( final DataSourceFactory dataSourceFactory ) throws Exception
    {
        final Properties dbProperties = DataSourceHelper.getDataSourceProperties ( "org.openscada.ae.server.storage.jdbc", DataSourceHelper.DEFAULT_PREFIX );

        this.jdbcStorage = createJdbcStorage ( dataSourceFactory, dbProperties, DataSourceHelper.isConnectionPool ( "org.openscada.ae.server.storage.jdbc", DataSourceHelper.DEFAULT_PREFIX, false ) );
        this.jdbcStorage.start ();

        final Dictionary<String, Object> properties = new Hashtable<String, Object> ( 2 );
        properties.put ( Constants.SERVICE_DESCRIPTION, "JDBC implementation for org.openscada.ae.server.storage.Storage" );
        properties.put ( Constants.SERVICE_VENDOR, "TH4 SYSTEMS GmbH" );
        this.jdbcStorageHandle = context.registerService ( new String[] { JdbcStorage.class.getName (), Storage.class.getName () }, this.jdbcStorage, properties );
    }

    private void deactivate () throws Exception
    {
        if ( this.jdbcStorageHandle != null )
        {
            this.jdbcStorageHandle.unregister ();
            this.jdbcStorageHandle = null;
        }
        if ( this.jdbcStorage != null )
        {
            this.jdbcStorage.dispose ();
            this.jdbcStorage = null;
        }
    };

    /*
     * (non-Javadoc)
     * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
     */
    @Override
    public void stop ( final BundleContext bundleContext ) throws Exception
    {
        if ( this.dataSouceFactoryTracker != null )
        {
            this.dataSouceFactoryTracker.close ();
        }
        deactivate (); // redundant, but if something happened with the tracker we are sure it is shut down
        Activator.context = null;
    }

    private JdbcStorage createJdbcStorage ( final DataSourceFactory dataSourceFactory, final Properties dbParameters, final boolean usePool ) throws SQLException
    {
        final AbstractJdbcStorageDao jdbcStorageDao = new JdbcStorageDao ( dataSourceFactory, dbParameters, usePool, this.stringInterner );
        jdbcStorageDao.setInstance ( System.getProperty ( "org.openscada.ae.server.storage.jdbc.instance", "default" ) );
        jdbcStorageDao.setMaxLength ( Integer.getInteger ( "org.openscada.ae.server.storage.jdbc.maxlength", this.maxLength ) );
        if ( !System.getProperty ( "org.openscada.ae.server.storage.jdbc.schema", "" ).trim ().isEmpty () )
        {
            jdbcStorageDao.setSchema ( System.getProperty ( "org.openscada.ae.server.storage.jdbc.schema" ) + "." );
        }
        return new JdbcStorage ( jdbcStorageDao );
    }
}
