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

package org.openscada.da.core.common.impl;

import org.openscada.da.core.ItemChangeListener;
import org.openscada.da.core.ItemListListener;
import org.openscada.da.core.browser.FolderListener;


public class SessionCommon implements org.openscada.da.core.Session
{
	private HiveCommon _hive;
	private ItemChangeListener _listener;
    
	private SessionCommonData _data = new SessionCommonData ();
    private SessionCommonOperations _operations = new SessionCommonOperations ();
	
    private boolean _itemListSubscriber = false;
    private ItemListListener _itemListListener;
    
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

    public void setListener ( ItemListListener listener )
    {
        _itemListListener = listener;
    }

    public ItemListListener getItemListListener ()
    {
        return _itemListListener;
    }

    public boolean isItemListSubscriber ()
    {
        return _itemListSubscriber;
    }

    public void setItemListSubscriber ( boolean itemListSubscriber )
    {
        _itemListSubscriber = itemListSubscriber;
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
    
    
}
