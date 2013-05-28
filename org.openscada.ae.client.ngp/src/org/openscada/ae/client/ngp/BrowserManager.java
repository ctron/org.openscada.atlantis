/*
 * This file is part of the openSCADA project
 * Copyright (C) 2011-2012 TH4 SYSTEMS GmbH (http://th4-systems.com)
 *
 * openSCADA is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 3
 * only, as published by the Free Software Foundation.
 *
 * openSCADA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License version 3 for more details
 * (a copy is included in the LICENSE file that accompanied this code).
 *
 * You should have received a copy of the GNU Lesser General Public License
 * version 3 along with openSCADA. If not, see
 * <http://opensource.org/licenses/lgpl-3.0.html> for a copy of the LGPLv3 License.
 */

package org.openscada.ae.client.ngp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executor;

import org.openscada.ae.BrowserListener;
import org.openscada.ae.data.BrowserEntry;

public class BrowserManager
{

    private final Set<BrowserListener> browserListeners = new LinkedHashSet<BrowserListener> ();

    private final ConnectionImpl connection;

    private final Executor executor;

    private final Map<String, BrowserEntry> currentEntries = new HashMap<String, BrowserEntry> ();

    public BrowserManager ( final Executor executor, final ConnectionImpl connection )
    {
        this.executor = executor;
        this.connection = connection;
    }

    public void addBrowserListener ( final BrowserListener listener )
    {
        if ( listener == null )
        {
            throw new NullPointerException ();
        }

        final boolean wasEmpty = this.browserListeners.isEmpty ();
        if ( this.browserListeners.add ( listener ) )
        {
            if ( wasEmpty )
            {
                this.connection.sendStartBrowse ();

            }

            final List<BrowserEntry> values = new ArrayList<BrowserEntry> ( this.currentEntries.values () );

            this.executor.execute ( new Runnable () {

                @Override
                public void run ()
                {
                    listener.dataChanged ( values, Collections.<String> emptySet (), true );
                }
            } );
        }
    }

    public void removeBrowserListener ( final BrowserListener listener )
    {
        if ( this.browserListeners.remove ( listener ) )
        {
            if ( this.browserListeners.isEmpty () )
            {
                // we removed some entry and now we are empty
                this.connection.sendStopBrowse ();
            }
        }
    }

    public void dispose ()
    {

    }

    public void onBound ()
    {
        if ( !this.browserListeners.isEmpty () )
        {
            this.connection.sendStartBrowse ();
        }
    }

    public void onClosed ()
    {
        setCurrentEntries ( Collections.<BrowserEntry> emptyList (), null, true );
    }

    private void setCurrentEntries ( final List<BrowserEntry> addedOrUpdated, final Set<String> removed, final boolean full )
    {
        if ( full )
        {
            this.currentEntries.clear ();
        }

        if ( addedOrUpdated != null )
        {
            for ( final BrowserEntry entry : addedOrUpdated )
            {
                this.currentEntries.put ( entry.getId (), entry );
            }
        }

        if ( removed != null )
        {
            for ( final String id : removed )
            {
                this.currentEntries.remove ( id );
            }
        }

        for ( final BrowserListener listener : this.browserListeners )
        {
            this.executor.execute ( new Runnable () {
                @Override
                public void run ()
                {
                    listener.dataChanged ( addedOrUpdated, removed, full );
                };
            } );
        }
    }

    public void updateData ( final List<BrowserEntry> addedOrUpdated, final Set<String> removed )
    {
        setCurrentEntries ( addedOrUpdated, removed, false );
    }
}
