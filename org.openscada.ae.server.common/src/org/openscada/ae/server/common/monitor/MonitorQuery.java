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

package org.openscada.ae.server.common.monitor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executor;

import org.openscada.ae.MonitorStatusInformation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MonitorQuery
{
    private final static Logger logger = LoggerFactory.getLogger ( MonitorQuery.class );

    private final Set<MonitorQueryListener> listeners;

    private final Map<String, MonitorStatusInformation> cachedData;

    private final Executor executor;

    public MonitorQuery ( final Executor executor )
    {
        this.executor = executor;
        this.cachedData = new HashMap<String, MonitorStatusInformation> ();
        this.listeners = new HashSet<MonitorQueryListener> ();
    }

    /**
     * Add a listener to the monitor query
     * <p>
     * If the listener was already added this operation has no effect
     * </p>
     * <p>
     * The listener will receive all current known elements in a first update call.
     * </p>
     * @param listener the listener to add
     */
    public synchronized void addListener ( final MonitorQueryListener listener )
    {
        if ( this.listeners.add ( listener ) )
        {
            final MonitorStatusInformation[] data = this.cachedData.values ().toArray ( new MonitorStatusInformation[0] );
            listener.dataChanged ( data, null );
        }
    }

    public synchronized void removeListener ( final MonitorQueryListener listener )
    {
        this.listeners.remove ( listener );
    }

    private synchronized void fireListener ( final MonitorStatusInformation[] addedOrUpdated, final String[] removed )
    {
        for ( final MonitorQueryListener listener : this.listeners )
        {
            this.executor.execute ( new Runnable () {

                @Override
                public void run ()
                {
                    try
                    {
                        listener.dataChanged ( addedOrUpdated, removed );
                    }
                    catch ( final Exception e )
                    {
                        logger.warn ( "Failed to notify", e );
                    }
                }
            } );
        }
    }

    protected synchronized void updateData ( final MonitorStatusInformation[] data, final String[] removed )
    {
        if ( data != null )
        {
            for ( final MonitorStatusInformation info : data )
            {
                this.cachedData.put ( info.getId (), info );
            }
        }
        final Set<String> removedItems = new HashSet<String> ();
        if ( removed != null )
        {
            for ( final String entry : removed )
            {
                if ( this.cachedData.remove ( entry ) != null )
                {
                    removedItems.add ( entry );
                }
            }
        }
        fireListener ( data, removedItems.toArray ( new String[removedItems.size ()] ) );
    }

    public synchronized void dispose ()
    {
        clear ();
        this.listeners.clear ();
    }

    /**
     * Set current data set. Will handle notifications accordingly.
     * @param data the new data set
     */
    protected synchronized void setData ( final MonitorStatusInformation[] data )
    {
        logger.debug ( "Set new data: {}", data.length );

        clear ();

        final ArrayList<MonitorStatusInformation> newData = new ArrayList<MonitorStatusInformation> ( data.length );
        for ( final MonitorStatusInformation ci : data )
        {
            newData.add ( ci );
            final MonitorStatusInformation oldCi = this.cachedData.put ( ci.getId (), ci );
            if ( oldCi != null )
            {
                newData.remove ( oldCi );
            }
        }
        fireListener ( newData.toArray ( new MonitorStatusInformation[newData.size ()] ), null );
    }

    protected synchronized void clear ()
    {
        fireListener ( null, this.cachedData.keySet ().toArray ( new String[this.cachedData.size ()] ) );
        this.cachedData.clear ();
    }
}
