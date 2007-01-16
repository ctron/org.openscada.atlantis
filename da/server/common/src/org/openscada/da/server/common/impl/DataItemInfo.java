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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.openscada.core.Variant;
import org.openscada.core.utils.AttributesHelper;
import org.openscada.da.server.common.DataItem;
import org.openscada.da.server.common.SuspendableItem;

public class DataItemInfo {
	private DataItem _item = null;
	private Set<SessionCommon> _sessions = new HashSet<SessionCommon>();
    
    private Variant _cachedValue = new Variant();
    private Map<String,Variant> _cachedAttributes = new HashMap<String,Variant>();

	public DataItemInfo ( DataItem item )
	{
		_item = item;
	}
	
	public Set<SessionCommon> getSessions()
    {
		return _sessions;
	}
	
    /**
     * Add a session to the items listeners
     * <p>
     * Also if the item implements SuspendableItem the {@link SuspendableItem#wakeup()}
     * method is called.
     * @param session the session to add
     */
	public void addSession ( SessionCommon session )
	{
		synchronized ( _sessions )
		{
            if ( _sessions.size () == 0 )
            {
                if ( _item instanceof SuspendableItem )
                    ((SuspendableItem)_item).wakeup ();
            }
            
			_sessions.add ( session );
        }
	}
    
	/**
	 * Remove a session from the items listeners
	 * <p>
	 * Also if the item implements SuspendableItem the {@link SuspendableItem#suspend()}
	 * method is called.
	 * @param session the session to remove
	 */
	public void removeSession ( SessionCommon session )
	{
		synchronized ( _sessions )
		{
			_sessions.remove ( session );
            
            if ( _sessions.size () == 0 )
            {
                if ( _item instanceof SuspendableItem )
                    ((SuspendableItem)_item).suspend ();
            }
		}		
	}
	
	public boolean containsSession ( SessionCommon session )
	{
		synchronized ( _sessions )
		{
			return _sessions.contains(session);
		}		
	}
	
	public void dispose ()
	{
		for ( SessionCommon session : _sessions )
		{
			session.getData().removeItem(_item);
		}
	}

    public Variant getCachedValue ()
    {
        synchronized ( _cachedValue )
        {
            return _cachedValue;
        }
    }

    public void setCachedValue ( Variant cachedValue )
    {
        synchronized ( _cachedValue )
        {
            _cachedValue = new Variant(cachedValue);
        }
    }

    public Map<String, Variant> getCachedAttributes ()
    {
        synchronized ( _cachedAttributes )
        {
            return _cachedAttributes;
        }
    }

    /**
     * merge in the attribute change set into the cached attributes
     * @param attributes the changed attributes
     */
    public void mergeAttributes ( Map<String, Variant> attributes )
    {
        synchronized ( _cachedAttributes )
        {
           AttributesHelper.mergeAttributes ( _cachedAttributes, attributes );
        }
    }
	
}
