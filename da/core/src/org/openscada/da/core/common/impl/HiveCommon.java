package org.openscada.da.core.common.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.openscada.da.core.Hive;
import org.openscada.da.core.InvalidItemException;
import org.openscada.da.core.InvalidSessionException;
import org.openscada.da.core.ItemChangeListener;
import org.openscada.da.core.Session;
import org.openscada.da.core.common.DataItem;
import org.openscada.da.core.common.ItemListener;
import org.openscada.da.core.data.Variant;

public class HiveCommon implements Hive, ItemListener {
	
	private Map<DataItem,DataItemInfo> _items = new HashMap<DataItem,DataItemInfo>();
	private Set<SessionCommon> _sessions = new HashSet<SessionCommon>();
	
	private Map<String,DataItem> _itemMap = new HashMap<String,DataItem>();
	
	public void validateSession ( Session session ) throws InvalidSessionException
	{
		if ( !(session instanceof SessionCommon) )
			throw new InvalidSessionException();
		
		SessionCommon sessionCommon = (SessionCommon)session;
		if ( sessionCommon.getHive () != this )
			throw new InvalidSessionException();
		
		if ( !_sessions.contains( sessionCommon ) )
			throw new InvalidSessionException();
	}
	
	// implementation of hive interface
	
	public Session createSession(Properties props)
	{
		SessionCommon session = new SessionCommon(this);
		synchronized ( _sessions )
		{
			_sessions.add(session);
		}
		return session;
	}
	
	private void closeSessions ( Set<SessionCommon> sessions )
	{
		try {
			for ( SessionCommon session : sessions )
			{
				closeSession ( session );
			}
		}
		catch ( InvalidSessionException e )
		{
			// this should never happen, only if session is already closed
		}
	}
	
	public void closeSession(Session session) throws InvalidSessionException
	{	
		validateSession ( session );
		
		synchronized ( _sessions )
		{
			SessionCommonData sessionData = ((SessionCommon)session).getData();
			SessionCommon sessionCommon = ((SessionCommon)session);
			
			Set<DataItem> sessionItems = new HashSet<DataItem>(sessionData.getItems());
			for ( DataItem item : sessionItems )
			{
				synchronized ( _items )
				{
					if ( _items.containsKey(item) )
					{
						DataItemInfo info = _items.get(item);
						info.removeSession(sessionCommon);
					}
				}
			}
			
			_sessions.remove(session);
		}
	}
	
	public void registerForItem(Session session, String itemName) throws InvalidSessionException, InvalidItemException
	{
		validateSession ( session );
		
		DataItem item = lookupItem ( itemName );
		
		if ( item == null )
			throw new InvalidItemException(itemName);
		
		SessionCommon sessionCommon = (SessionCommon)session;
		sessionCommon.getData().addItem(item);
		_items.get(item).addSession(sessionCommon);
	}
	
	public void unregisterForItem(Session session, String itemName) throws InvalidSessionException, InvalidItemException
	{
		validateSession ( session );
		
		DataItem item = lookupItem ( itemName );
		
		if ( item == null )
			throw new InvalidItemException(itemName);
		
		SessionCommon sessionCommon = (SessionCommon)session;
		sessionCommon.getData().removeItem(item);
		_items.get(item).removeSession(sessionCommon);
	}
	
	public Collection<String> listItems(Session session) throws InvalidSessionException {
		validateSession ( session );
		
		return _itemMap.keySet();
	}
	
	// data item
	public void registerItem ( DataItem item )
	{
		synchronized ( _items )
		{
			if ( !_items.containsKey(item) )
			{
				item.setListener(this);
				_items.put ( item, new DataItemInfo(item) );
				_itemMap.put( item.getName(), item );
			}
		}
	}
	
	public void unregisterItem ( DataItem item )
	{
		synchronized ( _items )
		{
			if ( _items.containsKey(item) )
			{
				item.setListener(null);
				
				DataItemInfo info = _items.get(item);
				info.dispose();
				
				_items.remove(item);	
				_itemMap.remove(item.getName());
			}
		}
	}
	
	private boolean containsItem ( DataItem item )
	{
		synchronized ( _items )
		{
			return _items.containsKey(item);
		}
	}
	
	private DataItemInfo getItemInfo ( DataItem item )
	{
		synchronized ( _items )
		{
			return _items.get ( item );
		}
	}
	
	private DataItem lookupItem ( String name )
	{
		return _itemMap.get(name);
	}
	
	// ItemListener Interface
	public void valueChanged(DataItem item, Variant variant)
	{
		DataItemInfo info = getItemInfo ( item );
		if ( info == null )
			return; // ignore
		
		Set<SessionCommon> sessionsToClose = new HashSet<SessionCommon>();
		
		Set<SessionCommon> sessions = new HashSet<SessionCommon>(info.getSessions());
		
		for ( SessionCommon session : sessions )
		{
			ItemChangeListener listener = session.getListener();
			
			if ( listener == null )
				continue; // if no listener is set simply ignore it
			
			try
			{
				listener.valueChanged ( item.getName(), variant );
			}
			catch ( Exception e )
			{
				// mark session for closing later
				sessionsToClose.add(session);
			}
		}
		
		// if we have broken sessions close them now
		if ( sessionsToClose.size() > 0 )
			closeSessions(sessionsToClose);
		
	}
	
	public void attributesChanged(DataItem item, Map<String, Variant> attributes)
	{
		DataItemInfo info = getItemInfo ( item );
		if ( info == null )
			return; // ignore
		
		Set<SessionCommon> sessionsToClose = new HashSet<SessionCommon>();
		
		Set<SessionCommon> sessions = new HashSet<SessionCommon>(info.getSessions());
		
		for ( SessionCommon session : sessions )
		{
			ItemChangeListener listener = session.getListener();
			
			if ( listener == null )
				continue; // if no listener is set simply ignore it
			
			try
			{
				listener.attributesChanged(item.getName(), attributes);
			}
			catch ( Exception e )
			{
				// mark session for closing later
				sessionsToClose.add(session);
			}
		}
		
		// if we have broken sessions close them now
		if ( sessionsToClose.size() > 0 )
			closeSessions(sessionsToClose);
		
	}

	public void registerForAll(Session session) throws InvalidSessionException {
		// TODO Auto-generated method stub
		
	}

	public void unregisterForAll(Session session) throws InvalidSessionException {
		// TODO Auto-generated method stub
		
	}
	
}
