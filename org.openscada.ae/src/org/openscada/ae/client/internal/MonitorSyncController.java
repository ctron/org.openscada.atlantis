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

package org.openscada.ae.client.internal;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.eclipse.scada.ae.data.MonitorStatusInformation;
import org.eclipse.scada.core.data.SubscriptionState;
import org.openscada.ae.client.Connection;
import org.openscada.ae.client.MonitorListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MonitorSyncController implements MonitorListener
{

    private final static Logger logger = LoggerFactory.getLogger ( MonitorSyncController.class );

    private final List<MonitorListener> listeners = new CopyOnWriteArrayList<MonitorListener> ();

    private final Connection connection;

    private final String id;

    private final Set<MonitorStatusInformation> cachedMonitors = CollectionsBackPort.<MonitorStatusInformation> newSetFromMap ( new ConcurrentHashMap<MonitorStatusInformation, Boolean> () );

    public MonitorSyncController ( final Connection connection, final String id )
    {
        if ( connection == null )
        {
            throw new IllegalArgumentException ( "connection is null" );
        }
        this.connection = connection;
        this.id = id;
        this.connection.setMonitorListener ( this.id, this );
    }

    @Override
    public void dataChanged ( final List<MonitorStatusInformation> addedOrUpdated, final Set<String> removed, final boolean full )
    {
        if ( full )
        {
            this.cachedMonitors.clear ();
        }
        if ( addedOrUpdated != null )
        {
            this.cachedMonitors.removeAll ( addedOrUpdated );
            this.cachedMonitors.addAll ( addedOrUpdated );
        }
        if ( removed != null )
        {
            final Set<MonitorStatusInformation> toRemove = new HashSet<MonitorStatusInformation> ();
            for ( final MonitorStatusInformation monitor : this.cachedMonitors )
            {
                if ( removed.contains ( monitor.getId () ) )
                {
                    toRemove.add ( monitor );
                }
            }
            for ( final MonitorStatusInformation monitor : toRemove )
            {
                this.cachedMonitors.remove ( monitor );
            }
        }

        // forward events
        for ( final MonitorListener listener : this.listeners )
        {
            listener.dataChanged ( addedOrUpdated, removed, full );
        }
    }

    public synchronized void addListener ( final MonitorListener listener )
    {
        this.listeners.add ( listener );
        listener.dataChanged ( new ArrayList<MonitorStatusInformation> ( this.cachedMonitors ), null, true );
    }

    public synchronized boolean removeListener ( final MonitorListener listener )
    {
        this.listeners.remove ( listener );
        return this.listeners.size () == 0;
    }

    @Override
    public void statusChanged ( final SubscriptionState state )
    {
        fireStateChange ( state );

        switch ( state )
        {
            case CONNECTED:
                for ( final MonitorListener listener : this.listeners )
                {
                    listener.dataChanged ( new ArrayList<MonitorStatusInformation> ( this.cachedMonitors ), null, true );
                }
                break;
            default:
                break;
        }
    }

    private void fireStateChange ( final SubscriptionState state )
    {
        for ( final MonitorListener listener : this.listeners )
        {
            try
            {
                listener.statusChanged ( state );
            }
            catch ( final Exception e )
            {
                logger.debug ( "Failed to notify subscription change", e );
            }
        }
    }

    public void dispose ()
    {
        this.connection.setMonitorListener ( this.id, null );
    }
}
