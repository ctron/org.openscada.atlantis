/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2010 TH4 SYSTEMS GmbH (http://inavare.com)
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

package org.openscada.ae.client;

import java.util.HashMap;
import java.util.Map;

import org.openscada.ae.client.internal.EventSyncController;
import org.openscada.core.client.ConnectionState;
import org.openscada.core.client.ConnectionStateListener;

public class EventManager implements ConnectionStateListener
{
    private final Connection connection;

    private boolean connected;

    private final Map<String, EventSyncController> eventListeners = new HashMap<String, EventSyncController> ();

    public EventManager ( final Connection connection )
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

    public void stateChange ( final org.openscada.core.client.Connection connection, final ConnectionState state, final Throwable error )
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
            for ( final EventSyncController controller : this.eventListeners.values () )
            {
                controller.dispose ();
            }
            this.eventListeners.clear ();
        default:
            if ( this.connected )
            {
                this.connected = false;
            }
            break;
        }
    }

    public synchronized void addEventListener ( final String id, final EventListener listener )
    {
        EventSyncController eventSyncController = this.eventListeners.get ( id );
        if ( eventSyncController == null )
        {
            eventSyncController = new EventSyncController ( this.connection, id );
            this.eventListeners.put ( id, eventSyncController );
        }
        eventSyncController.addListener ( listener );
    }

    public synchronized void removeEventListener ( final String id, final EventListener listener )
    {
        final EventSyncController eventSyncController = this.eventListeners.get ( id );
        if ( eventSyncController == null )
        {
            return;
        }
        eventSyncController.removeListener ( listener );
    }

    public boolean isConnected ()
    {
        return this.connected;
    }
}
