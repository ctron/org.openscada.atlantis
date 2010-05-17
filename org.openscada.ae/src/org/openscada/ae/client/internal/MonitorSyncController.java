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

package org.openscada.ae.client.internal;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.openscada.ae.ConditionStatusInformation;
import org.openscada.ae.client.ConditionListener;
import org.openscada.ae.client.Connection;
import org.openscada.core.subscription.SubscriptionState;

public class MonitorSyncController implements ConditionListener
{
    private final List<ConditionListener> listeners = new CopyOnWriteArrayList<ConditionListener> ();

    private final Connection connection;

    private final String id;

    private final Set<ConditionStatusInformation> cachedMonitors = CollectionsBackPort.<ConditionStatusInformation> newSetFromMap ( new ConcurrentHashMap<ConditionStatusInformation, Boolean> () );

    public MonitorSyncController ( final Connection connection, final String id )
    {
        if ( connection == null )
        {
            throw new IllegalArgumentException ( "connection is null" );
        }
        this.connection = connection;
        this.id = id;
        this.connection.setConditionListener ( this.id, this );
    }

    public void dataChanged ( final ConditionStatusInformation[] addedOrUpdated, final String[] removed )
    {
        if ( addedOrUpdated != null )
        {
            this.cachedMonitors.removeAll ( Arrays.asList ( addedOrUpdated ) );
            this.cachedMonitors.addAll ( Arrays.asList ( addedOrUpdated ) );
        }
        if ( removed != null )
        {
            final Set<ConditionStatusInformation> toRemove = new HashSet<ConditionStatusInformation> ();
            final List<String> removedList = Arrays.asList ( removed );
            for ( final ConditionStatusInformation monitor : this.cachedMonitors )
            {
                if ( removedList.contains ( monitor.getId () ) )
                {
                    toRemove.add ( monitor );
                }
            }
            for ( final ConditionStatusInformation monitor : toRemove )
            {
                this.cachedMonitors.remove ( monitor );
            }
        }
        for ( final ConditionListener listener : this.listeners )
        {
            listener.dataChanged ( addedOrUpdated, removed );
        }
    }

    public synchronized void addListener ( final ConditionListener listener )
    {
        this.listeners.add ( listener );
        listener.dataChanged ( this.cachedMonitors.toArray ( new ConditionStatusInformation[] {} ), null );
    }

    public synchronized boolean removeListener ( final ConditionListener listener )
    {
        this.listeners.remove ( listener );
        return this.listeners.size () == 0;
    }

    public void statusChanged ( final SubscriptionState state )
    {
        switch ( state )
        {
        case CONNECTED:
            for ( final ConditionListener listener : this.listeners )
            {
                listener.dataChanged ( this.cachedMonitors.toArray ( new ConditionStatusInformation[] {} ), null );
            }
            break;
        default:
            break;
        }
    }

    public void dispose ()
    {
        this.connection.setConditionListener ( this.id, null );
    }
}
