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

package org.eclipse.scada.ae.client.internal;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.eclipse.scada.ae.Event;
import org.eclipse.scada.ae.client.Connection;
import org.eclipse.scada.ae.client.EventListener;
import org.eclipse.scada.core.data.SubscriptionState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EventSyncController implements EventListener
{

    private final static Logger logger = LoggerFactory.getLogger ( EventSyncController.class );

    private final List<EventListener> listeners = new CopyOnWriteArrayList<EventListener> ();

    private final Connection connection;

    private final String id;

    private final Set<Event> cachedEvents = CollectionsBackPort.<Event> newSetFromMap ( new ConcurrentHashMap<Event, Boolean> () );

    public EventSyncController ( final Connection connection, final String id )
    {
        if ( connection == null )
        {
            throw new IllegalArgumentException ( "connection is null" );
        }
        this.connection = connection;
        this.id = id;
        this.connection.setEventListener ( this.id, this );
    }

    public synchronized void addListener ( final EventListener listener )
    {
        this.listeners.add ( listener );
        listener.dataChanged ( new ArrayList<Event> ( this.cachedEvents ) );
    }

    /**
     * returns true if no listeners left
     * 
     * @param listener
     * @return
     */
    public synchronized boolean removeListener ( final EventListener listener )
    {
        this.listeners.remove ( listener );
        return this.listeners.size () == 0;
    }

    @Override
    public void dataChanged ( final List<Event> addedEvents )
    {
        this.cachedEvents.removeAll ( addedEvents );
        this.cachedEvents.addAll ( addedEvents );
        for ( final EventListener listener : this.listeners )
        {
            listener.dataChanged ( addedEvents );
        }
    }

    @Override
    public void statusChanged ( final SubscriptionState state )
    {
        fireStateChange ( state );

        switch ( state )
        {
            case CONNECTED:
                for ( final EventListener listener : this.listeners )
                {
                    listener.dataChanged ( new ArrayList<Event> ( this.cachedEvents ) );
                }
                break;
            default:
                break;
        }
    }

    private void fireStateChange ( final SubscriptionState state )
    {
        for ( final EventListener listener : this.listeners )
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
        this.connection.setEventListener ( this.id, null );
    }
}
