/*
 * This file is part of the openSCADA project
 * 
 * Copyright (C) 2011-2012 TH4 SYSTEMS GmbH (http://th4-systems.com)
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

package org.openscada.hd.server.storage.slave.hds;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;

import org.eclipse.scada.hds.DataFilePool;
import org.eclipse.scada.utils.concurrent.ScheduledExportedExecutorService;
import org.eclipse.scada.utils.str.StringReplacer;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator
{
    private static final String BASE_PATH_PROP = "org.openscada.hd.server.storage.slave.hds.basePath";

    private static BundleContext context;

    static BundleContext getContext ()
    {
        return context;
    }

    private ScheduledExecutorService executor;

    private final List<StorageManager> storageManagers = new LinkedList<> ();

    private DataFilePool pool;

    private ScheduledExportedExecutorService eventExecutor;

    /*
     * (non-Javadoc)
     * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
     */
    @Override
    public void start ( final BundleContext bundleContext ) throws Exception
    {
        Activator.context = bundleContext;

        this.executor = new ScheduledExportedExecutorService ( BASE_PATH_PROP, 1 );
        this.eventExecutor = new ScheduledExportedExecutorService ( "org.openscada.hd.server.storage.slave.hds.events", 1 );

        this.pool = new DataFilePool ( Integer.getInteger ( "org.openscada.hd.server.storage.slave.hds.instanceCountTarget", 10 ) );

        final String basePaths = System.getProperty ( BASE_PATH_PROP );
        if ( basePaths == null || basePaths.isEmpty () )
        {
            throw new IllegalStateException ( String.format ( "Property '%s' must be set in order to activate bundle.", BASE_PATH_PROP ) );
        }

        try
        {
            for ( String path : basePaths.split ( ":" ) )
            {
                // replace variables in path, like "user.home"
                path = StringReplacer.replace ( path, System.getProperties () );
                if ( path.startsWith ( "@" ) )
                {
                    final File dir = new File ( path.substring ( 1 ) );
                    if ( !dir.isDirectory () )
                    {
                        throw new IllegalStateException ( String.format ( "'%s' is not a directory", dir ) );
                    }
                    for ( final File child : dir.listFiles () )
                    {
                        this.storageManagers.add ( new StorageManager ( bundleContext, child, this.pool, this.executor, this.eventExecutor ) );
                    }
                }
                else
                {
                    this.storageManagers.add ( new StorageManager ( bundleContext, new File ( path ), this.pool, this.executor, this.eventExecutor ) );
                }
            }
        }
        catch ( final Exception e )
        {
            dispose ();
            throw new Exception ( "Failed to start up bundle", e );
        }
    }

    private void dispose ()
    {
        if ( this.pool != null )
        {
            this.pool.dispose ();
            this.pool = null;
        }

        for ( final StorageManager manager : this.storageManagers )
        {
            manager.dispose ();
        }
        this.storageManagers.clear ();

        if ( this.executor != null )
        {
            this.executor.shutdown ();
            this.executor = null;
        }

        if ( this.eventExecutor != null )
        {
            this.eventExecutor.shutdown ();
            this.eventExecutor = null;
        }
    }

    /*
     * (non-Javadoc)
     * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
     */
    @Override
    public void stop ( final BundleContext bundleContext ) throws Exception
    {
        dispose ();
        Activator.context = null;
    }

}
