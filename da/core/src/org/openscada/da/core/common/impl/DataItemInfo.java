package org.openscada.da.core.common.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.openscada.da.core.common.DataItem;
import org.openscada.da.core.data.Variant;

public class DataItemInfo {
	private DataItem _item = null;
	private Set<SessionCommon> _sessions = new HashSet<SessionCommon>();
    
    private Variant _cachedValue = new Variant();
    private Map<String,Variant> _cachedAttributes = new HashMap<String,Variant>();

	public DataItemInfo ( DataItem item )
	{
		_item = item;
	}
	
	public Set<SessionCommon> getSessions() {
		return _sessions;
	}
	
	public void addSession ( SessionCommon session )
	{
		synchronized ( _sessions )
		{
			_sessions.add(session);
        }
	}

	public void removeSession ( SessionCommon session )
	{
		synchronized ( _sessions )
		{
			_sessions.remove(session);
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
        return _cachedValue;
    }

    public void setCachedValue ( Variant cachedValue )
    {
        _cachedValue = new Variant(cachedValue);
    }

    public Map<String, Variant> getCachedAttributes ()
    {
        return _cachedAttributes;
    }
	
}
