/*
 * This file is part of the openSCADA project
 * Copyright (C) 2006-2012 TH4 SYSTEMS GmbH (http://th4-systems.com)
 *
 * openSCADA is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 3
 * only, as published by the Free Software Foundation.
 *
 * openSCADA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License version 3 for more details
 * (a copy is included in the LICENSE file that accompanied this code).
 *
 * You should have received a copy of the GNU Lesser General Public License
 * version 3 along with openSCADA. If not, see
 * <http://opensource.org/licenses/lgpl-3.0.html> for a copy of the LGPLv3 License.
 */

package org.openscada.hd.server.importer.hsdb;

import java.io.File;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.openscada.utils.concurrent.NamedThreadFactory;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator
{

    private static BundleContext context;

    static BundleContext getContext ()
    {
        return context;
    }

    private HSDBStorageManager manager;

    private ScheduledExecutorService executor;

    /*
     * (non-Javadoc)
     * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
     */
    @Override
    public void start ( final BundleContext bundleContext ) throws Exception
    {
        Activator.context = bundleContext;
        this.executor = Executors.newSingleThreadScheduledExecutor ( new NamedThreadFactory ( "HSBDImporter" ) );
        this.manager = new HSDBStorageManager ( this.executor, "HSDB.", bundleContext, new File ( System.getProperty ( "org.openscada.hd.server.importer.hsdb.root", "" ) ) );
    }

    /*
     * (non-Javadoc)
     * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
     */
    @Override
    public void stop ( final BundleContext bundleContext ) throws Exception
    {
        this.manager.dispose ();
        this.executor.shutdown ();
        Activator.context = null;
    }

}
