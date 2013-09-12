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

package org.openscada.core.server.exporter.advertiser.ecf;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.eclipse.ecf.core.ContainerConnectException;
import org.eclipse.ecf.core.ContainerCreateException;
import org.eclipse.ecf.core.ContainerFactory;
import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.discovery.IDiscoveryAdvertiser;
import org.eclipse.ecf.discovery.IServiceInfo;
import org.eclipse.ecf.discovery.ServiceInfo;
import org.eclipse.ecf.discovery.identity.IServiceTypeID;
import org.eclipse.ecf.discovery.identity.ServiceIDFactory;
import org.eclipse.scada.core.ConnectionInformation;
import org.eclipse.scada.utils.concurrent.NamedThreadFactory;
import org.openscada.core.server.exporter.ExporterInformation;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Advertiser
{

    private final static Logger logger = LoggerFactory.getLogger ( Advertiser.class );

    public static final String DISCOVERY_CONTAINER = "ecf.discovery.composite"; //$NON-NLS-1$

    private IContainer container;

    private IDiscoveryAdvertiser advertiser;

    private ServiceTracker<ExporterInformation, ExporterInformation> tracker;

    private final Map<ExporterInformation, IServiceInfo> infoMap = new HashMap<ExporterInformation, IServiceInfo> ();

    private ExecutorService executor;

    public Advertiser ()
    {
    }

    public void start ( final BundleContext context )
    {
        this.executor = Executors.newSingleThreadExecutor ( new NamedThreadFactory ( "ECF/Advertiser", true ) );

        try
        {
            connect ();
        }
        catch ( final Exception e )
        {
            logger.warn ( "Failed to start advertiser", e );
            return;
        }

        this.tracker = new ServiceTracker<ExporterInformation, ExporterInformation> ( context, ExporterInformation.class, new ServiceTrackerCustomizer<ExporterInformation, ExporterInformation> () {

            @Override
            public void removedService ( final ServiceReference<ExporterInformation> reference, final ExporterInformation service )
            {
                Advertiser.this.executor.execute ( new Runnable () {

                    @Override
                    public void run ()
                    {
                        Advertiser.this.removedService ( service );
                    }

                } );
            }

            @Override
            public void modifiedService ( final ServiceReference<ExporterInformation> reference, final ExporterInformation service )
            {
            }

            @Override
            public ExporterInformation addingService ( final ServiceReference<ExporterInformation> reference )
            {
                return performAdd ( context, reference );
            }
        } );
        this.tracker.open ();
    }

    protected void addingService ( final ExporterInformation service ) throws URISyntaxException
    {
        logger.info ( "Found new exported service: {}", service );

        final String interfaceName = service.getConnectionInformation ().getInterface ();
        final String protocolName = service.getConnectionInformation ().getDriver ();
        final String osType = String.format ( "openscada_%s_%s", interfaceName, protocolName );
        final IServiceTypeID typeId = ServiceIDFactory.getDefault ().createServiceTypeID ( this.advertiser.getServicesNamespace (), new String[] { osType } );

        final ConnectionInformation ci = service.getConnectionInformation ();

        String description;
        if ( service.getDescription () != null )
        {
            description = service.getDescription ();
        }
        else
        {
            description = ci.toString ();
        }

        final URI uri = new URI ( "openscada", null, ci.getTarget (), ci.getSecondaryTarget (), null, null, null );

        final IServiceInfo serviceInfo = new ServiceInfo ( uri, description, typeId );
        this.advertiser.registerService ( serviceInfo );

        final IServiceInfo oldServiceInfo = this.infoMap.put ( service, serviceInfo );
        if ( oldServiceInfo != null )
        {
            logger.warn ( "Removing duplicate entry: {}", service );
            this.advertiser.unregisterService ( oldServiceInfo );
        }
    }

    protected void removedService ( final ExporterInformation service )
    {
        logger.info ( "Removing exported service: {}", service );

        final IServiceInfo serviceInfo = this.infoMap.remove ( service );
        if ( serviceInfo != null )
        {
            this.advertiser.unregisterService ( serviceInfo );
        }
    }

    public void stop ()
    {
        this.tracker.close ();
        disconnect ();

        logger.info ( "Shutting down executor" );
        this.executor.shutdown ();
    }

    private void connect () throws ContainerCreateException, ContainerConnectException
    {
        this.container = ContainerFactory.getDefault ().createContainer ( DISCOVERY_CONTAINER, new Object[] { "ecf.discovery.composite.locator" } ); //$NON-NLS-1$
        this.advertiser = (IDiscoveryAdvertiser)this.container.getAdapter ( IDiscoveryAdvertiser.class );
        if ( this.advertiser != null )
        {
            this.container.connect ( null, null );
        }
    }

    public void disconnect ()
    {
        this.advertiser.unregisterAllServices ();
        this.container.disconnect ();
        this.container.dispose ();
    }

    private ExporterInformation performAdd ( final BundleContext context, final ServiceReference<ExporterInformation> reference )
    {
        final ExporterInformation info = context.getService ( reference );
        logger.info ( "Export information: {}", info );
        try
        {
            this.executor.execute ( new Runnable () {

                @Override
                public void run ()
                {
                    try
                    {
                        Advertiser.this.addingService ( info );
                    }
                    catch ( final URISyntaxException e )
                    {
                        logger.warn ( "Failed to register", e );
                    }
                }
            } );

            return info;
        }
        catch ( final Exception e )
        {
            logger.warn ( "Failed to add", e );
            context.ungetService ( reference );
            return null;
        }
    }
}
