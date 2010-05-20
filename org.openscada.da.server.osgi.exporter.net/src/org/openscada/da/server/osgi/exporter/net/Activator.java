/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2010 inavare GmbH (http://inavare.com)
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

package org.openscada.da.server.osgi.exporter.net;

import org.apache.log4j.Logger;
import org.openscada.core.ConnectionInformation;
import org.openscada.core.server.exporter.ExporterInformation;
import org.openscada.da.core.server.Hive;
import org.openscada.da.server.net.Exporter;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;

public class Activator implements BundleActivator
{
    private final static Logger logger = Logger.getLogger ( Activator.class );

    private ServiceListener listener;

    private ServiceReference currentServiceReference;

    private BundleContext context;

    private final ConnectionInformation connectionInformation = ConnectionInformation.fromURI ( System.getProperty ( "openscada.da.net.exportUri", "da:net://0.0.0.0:1202" ) );

    private Exporter exporter;

    private Hive currentService;

    private ServiceRegistration exporterHandle;

    /*
     * (non-Javadoc)
     * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
     */
    public void start ( final BundleContext context ) throws Exception
    {
        this.context = context;

        context.addServiceListener ( this.listener = new ServiceListener () {

            public void serviceChanged ( final ServiceEvent event )
            {
                switch ( event.getType () )
                {
                case ServiceEvent.REGISTERED:
                    Activator.this.startExporter ( event.getServiceReference () );
                    break;
                case ServiceEvent.UNREGISTERING:
                    Activator.this.stopExporter ( event.getServiceReference () );
                    break;
                }
            }
        }, "(" + Constants.OBJECTCLASS + "=" + Hive.class.getName () + ")" );

        startExporter ( context.getServiceReference ( Hive.class.getName () ) );
    }

    protected void stopExporter ( final ServiceReference serviceReference )
    {
        if ( this.currentServiceReference != serviceReference )
        {
            return;
        }

        try
        {
            if ( this.exporterHandle != null )
            {
                this.exporterHandle.unregister ();
                this.exporterHandle = null;
            }

            this.exporter.stop ();
        }
        catch ( final Throwable e )
        {
            e.printStackTrace ();
        }
        finally
        {
            this.context.ungetService ( this.currentServiceReference );
            this.currentService = null;
            this.exporter = null;
            this.currentServiceReference = null;
        }

    }

    protected void startExporter ( final ServiceReference serviceReference )
    {
        if ( this.currentServiceReference != null || serviceReference == null )
        {
            return;
        }

        final Object o = this.context.getService ( serviceReference );
        if ( o instanceof Hive )
        {
            try
            {
                logger.info ( "Exporting: " + serviceReference );
                this.currentService = (Hive)o;
                this.exporter = new Exporter ( this.currentService, this.connectionInformation );
                this.exporter.start ();

                final ExporterInformation info = new ExporterInformation ( this.connectionInformation, null );
                this.exporterHandle = this.context.registerService ( ExporterInformation.class.getName (), info, null );
            }
            catch ( final Throwable e )
            {
                e.printStackTrace ();
                this.exporter = null;
                this.currentService = null;
                this.context.ungetService ( serviceReference );
            }
        }
        else
        {
            this.context.ungetService ( serviceReference );
        }

    }

    /*
     * (non-Javadoc)
     * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
     */
    public void stop ( final BundleContext context ) throws Exception
    {
        if ( this.exporterHandle != null )
        {
            this.exporterHandle.unregister ();
            this.exporterHandle = null;
        }

        context.removeServiceListener ( this.listener );
        this.context = null;
    }

}
