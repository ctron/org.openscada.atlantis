/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2011 TH4 SYSTEMS GmbH (http://th4-systems.com)
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

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Map.Entry;
import java.util.Properties;

import javax.sql.ConnectionPoolDataSource;

import org.openscada.ae.server.storage.Storage;
import org.openscada.utils.osgi.SingleServiceListener;
import org.openscada.utils.osgi.SingleServiceTracker;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.Filter;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.jdbc.DataSourceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Activator implements BundleActivator
{
    private static final Logger logger = LoggerFactory.getLogger ( Activator.class );

    private static BundleContext context;

    private JdbcStorage jdbcStorage;

    private ServiceRegistration<?> jdbcStorageHandle;

    private final int maxLength = 4000;

    private SingleServiceTracker<DataSourceFactory> dataSouceFactoryTracker;

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

        final String driver = System.getProperty ( "org.openscada.ae.server.storage.jdbc.driver", System.getProperty ( "org.openscada.jdbc.driver", "" ) );
        Filter filter = context.createFilter ( "(&(objectClass=" + DataSourceFactory.class.getName () + ")(" + DataSourceFactory.OSGI_JDBC_DRIVER_CLASS + "=" + driver + "))" );
        dataSouceFactoryTracker = new SingleServiceTracker<DataSourceFactory> ( bundleContext, filter, new SingleServiceListener<DataSourceFactory> () {
            public void serviceChange ( ServiceReference<DataSourceFactory> reference, DataSourceFactory dsf )
            {
                try
                {
                    deactivate ();
                }
                catch ( Exception e )
                {
                    logger.error ( "an error occured on deactivating ae jdbc storage", e );
                }
                if ( dsf != null )
                {
                    try
                    {
                        activate ( dsf );
                    }
                    catch ( Exception e )
                    {
                        logger.error ( "an error occured on activating ae jdbc storage", e );
                    }
                }
            }
        } );
        dataSouceFactoryTracker.open ();
    }

    private void activate ( DataSourceFactory dsf ) throws Exception
    {
        final Properties dbproperties = new Properties ();
        setupDbProperties ( dbproperties );

        if ( dbproperties.get ( DataSourceFactory.JDBC_USER ) == null || ( (String)dbproperties.get ( DataSourceFactory.JDBC_USER ) ).trim ().isEmpty () )
        {
            dbproperties.setProperty ( DataSourceFactory.JDBC_USER, System.getProperty ( "org.openscada.ae.server.storage.jdbc.username", "" ) );
        }

        ConnectionPoolDataSource dataSource = dsf.createConnectionPoolDataSource ( dbproperties );
        this.jdbcStorage = createJdbcStorage ( dataSource );
        this.jdbcStorage.start ();

        final Dictionary<String, Object> properties = new Hashtable<String, Object> ();
        properties.put ( Constants.SERVICE_DESCRIPTION, "JDBC implementation for org.openscada.ae.server.storage.Storage" );
        properties.put ( Constants.SERVICE_VENDOR, "TH4 SYSTEMS GmbH" );
        this.jdbcStorageHandle = context.registerService ( new String[] { JdbcStorage.class.getName (), Storage.class.getName () }, this.jdbcStorage, properties );
    }

    private void setupDbProperties ( Properties dbproperties )
    {
        for ( Entry<Object, Object> e : System.getProperties ().entrySet () )
        {
            if ( ! ( e.getKey () instanceof String ) || ! ( e.getValue () instanceof String ) )
            {
                continue;
            }
            String key = (String)e.getKey ();
            String value = (String)e.getValue ();
            if ( !key.startsWith ( "org.openscada.jdbc." ) )
            {
                continue;
            }
            key = key.replace ( "org.openscada.jdbc.", "" );
            dbproperties.setProperty ( key, value );
        }
        for ( Entry<Object, Object> e : System.getProperties ().entrySet () )
        {
            if ( ! ( e.getKey () instanceof String ) || ! ( e.getValue () instanceof String ) )
            {
                continue;
            }
            String key = (String)e.getKey ();
            String value = (String)e.getValue ();
            if ( !key.startsWith ( "org.openscada.ae.server.storage.jdbc." ) )
            {
                continue;
            }
            key = key.replace ( "org.openscada.ae.server.storage.jdbc.", "" );
            dbproperties.setProperty ( key, value );
        }
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
            this.jdbcStorage.stop ();
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
        if ( dataSouceFactoryTracker != null )
        {
            dataSouceFactoryTracker.close ();
        }
        deactivate (); // redundant, but if something happened with the tracker we are sure it is shut down
        Activator.context = null;
    }

    private JdbcStorage createJdbcStorage ( final ConnectionPoolDataSource dataSource )
    {
        final JdbcStorage jdbcStorage = new JdbcStorage ();
        StorageDao storageDao;
        if ( "legacy".equals ( System.getProperty ( "org.openscada.ae.server.storage.jdbc.instance", "" ) ) )
        {
            final LegacyJdbcStorageDao jdbcStorageDao = new LegacyJdbcStorageDao ();
            jdbcStorageDao.setMaxLength ( Integer.getInteger ( "org.openscada.ae.server.storage.jdbc.maxlength", this.maxLength ) );
            if ( !System.getProperty ( "org.openscada.ae.server.storage.jdbc.schema", "" ).trim ().isEmpty () )
            {
                jdbcStorageDao.setSchema ( System.getProperty ( "org.openscada.ae.server.storage.jdbc.schema" ) + "." );
            }
            jdbcStorageDao.setDataSource ( dataSource );
            storageDao = jdbcStorageDao;
        }
        else
        {
            final JdbcStorageDao jdbcStorageDao = new JdbcStorageDao ();
            jdbcStorageDao.setInstance ( System.getProperty ( "org.openscada.ae.server.storage.jdbc.instance", "default" ) );
            jdbcStorageDao.setMaxLength ( Integer.getInteger ( "org.openscada.ae.server.storage.jdbc.maxlength", this.maxLength ) );
            if ( !System.getProperty ( "org.openscada.ae.server.storage.jdbc.schema", "" ).trim ().isEmpty () )
            {
                jdbcStorageDao.setSchema ( System.getProperty ( "org.openscada.ae.server.storage.jdbc.schema" ) + "." );
            }
            jdbcStorageDao.setDataSource ( dataSource );
            storageDao = jdbcStorageDao;
        }
        jdbcStorage.setJdbcStorageDao ( storageDao );
        return jdbcStorage;
    }
}
