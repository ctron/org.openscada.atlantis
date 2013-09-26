/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2010 TH4 SYSTEMS GmbH (http://th4-systems.com)
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

package org.eclipse.scada.ae.client;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.scada.ae.client.internal.MonitorSyncController;
import org.eclipse.scada.core.client.ConnectionState;
import org.eclipse.scada.core.client.ConnectionStateListener;

public class MonitorManager implements ConnectionStateListener
{
    private final Connection connection;

    private boolean connected;

    private final Map<String, MonitorSyncController> monitorListeners = new HashMap<String, MonitorSyncController> ();

    public MonitorManager ( final Connection connection )
    {
        super ();
        if ( connection == null )
        {
            throw new IllegalArgumentException ( "connection is null" );
        }
        this.connection = connection;

        synchronized ( this )
        {
            this.connection.addConnectionStateListener ( this );
            this.connected = this.connection.getState () == ConnectionState.BOUND;
        }
    }

    @Override
    public void stateChange ( final org.eclipse.scada.core.client.Connection connection, final ConnectionState state, final Throwable error )
    {
        switch ( state )
        {
            case BOUND:
                if ( !this.connected )
                {
                    this.connected = true;
                }
                break;
            case CLOSED:
                for ( final MonitorSyncController controller : this.monitorListeners.values () )
                {
                    controller.dispose ();
                }
                this.monitorListeners.clear ();
                //$FALL-THROUGH$
            default:
                if ( this.connected )
                {
                    this.connected = false;
                }
                break;
        }
    }

    public synchronized void addMonitorListener ( final String id, final MonitorListener listener )
    {
        MonitorSyncController monitorSyncController = this.monitorListeners.get ( id );
        if ( monitorSyncController == null )
        {
            monitorSyncController = new MonitorSyncController ( this.connection, id );
            this.monitorListeners.put ( id, monitorSyncController );
        }
        monitorSyncController.addListener ( listener );
    }

    public synchronized void removeMonitorListener ( final String id, final MonitorListener listener )
    {
        final MonitorSyncController monitorSyncController = this.monitorListeners.get ( id );
        if ( monitorSyncController == null )
        {
            return;
        }
        monitorSyncController.removeListener ( listener );
    }

    public boolean isConnected ()
    {
        return this.connected;
    }
}
