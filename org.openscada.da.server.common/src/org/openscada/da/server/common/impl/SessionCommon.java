/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2010 TH4 SYSTEMS GmbH (http://inavare.com)
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

package org.openscada.da.server.common.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Future;

import org.openscada.core.InvalidSessionException;
import org.openscada.core.Variant;
import org.openscada.core.server.common.session.AbstractSessionImpl;
import org.openscada.core.subscription.SubscriptionState;
import org.openscada.da.core.server.ItemChangeListener;
import org.openscada.da.core.server.Session;
import org.openscada.da.core.server.browser.FolderListener;
import org.openscada.da.server.common.DataItem;
import org.openscada.sec.UserInformation;
import org.openscada.utils.concurrent.NotifyFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SessionCommon extends AbstractSessionImpl implements Session, DataItemSubscriptionListener
{

    private final static Logger logger = LoggerFactory.getLogger ( SessionCommon.class );

    private final HiveCommon hive;

    private volatile ItemChangeListener listener;

    private final SessionCommonData data = new SessionCommonData ();

    private volatile FolderListener folderListener = null;

    private boolean disposed = false;

    private final Collection<Future<?>> tasks = new ConcurrentLinkedQueue<Future<?>> ();

    public SessionCommon ( final HiveCommon hive, final UserInformation userInformation, final Map<String, String> properties )
    {
        super ( userInformation, properties );

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
        logger.debug ( "Data changed - itemId: {}, value: {}, attributes: {}, cache: {}", new Object[] { item.getInformation ().getName (), value, attributes, cache } );

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
