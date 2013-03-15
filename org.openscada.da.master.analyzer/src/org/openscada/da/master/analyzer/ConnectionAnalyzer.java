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

package org.openscada.da.master.analyzer;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.openscada.core.ConnectionInformation;
import org.openscada.core.Variant;
import org.openscada.core.client.Connection;
import org.openscada.core.client.ConnectionState;
import org.openscada.core.client.ConnectionStateListener;
import org.openscada.core.connection.provider.ConnectionService;
import org.openscada.core.connection.provider.info.ConnectionInformationProvider;
import org.openscada.core.info.StatisticEntry;
import org.openscada.core.server.OperationParameters;
import org.openscada.da.server.common.AttributeMode;
import org.openscada.da.server.common.chain.DataItemInputChained;
import org.openscada.da.server.common.chain.WriteHandler;
import org.openscada.da.server.common.exporter.StaticObjectExporter;
import org.openscada.da.server.common.osgi.factory.DataItemFactory;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class ConnectionAnalyzer implements ConnectionStateListener
{
    private final static long STATISTICS_DELAY = Long.getLong ( "org.openscada.da.master.analyzer.statisticsDelay", 5 );

    private final DataItemFactory factory;

    private final StaticObjectExporter<ConnectionAnalyzerStatus> exporter;

    private final ConnectionAnalyzerStatus value;

    private final ConnectionService service;

    private final ScheduledFuture<?> job;

    private final DataItemInputChained statisticsItem;

    public ConnectionAnalyzer ( final ScheduledExecutorService executor, final BundleContext context, final ServiceReference<?> reference, final ConnectionService service )
    {
        this.factory = new DataItemFactory ( context, executor, "org.openscada.da.master.analyzer.connectionService." + makeId ( reference ) );
        this.exporter = new StaticObjectExporter<ConnectionAnalyzerStatus> ( this.factory, ConnectionAnalyzerStatus.class, false, false, "state." );

        this.statisticsItem = this.factory.createInput ( "statistics", null );

        this.value = new ConnectionAnalyzerStatus ();

        this.value.setUri ( makeUri ( service ) );

        this.exporter.setTarget ( this.value );

        this.job = executor.scheduleWithFixedDelay ( new Runnable () {
            @Override
            public void run ()
            {
                refresh ();
            }
        }, 0, STATISTICS_DELAY, TimeUnit.SECONDS );

        this.factory.createOutput ( "connect", null, new WriteHandler () {

            @Override
            public void handleWrite ( final Variant value, final OperationParameters operationParameters ) throws Exception
            {
                ConnectionAnalyzer.this.handleConnect ();
            }
        } );

        this.factory.createOutput ( "disconnect", null, new WriteHandler () {

            @Override
            public void handleWrite ( final Variant value, final OperationParameters operationParameters ) throws Exception
            {
                ConnectionAnalyzer.this.handleDisconnect ();
            }
        } );

        this.service = service;

        stateChange ( service.getConnection (), service.getConnection ().getState (), null );
        service.getConnection ().addConnectionStateListener ( this );
    }

    protected void refresh ()
    {
        final Map<String, Variant> result = new HashMap<String, Variant> ();
        if ( this.service instanceof ConnectionInformationProvider )
        {
            final Collection<StatisticEntry> statistics = ( (ConnectionInformationProvider)this.service ).getStatistics ();

            for ( final StatisticEntry entry : statistics )
            {
                try
                {
                    result.put ( String.format ( "statistics.%s.current", entry.getLabel () ), Variant.valueOf ( entry.getValue ().getCurrent () ) );
                    result.put ( String.format ( "statistics.%s.min", entry.getLabel () ), Variant.valueOf ( entry.getValue ().getMinimum () ) );
                    result.put ( String.format ( "statistics.%s.max", entry.getLabel () ), Variant.valueOf ( entry.getValue ().getMaximum () ) );
                }
                catch ( final Exception e )
                {
                }
            }
            this.statisticsItem.updateData ( makeJson ( statistics ), result, AttributeMode.SET );
        }
    }

    private Variant makeJson ( final Collection<StatisticEntry> statistics )
    {
        final GsonBuilder builder = new GsonBuilder ();
        final Gson gson = builder.create ();
        return Variant.valueOf ( gson.toJson ( statistics ) );
    }

    /**
     * Get the uri of the connection service
     * 
     * @param service
     *            the service
     * @return the connection uri or <code>null</code>
     */
    private String makeUri ( final ConnectionService service )
    {
        if ( service != null )
        {
            final Connection connection = service.getConnection ();
            if ( connection != null )
            {
                final ConnectionInformation info = connection.getConnectionInformation ();
                if ( info != null )
                {
                    return info.toMaskedString ();
                }
            }
        }
        return null;
    }

    protected void handleDisconnect ()
    {
        final ConnectionService service = this.service;
        if ( service != null )
        {
            service.disconnect ();
        }
    }

    protected void handleConnect ()
    {
        final ConnectionService service = this.service;
        if ( service != null )
        {
            service.connect ();
        }
    }

    public void dispose ()
    {
        this.job.cancel ( false );
        this.service.getConnection ().removeConnectionStateListener ( this );
        this.factory.dispose ();
    }

    private static String makeId ( final ServiceReference<?> reference )
    {
        final Object id = reference.getProperty ( Constants.SERVICE_PID );
        if ( id instanceof String )
        {
            return (String)id;
        }

        return "" + reference.getProperty ( Constants.SERVICE_ID );
    }

    @Override
    public void stateChange ( final Connection connection, final ConnectionState state, final Throwable error )
    {
        this.value.setState ( state );
        this.value.setConnected ( state == ConnectionState.BOUND );
        this.exporter.setTarget ( this.value );
    }

}
