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

package org.openscada.da.server.common.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Future;

import org.openscada.core.InvalidSessionException;
import org.openscada.core.Variant;
import org.openscada.core.subscription.SubscriptionState;
import org.openscada.da.core.server.ItemChangeListener;
import org.openscada.da.core.server.Session;
import org.openscada.da.core.server.browser.FolderListener;
import org.openscada.da.server.common.DataItem;
import org.openscada.utils.concurrent.NotifyFuture;

public class SessionCommon implements Session, DataItemSubscriptionListener
{
    private final HiveCommon hive;

    private volatile ItemChangeListener listener;

    private final SessionCommonData data = new SessionCommonData ();

    private volatile FolderListener folderListener = null;

    private boolean disposed = false;

    private final Collection<Future<?>> tasks = new ConcurrentLinkedQueue<Future<?>> ();

    public SessionCommon ( final HiveCommon hive )
    {
        this.hive = hive;
    }

    public HiveCommon getHive ()
    {
        return this.hive;
    }

    public void setListener ( final ItemChangeListener listener )
    {
        this.listener = listener;
    }

    public ItemChangeListener getListener ()
    {
        return this.listener;
    }

    public SessionCommonData getData ()
    {
        return this.data;
    }

    public FolderListener getFolderListener ()
    {
        return this.folderListener;
    }

    public void setListener ( final FolderListener folderListener )
    {
        this.folderListener = folderListener;
    }

    // Data item listener stuff
    public void updateStatus ( final Object topic, final SubscriptionState subscriptionState )
    {
        ItemChangeListener listener;

        if ( ( listener = this.listener ) != null )
        {
            listener.subscriptionChanged ( topic.toString (), subscriptionState );
        }
    }

    public void dataChanged ( final DataItem item, final Variant value, final Map<String, Variant> attributes, final boolean cache )
    {
        ItemChangeListener listener;

        if ( ( listener = this.listener ) != null )
        {
            listener.dataChanged ( item.getInformation ().getName (), value, attributes, cache );
        }
    }

    /**
     * Add a future to the session.
     * <p>The future will be canceled when the session is completed.
     * The session subscribes to the future state in order to remove
     * the future from the session one it is completed.
     * </p> 
     * @param future the future to add
     * @throws InvalidSessionException in case the session was already disposed
     */
    public void addFuture ( final NotifyFuture<?> future ) throws InvalidSessionException
    {
        synchronized ( this.tasks )
        {
            if ( !this.disposed )
            {
                this.tasks.add ( future );
            }
            else
            {
                throw new InvalidSessionException ();
            }
        }

        // now add the listener
        future.addListener ( new Runnable () {

            public void run ()
            {
                removeFuture ( future );
            }
        } );
    }

    public void removeFuture ( final NotifyFuture<?> future )
    {
        synchronized ( this.tasks )
        {
            if ( !this.disposed )
            {
                this.tasks.remove ( future );
            }
        }
    }

    /**
     * Dispose the session
     */
    public void dispose ()
    {
        final Collection<Future<?>> tasks;
        synchronized ( this.tasks )
        {
            this.disposed = true;

            tasks = new ArrayList<Future<?>> ( this.tasks );
            this.tasks.clear ();
        }

        for ( final Future<?> task : tasks )
        {
            task.cancel ( true );
        }
    }
}
