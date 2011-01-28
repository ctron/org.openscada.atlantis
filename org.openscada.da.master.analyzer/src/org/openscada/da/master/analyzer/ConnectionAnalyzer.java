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

package org.openscada.da.master.analyzer;

import java.util.concurrent.Executor;

import org.openscada.core.Variant;
import org.openscada.core.client.Connection;
import org.openscada.core.client.ConnectionState;
import org.openscada.core.client.ConnectionStateListener;
import org.openscada.core.connection.provider.ConnectionService;
import org.openscada.da.core.OperationParameters;
import org.openscada.da.server.common.chain.WriteHandler;
import org.openscada.da.server.common.osgi.factory.DataItemFactory;
import org.openscada.da.server.common.osgi.factory.SimpleObjectExporter;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;

public class ConnectionAnalyzer implements ConnectionStateListener
{

    private final DataItemFactory factory;

    private final SimpleObjectExporter<ConnectionAnalyzerStatus> exporter;

    private final ConnectionAnalyzerStatus value;

    private final ConnectionService service;

    public ConnectionAnalyzer ( final Executor executor, final BundleContext context, final ServiceReference reference, final ConnectionService service )
    {
        this.factory = new DataItemFactory ( context, executor, "org.openscada.da.master.analyzer.connectionService." + makeId ( reference ) );
        this.exporter = new SimpleObjectExporter<ConnectionAnalyzerStatus> ( ConnectionAnalyzerStatus.class, this.factory, "state" );

        this.value = new ConnectionAnalyzerStatus ();
        this.exporter.setValue ( this.value );

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
        this.service.getConnection ().removeConnectionStateListener ( this );
        this.factory.dispose ();
    }

    private static String makeId ( final ServiceReference reference )
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
        this.exporter.setValue ( this.value );
    }

}
