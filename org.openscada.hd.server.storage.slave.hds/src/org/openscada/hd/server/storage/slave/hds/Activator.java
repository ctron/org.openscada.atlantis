/*
 * This file is part of the openSCADA project
 * Copyright (C) 2011-2012 TH4 SYSTEMS GmbH (http://th4-systems.com)
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
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.openscada.hds.DataFilePool;
import org.openscada.utils.concurrent.NamedThreadFactory;
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

    private StorageManager storageManager;

    private DataFilePool pool;

    /*
     * (non-Javadoc)
     * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
     */
    @Override
    public void start ( final BundleContext bundleContext ) throws Exception
    {
        Activator.context = bundleContext;

        this.executor = Executors.newSingleThreadScheduledExecutor ( new NamedThreadFactory ( BASE_PATH_PROP ) );

        this.pool = new DataFilePool ( Integer.getInteger ( "org.openscada.hd.server.storage.slave.hds.instanceCountTarget", 10 ) );

        final String basePath = System.getProperty ( BASE_PATH_PROP );
        if ( basePath == null || basePath.isEmpty () )
        {
            throw new IllegalStateException ( String.format ( "Property '%s' must be set in order to activate bundle.", BASE_PATH_PROP ) );
        }

        this.storageManager = new StorageManager ( bundleContext, new File ( basePath ), this.pool, this.executor );
    }

    /*
     * (non-Javadoc)
     * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
     */
    @Override
    public void stop ( final BundleContext bundleContext ) throws Exception
    {
        this.pool.dispose ();

        this.storageManager.dispose ();

        this.executor.shutdown ();
        Activator.context = null;
    }

}
