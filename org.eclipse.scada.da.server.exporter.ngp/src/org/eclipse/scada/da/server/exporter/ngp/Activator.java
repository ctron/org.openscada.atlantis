/*
 * This file is part of the OpenSCADA project
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

package org.eclipse.scada.da.server.exporter.ngp;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Set;

import org.eclipse.scada.core.ConnectionInformation;
import org.eclipse.scada.da.server.ngp.Exporter;
import org.eclipse.scada.utils.osgi.SingleServiceListener;
import org.eclipse.scada.utils.osgi.SingleServiceTracker;
import org.openscada.core.server.exporter.ExporterInformation;
import org.openscada.da.core.server.Hive;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
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

    private SingleServiceTracker<Hive> tracker;

    private Exporter exporter;

    private Set<ExporterInformation> exportedInformation;

    /*
     * (non-Javadoc)
     * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
     */
    @Override
    public void start ( final BundleContext bundleContext ) throws Exception
    {
        Activator.context = bundleContext;

        this.tracker = new SingleServiceTracker<Hive> ( context, Hive.class, new SingleServiceListener<Hive> () {

            @Override
            public void serviceChange ( final ServiceReference<Hive> reference, final Hive service )
            {
                handleServiceChange ( service );
            }

        } );
        this.tracker.open ();
    }

    private final Collection<ServiceRegistration<ExporterInformation>> registeredExportInformation = new LinkedList<ServiceRegistration<ExporterInformation>> ();

    protected void handleServiceChange ( final Hive service )
    {
        logger.warn ( "Exporting new service: {}", service );

        try
        {
            if ( this.exporter != null )
            {
                this.exporter.stop ();
                this.exporter = null;
            }

            // unregister all
            unregisterAllExportInformations ();

            if ( service != null )
            {
                this.exporter = new Exporter ( service, ConnectionInformation.fromURI ( System.getProperty ( "openscada.da.ngp.exportUri", "da:ngp://0.0.0.0:2101" ) ) );
                this.exporter.start ();

                this.exportedInformation = this.exporter.getExporterInformation ();
                for ( final ExporterInformation ei : this.exportedInformation )
                {
                    final ServiceRegistration<ExporterInformation> reg = context.registerService ( ExporterInformation.class, ei, null );
                    if ( reg != null )
                    {
                        this.registeredExportInformation.add ( reg );
                    }
                }
            }
        }
        catch ( final Exception e )
        {
            logger.warn ( "Failed to export hd service", e );
        }
    }

    private void unregisterAllExportInformations ()
    {
        for ( final ServiceRegistration<ExporterInformation> reg : this.registeredExportInformation )
        {
            reg.unregister ();
        }
        this.registeredExportInformation.clear ();
    }

    /*
     * (non-Javadoc)
     * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
     */
    @Override
    public void stop ( final BundleContext bundleContext ) throws Exception
    {
        unregisterAllExportInformations ();

        this.tracker.close ();
        if ( this.exporter != null )
        {
            this.exporter.stop ();
            this.exporter = null;
        }
        Activator.context = null;
    }

}
