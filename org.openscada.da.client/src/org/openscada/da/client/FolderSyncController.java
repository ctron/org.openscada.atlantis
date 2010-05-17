/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2009 inavare GmbH (http://inavare.com)
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package org.openscada.da.client;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.openscada.da.core.Location;
import org.openscada.da.core.browser.Entry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FolderSyncController extends FolderWatcher
{
    private final static Logger logger = LoggerFactory.getLogger ( FolderSyncController.class );

    private final Set<FolderListener> listener = new CopyOnWriteArraySet<FolderListener> ();

    private final Connection connection;

    private boolean subscribed = false;

    public FolderSyncController ( final Connection connection, final Location location )
    {
        super ( location );
        this.connection = connection;
        this.connection.setFolderListener ( location, this );
    }

    public void addListener ( final FolderListener listener )
    {
        if ( this.listener.add ( listener ) )
        {
            sync ();
        }
        transmitCache ( listener );
    }

    public void removeListener ( final FolderListener listener )
    {
        if ( this.listener.remove ( listener ) )
        {
            sync ();
        }
    }

    public void sync ()
    {
        sync ( false );
    }

    public void resync ()
    {
        sync ( true );
    }

    private void sync ( final boolean force )
    {
        synchronized ( this )
        {
            final boolean needSubscription = this.listener.size () > 0;

            if ( needSubscription != this.subscribed || force )
            {
                if ( needSubscription )
                {
                    subscribe ();
                }
                else
                {
                    unsubscribe ();
                }
            }
        }
    }

    private synchronized void subscribe ()
    {
        logger.debug ( "subscribing to folder: " + this.location.toString () );

        this.subscribed = true;

        try
        {
            this.connection.subscribeFolder ( this.location );
        }
        catch ( final Exception e )
        {
            handleError ( e );
        }
    }

    private synchronized void unsubscribe ()
    {
        logger.debug ( "unsubscribing from folder: " + this.location.toString () );

        this.subscribed = false;

        try
        {
            this.connection.unsubscribeFolder ( this.location );
        }
        catch ( final Exception e )
        {
            handleError ( e );
        }
    }

    protected void handleError ( final Throwable e )
    {
        this.subscribed = false;
    }

    private void transmitCache ( final FolderListener listener )
    {
        listener.folderChanged ( this.cache.values (), new LinkedList<String> (), true );
    }

    @Override
    public void folderChanged ( final Collection<Entry> added, final Collection<String> removed, final boolean full )
    {
        super.folderChanged ( added, removed, full );

        for ( final FolderListener listener : this.listener )
        {
            listener.folderChanged ( added, removed, full );
        }
    }

    public void disconnected ()
    {
        synchronized ( this )
        {
            this.subscribed = false;
            this.cache.clear ();
        }

        for ( final FolderListener listener : this.listener )
        {
            listener.folderChanged ( new LinkedList<Entry> (), new LinkedList<String> (), true );
        }
    }
}
