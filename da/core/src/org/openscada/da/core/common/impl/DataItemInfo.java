package org.openscada.da.core.common.impl;

import java.util.HashSet;
import java.util.Set;

import org.openscada.da.core.common.DataItem;

public class DataItemInfo {
	private DataItem _item = null;
	private Set<SessionCommon> _sessions = new HashSet<SessionCommon>();

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
	
}
