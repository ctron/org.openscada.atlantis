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
	private HiveCommon _hive;
	private ItemChangeListener _listener;
    
	private SessionCommonData _data = new SessionCommonData ();
    private SessionCommonOperations _operations = new SessionCommonOperations ();
	
    private FolderListener _folderListener = null;
    
	public SessionCommon ( HiveCommon hive )
	{
		_hive = hive;
	}
	
	public HiveCommon getHive ()
	{
		return _hive;
	}

	public void setListener ( ItemChangeListener listener )
	{
		_listener = listener;
	}

	public ItemChangeListener getListener ()
	{
		return _listener;
	}

	public SessionCommonData getData ()
    {
		return _data;
	}

    public FolderListener getFolderListener ()
    {
        return _folderListener;
    }

    public void setListener ( FolderListener folderListener )
    {
        _folderListener = folderListener;
    }

    public SessionCommonOperations getOperations ()
    {
        return _operations;
    }
    
    // Data item listener stuff

    public void attributesChanged ( DataItem item, Map<String, Variant> attributes )
    {
        ItemChangeListener listener;
        
        if ( ( listener = _listener ) != null )
        {
            listener.attributesChanged ( item.getInformation ().getName (), attributes, false );
        }
    }

    public void updateStatus ( Object topic, SubscriptionState subscriptionState )
    {
        ItemChangeListener listener;
        
        if ( ( listener = _listener ) != null )
        {
            listener.subscriptionChanged ( topic.toString (), subscriptionState );
        }
    }

    public void valueChanged ( DataItem item, Variant value )
    {
        ItemChangeListener listener;
        
        if ( ( listener = _listener ) != null )
        {
            listener.valueChanged ( item.getInformation ().getName (), value, false );
        }
    }
}
