/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006 inavare GmbH (http://inavare.com)
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

import java.util.Map;

import org.openscada.core.Variant;
import org.openscada.core.server.common.SessionCommonOperations;
import org.openscada.core.subscription.SubscriptionState;
import org.openscada.da.core.server.ItemChangeListener;
import org.openscada.da.core.server.Session;
import org.openscada.da.core.server.browser.FolderListener;
import org.openscada.da.server.common.DataItem;

public class SessionCommon implements Session, DataItemSubscriptionListener
{
    private final HiveCommon _hive;

    private ItemChangeListener _listener;

    private final SessionCommonData _data = new SessionCommonData ();

    private final SessionCommonOperations _operations = new SessionCommonOperations ();

    private FolderListener _folderListener = null;

    public SessionCommon ( final HiveCommon hive )
    {
        this._hive = hive;
    }

    public HiveCommon getHive ()
    {
        return this._hive;
    }

    public void setListener ( final ItemChangeListener listener )
    {
        this._listener = listener;
    }

    public ItemChangeListener getListener ()
    {
        return this._listener;
    }

    public SessionCommonData getData ()
    {
        return this._data;
    }

    public FolderListener getFolderListener ()
    {
        return this._folderListener;
    }

    public void setListener ( final FolderListener folderListener )
    {
        this._folderListener = folderListener;
    }

    public SessionCommonOperations getOperations ()
    {
        return this._operations;
    }

    // Data item listener stuff
    public void updateStatus ( final Object topic, final SubscriptionState subscriptionState )
    {
        ItemChangeListener listener;

        if ( ( listener = this._listener ) != null )
        {
            listener.subscriptionChanged ( topic.toString (), subscriptionState );
        }
    }

    public void dataChanged ( final DataItem item, final Variant value, final Map<String, Variant> attributes, final boolean cache )
    {
        ItemChangeListener listener;

        if ( ( listener = this._listener ) != null )
        {
            listener.dataChanged ( item.getInformation ().getName (), value, attributes, cache );
        }
    }

    public Object getSubscriptionHint ()
    {
        return null;
    }
}
