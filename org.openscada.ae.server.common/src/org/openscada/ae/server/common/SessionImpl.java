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

package org.openscada.ae.server.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.openscada.ae.BrowserEntry;
import org.openscada.ae.BrowserListener;
import org.openscada.ae.Event;
import org.openscada.ae.MonitorStatusInformation;
import org.openscada.ae.server.EventListener;
import org.openscada.ae.server.MonitorListener;
import org.openscada.ae.server.Session;
import org.openscada.core.server.common.session.AbstractSessionImpl;
import org.openscada.core.subscription.SubscriptionState;
import org.openscada.sec.UserInformation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SessionImpl extends AbstractSessionImpl implements Session, BrowserListener
{

    private final static Logger logger = LoggerFactory.getLogger ( SessionImpl.class );

    private final EventListener eventListener;

    private final MonitorListener monitorListener;

    private volatile MonitorListener clientConditionListener;

    private volatile EventListener clientEventListener;

    private volatile BrowserListener clientBrowserListener;

    private final Map<String, BrowserEntry> browserCache = new HashMap<String, BrowserEntry> ();

    private boolean disposed = false;

    private final Set<QueryImpl> queries = new HashSet<QueryImpl> ();

    public SessionImpl ( final UserInformation userInformation, final Map<String, String> properties )
    {
        super ( userInformation, properties );
        logger.info ( "Created new session" );

        this.eventListener = new EventListener () {

            @Override
            public void dataChanged ( final String poolId, final Event[] addedEvents )
            {
                SessionImpl.this.eventDataChanged ( poolId, addedEvents );
            }

            @Override
            public void updateStatus ( final Object poolId, final SubscriptionState state )
            {
                SessionImpl.this.eventStatusChanged ( poolId.toString (), state );
            }
        };
        this.monitorListener = new MonitorListener () {

            @Override
            public void dataChanged ( final String subscriptionId, final MonitorStatusInformation[] addedOrUpdated, final String[] removed )
            {
                SessionImpl.this.conditionDataChanged ( subscriptionId, addedOrUpdated, removed );
            }

            @Override
            public void updateStatus ( final Object poolId, final SubscriptionState state )
            {
                SessionImpl.this.conditionStatusChanged ( poolId.toString (), state );
            }
        };
    }

    protected void conditionStatusChanged ( final String string, final SubscriptionState state )
    {
        final MonitorListener listener = this.clientConditionListener;
        if ( listener != null )
        {
            listener.updateStatus ( string, state );
        }
    }

    protected void conditionDataChanged ( final String subscriptionId, final MonitorStatusInformation[] addedOrUpdated, final String[] removed )
    {
        final MonitorListener listener = this.clientConditionListener;
        if ( listener != null )
        {
            logger.info ( String.format ( "Condition Data Change: %s - %s - %s", subscriptionId, addedOrUpdated != null ? addedOrUpdated.length : "none", removed != null ? removed.length : "none" ) );
            listener.dataChanged ( subscriptionId, addedOrUpdated, removed );
        }
    }

    protected void eventStatusChanged ( final String string, final SubscriptionState state )
    {
        final EventListener listener = this.clientEventListener;
        if ( listener != null )
        {
            listener.updateStatus ( string, state );
        }
    }

    protected void eventDataChanged ( final String poolId, final Event[] addedEvents )
    {
        final EventListener listener = this.clientEventListener;
        if ( listener != null )
        {
            listener.dataChanged ( poolId, translateEvents ( addedEvents ) );
        }
    }

    /**
     * Translate the events into the current session language
     * @param events the events to translate
     * @return a new array of translated events
     */
    protected Event[] translateEvents ( final Event[] events )
    {
        return events;
    }

    @Override
    public void setConditionListener ( final MonitorListener listener )
    {
        this.clientConditionListener = listener;
    }

    @Override
    public void setEventListener ( final EventListener listener )
    {
        this.clientEventListener = listener;
    }

    public synchronized void dispose ()
    {
        logger.info ( "Disposing session" );

        // mark disposed
        this.disposed = true;

        // dispose queries : operate on copy to prevent concurrent modification
        for ( final QueryImpl query : new ArrayList<QueryImpl> ( this.queries ) )
        {
            query.dispose ( null );
        }
        this.queries.clear ();

        // clear listeners
        this.clientConditionListener = null;
        this.clientEventListener = null;
        this.clientBrowserListener = null;
    }

    public MonitorListener getConditionListener ()
    {
        return this.monitorListener;
    }

    public EventListener getEventListener ()
    {
        return this.eventListener;
    }

    @Override
    public void setBrowserListener ( final BrowserListener listener )
    {
        synchronized ( this.browserCache )
        {
            this.clientBrowserListener = listener;
            if ( this.clientBrowserListener != null )
            {
                this.clientBrowserListener.dataChanged ( this.browserCache.values ().toArray ( new BrowserEntry[0] ), null, true );
            }
        }
    }

    @Override
    public void dataChanged ( final BrowserEntry[] addedOrUpdated, final String[] removed, final boolean full )
    {
        synchronized ( this.browserCache )
        {
            if ( full )
            {
                this.browserCache.clear ();
            }
            if ( removed != null )
            {
                for ( final String id : removed )
                {
                    this.browserCache.remove ( id );
                }
            }
            if ( addedOrUpdated != null )
            {
                for ( final BrowserEntry entry : addedOrUpdated )
                {
                    this.browserCache.put ( entry.getId (), entry );
                }
            }

            final BrowserListener listener = this.clientBrowserListener;
            if ( listener != null )
            {
                listener.dataChanged ( addedOrUpdated, removed, full );
            }
        }
    }

    public synchronized void addQuery ( final QueryImpl query )
    {
        if ( this.disposed )
        {
            query.dispose ( null );
        }
        else
        {
            this.queries.add ( query );
        }
    }

    public synchronized void removeQuery ( final QueryImpl query )
    {
        this.queries.remove ( query );
    }
}
