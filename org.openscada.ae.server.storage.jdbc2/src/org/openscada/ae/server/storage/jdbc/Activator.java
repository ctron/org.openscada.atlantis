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

import org.openscada.ae.server.storage.Storage;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;

public class Activator implements BundleActivator
{
    private static BundleContext context;

    private JdbcStorage jdbcStorage;

    private ConnectionCreator connectionCreator;

    private ServiceRegistration jdbcStorageHandle;

    private final int maxLength = 4000;

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
        this.connectionCreator = new ConnectionCreator ();
        this.connectionCreator.init ();
        this.jdbcStorage = createJdbcStorage ( this.connectionCreator );
        this.jdbcStorage.start ();

        final Dictionary<String, Object> properties = new Hashtable<String, Object> ();
        properties.put ( Constants.SERVICE_DESCRIPTION, "JDBC implementation for org.openscada.ae.server.storage.Storage" );
        properties.put ( Constants.SERVICE_VENDOR, "TH4 SYSTEMS GmbH" );
        this.jdbcStorageHandle = context.registerService ( new String[] { JdbcStorage.class.getName (), Storage.class.getName () }, this.jdbcStorage, properties );
    }

    /*
     * (non-Javadoc)
     * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
     */
    @Override
    public void stop ( final BundleContext bundleContext ) throws Exception
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
        Activator.context = null;
    }

    private JdbcStorage createJdbcStorage ( final ConnectionCreator connectionCreator )
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
            jdbcStorageDao.setConnectionCreator ( connectionCreator );
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
            jdbcStorageDao.setConnectionCreator ( connectionCreator );
            storageDao = jdbcStorageDao;
        }
        jdbcStorage.setJdbcStorageDao ( storageDao );
        return jdbcStorage;
    }
}
