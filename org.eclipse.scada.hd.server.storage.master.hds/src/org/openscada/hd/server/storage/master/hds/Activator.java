/*
 * This file is part of the openSCADA project
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

package org.openscada.hd.server.storage.master.hds;

import java.util.Dictionary;
import java.util.Hashtable;

import org.eclipse.scada.hds.DataFilePool;
import org.openscada.hd.server.storage.master.hds.console.Console;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Activator implements BundleActivator
{

    private final static Logger logger = LoggerFactory.getLogger ( Activator.class );

    private static BundleContext context;

    private StorageManager manager;

    private DataFilePool pool;

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

        this.pool = new DataFilePool ( Integer.getInteger ( "org.openscada.hd.server.storage.master.hds.instanceCountTarget", 10 ) ); //$NON-NLS-1$

        this.manager = new StorageManager ( bundleContext, this.pool );
        registerConsole ();
    }

    /*
     * (non-Javadoc)
     * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
     */
    @Override
    public void stop ( final BundleContext bundleContext ) throws Exception
    {
        this.manager.dispose ();
        this.manager = null;

        this.pool.dispose ();

        Activator.context = null;
    }

    private void registerConsole ()
    {
        try
        {
            final Console console = new Console ( this.manager );
            final Dictionary<String, Object> properties = new Hashtable<String, Object> ();
            properties.put ( "osgi.command.scope", "hds" ); //$NON-NLS-1$
            properties.put ( "osgi.command.function", new String[] { "list", "purgeAll", "remove", "create" } ); //$NON-NLS-1$

            context.registerService ( Console.class, console, properties );
        }
        catch ( final Exception e )
        {
            logger.warn ( "Failed to register console", e ); //$NON-NLS-1$
        }
    }

}
