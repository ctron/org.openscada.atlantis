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

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.openscada.ae.MonitorStatusInformation;
import org.openscada.ae.client.Connection;
import org.openscada.ae.client.MonitorListener;
import org.openscada.core.subscription.SubscriptionState;
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
    public void dataChanged ( final MonitorStatusInformation[] addedOrUpdated, final String[] removed )
    {
        if ( addedOrUpdated != null )
        {
            this.cachedMonitors.removeAll ( Arrays.asList ( addedOrUpdated ) );
            this.cachedMonitors.addAll ( Arrays.asList ( addedOrUpdated ) );
        }
        if ( removed != null )
        {
            final Set<MonitorStatusInformation> toRemove = new HashSet<MonitorStatusInformation> ();
            final List<String> removedList = Arrays.asList ( removed );
            for ( final MonitorStatusInformation monitor : this.cachedMonitors )
            {
                if ( removedList.contains ( monitor.getId () ) )
                {
                    toRemove.add ( monitor );
                }
            }
            for ( final MonitorStatusInformation monitor : toRemove )
            {
                this.cachedMonitors.remove ( monitor );
            }
        }
        for ( final MonitorListener listener : this.listeners )
        {
            listener.dataChanged ( addedOrUpdated, removed );
        }
    }

    public synchronized void addListener ( final MonitorListener listener )
    {
        this.listeners.add ( listener );
        listener.dataChanged ( this.cachedMonitors.toArray ( new MonitorStatusInformation[] {} ), null );
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
                    listener.dataChanged ( this.cachedMonitors.toArray ( new MonitorStatusInformation[] {} ), null );
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
