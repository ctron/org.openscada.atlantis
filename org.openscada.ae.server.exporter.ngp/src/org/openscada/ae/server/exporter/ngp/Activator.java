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

package org.openscada.ae.server.exporter.ngp;

import org.openscada.ae.server.Service;
import org.openscada.ae.server.ngp.Exporter;
import org.openscada.core.ConnectionInformation;
import org.openscada.utils.osgi.SingleServiceListener;
import org.openscada.utils.osgi.SingleServiceTracker;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Activator implements BundleActivator
{

    private final static Logger logger = LoggerFactory.getLogger ( Activator.class );

    private static BundleContext context;

    static BundleContext getContext ()
    {
        return context;
    }

    private SingleServiceTracker<Service> tracker;

    private Exporter exporter;

    /*
     * (non-Javadoc)
     * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
     */
    @Override
    public void start ( final BundleContext bundleContext ) throws Exception
    {
        Activator.context = bundleContext;

        this.tracker = new SingleServiceTracker<Service> ( context, Service.class, new SingleServiceListener<Service> () {

            @Override
            public void serviceChange ( final ServiceReference<Service> reference, final Service service )
            {
                handleServiceChange ( service );
            }

        } );
        this.tracker.open ();
    }

    protected void handleServiceChange ( final Service service )
    {
        logger.warn ( "Exporting new service: {}", service );

        try
        {
            if ( this.exporter != null )
            {
                this.exporter.stop ();
                this.exporter = null;
            }
            if ( service != null )
            {
                this.exporter = new Exporter ( service, ConnectionInformation.fromURI ( System.getProperty ( "openscada.hd.ngp.exportUri", "hd:ngp://0.0.0.0:2301" ) ) );
                this.exporter.start ();
            }
        }
        catch ( final Exception e )
        {
            logger.warn ( "Failed to export hd service", e );
        }
    }

    /*
     * (non-Javadoc)
     * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
     */
    @Override
    public void stop ( final BundleContext bundleContext ) throws Exception
    {
        this.tracker.close ();
        if ( this.exporter != null )
        {
            this.exporter.stop ();
            this.exporter = null;
        }
        Activator.context = null;
    }

}
