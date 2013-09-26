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

package org.eclipse.scada.ae.server.common.monitor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.scada.ae.data.MonitorStatusInformation;
import org.eclipse.scada.ae.server.MonitorListener;
import org.eclipse.scada.core.subscription.SubscriptionInformation;
import org.eclipse.scada.core.subscription.SubscriptionSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MonitorQuerySource implements SubscriptionSource, MonitorQueryListener
{
    private final static Logger logger = LoggerFactory.getLogger ( MonitorQuerySource.class );

    private final MonitorQuery monitorQuery;

    private final Set<MonitorListener> listeners = new HashSet<MonitorListener> ();

    private final Map<String, MonitorStatusInformation> cachedData = new HashMap<String, MonitorStatusInformation> ();

    private final String queryId;

    public MonitorQuerySource ( final String queryId, final MonitorQuery monitorQuery )
    {
        this.queryId = queryId;
        this.monitorQuery = monitorQuery;
    }

    @Override
    public synchronized void addListener ( final Collection<SubscriptionInformation> listeners )
    {
        final boolean wasEmpty = this.listeners.isEmpty ();

        for ( final SubscriptionInformation information : listeners )
        {
            final MonitorListener listener = (MonitorListener)information.getListener ();
            this.listeners.add ( listener );

            if ( !this.cachedData.isEmpty () )
            {
                listener.dataChanged ( this.queryId, new ArrayList<MonitorStatusInformation> ( this.cachedData.values () ), null, true );
            }
        }

        if ( wasEmpty && !this.listeners.isEmpty () )
        {
            this.monitorQuery.addListener ( this );
        }
    }

    @Override
    public synchronized void removeListener ( final Collection<SubscriptionInformation> listeners )
    {
        for ( final SubscriptionInformation information : listeners )
        {
            final MonitorListener listener = (MonitorListener)information.getListener ();
            this.listeners.remove ( listener );
        }

        if ( this.listeners.isEmpty () )
        {
            this.monitorQuery.removeListener ( this );
            this.cachedData.clear ();
        }
    }

    @Override
    public boolean supportsListener ( final SubscriptionInformation subscriptionInformation )
    {
        return subscriptionInformation.getListener () instanceof MonitorListener;
    }

    @Override
    public synchronized void dataChanged ( final List<MonitorStatusInformation> addedOrUpdated, final Set<String> removed, final boolean full )
    {
        if ( full )
        {
            this.cachedData.clear ();
        }
        if ( removed != null )
        {
            for ( final String id : removed )
            {
                this.cachedData.remove ( id );
            }
        }
        if ( addedOrUpdated != null )
        {
            for ( final MonitorStatusInformation info : addedOrUpdated )
            {
                this.cachedData.put ( info.getId (), info );
            }
        }
        for ( final MonitorListener listener : this.listeners )
        {
            try
            {
                listener.dataChanged ( this.queryId, addedOrUpdated, removed, full );
            }
            catch ( final Throwable e )
            {
                logger.warn ( "Failed to notify", e );
            }
        }
    }
}
