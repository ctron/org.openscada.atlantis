package org.openscada.da.core.common.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.openscada.da.core.DataItemInformation;
import org.openscada.da.core.Hive;
import org.openscada.da.core.InvalidItemException;
import org.openscada.da.core.InvalidSessionException;
import org.openscada.da.core.ItemChangeListener;
import org.openscada.da.core.Session;
import org.openscada.da.core.WriteOperationListener;
import org.openscada.da.core.browser.Entry;
import org.openscada.da.core.browser.HiveBrowser;
import org.openscada.da.core.browser.common.Folder;
import org.openscada.da.core.common.DataItem;
import org.openscada.da.core.common.DataItemInformationBase;
import org.openscada.da.core.common.ItemListener;
import org.openscada.da.core.data.Variant;
import org.openscada.utils.exec.OperationResultHandler;

public class HiveCommon implements Hive, ItemListener
{
	
	private Set<SessionCommon> _sessions = new HashSet<SessionCommon>();
	
    private Map<DataItem,DataItemInfo> _items = new HashMap<DataItem,DataItemInfo>();
	private Map<DataItemInformation,DataItem> _itemMap = new HashMap<DataItemInformation,DataItem>();
    
    private Executor _executor = Executors.newCachedThreadPool();
    
    private HiveBrowserCommon _browser = null;
    private Folder _rootFolder = null;
    
    private Set<SessionListener> _sessionListeners = new HashSet<SessionListener> ();
	
    public HiveCommon ()
    {
        super ();
    }
    
    public void addSessionListener ( SessionListener listener )
    {
        synchronized ( _sessionListeners )
        {
            _sessionListeners.add ( listener );
        }
    }
    
    public void removeSessionListener ( SessionListener listener )
    {
        synchronized ( _sessionListeners )
        {
            _sessionListeners.remove ( listener );
        }
    }
    
    private void fireSessionCreate ( SessionCommon session )
    {
        synchronized ( _sessionListeners )
        {
            for ( SessionListener listener : _sessionListeners )
            {
                try
                {
                    listener.create ( session );
                }
                catch ( Exception e )
                {}
            }
        }
    }
    
    private void fireSessionDestroy ( SessionCommon session )
    {
        synchronized ( _sessionListeners )
        {
            for ( SessionListener listener : _sessionListeners )
            {
                try
                {
                    listener.destroy ( session );
                }
                catch ( Exception e )
                {}
            }
        }
    }
    
    protected synchronized void setRootFolder ( Folder rootFolder )
    {
        if ( _rootFolder == null )
        {
            _rootFolder = rootFolder;
        }
    }
    
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
	
	public Session createSession ( Properties props )
	{
		SessionCommon session = new SessionCommon ( this );
		synchronized ( _sessions )
		{
			_sessions.add ( session );
            fireSessionCreate ( session );
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
	
	public void closeSession ( Session session ) throws InvalidSessionException
	{	
		validateSession ( session );
		
		synchronized ( _sessions )
		{
            fireSessionDestroy ( (SessionCommon)session );
            
			SessionCommonData sessionData = ((SessionCommon)session).getData ();
			SessionCommon sessionCommon = ((SessionCommon)session);
			
			Set<DataItem> sessionItems = new HashSet<DataItem>(sessionData.getItems());
			for ( DataItem item : sessionItems )
			{
				synchronized ( _items )
				{
					if ( _items.containsKey(item) )
					{
						DataItemInfo info = _items.get ( item );
						info.removeSession ( sessionCommon );
					}
				}
			}
			
			_sessions.remove ( session );
		}
	}
	
	public void registerForItem(Session session, String itemName, boolean initial) throws InvalidSessionException, InvalidItemException
	{
		validateSession ( session );
		
		// lookup the item first
		DataItem item = lookupItem ( itemName );
		
		if ( item == null )
			throw new InvalidItemException(itemName);
		
		SessionCommon sessionCommon = (SessionCommon)session;
		sessionCommon.getData().addItem ( item );
        DataItemInfo info = _items.get ( item );
        
		info.addSession ( sessionCommon );
        
        // process initial transmission
        if ( initial && (sessionCommon.getListener() != null) )
        {
            try
            {
                ItemChangeListener listener = sessionCommon.getListener();
                listener.valueChanged ( itemName, info.getCachedValue(), true );
                listener.attributesChanged ( itemName, info.getCachedAttributes(), true );
            }
            catch ( Exception e )
            {
                closeSession ( session );
            }
        }
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
	
	public Collection<DataItemInformation> listItems ( Session session ) throws InvalidSessionException {
		validateSession ( session );
		
        synchronized ( _items )
        {
            return _itemMap.keySet();
        }
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
				_itemMap.put ( new DataItemInformationBase(item.getInformation()), item );
                
                fireAddItem ( item.getInformation () );
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
				_itemMap.remove ( new DataItemInformationBase ( item.getInformation ().getName () ) );
                
                fireRemoveItem ( item.getInformation().getName() );
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
        synchronized ( _items )
        {
            return _itemMap.get ( new DataItemInformationBase ( name ) );
        }
	}
	
	// ItemListener Interface
	public void valueChanged(DataItem item, Variant variant)
	{
		DataItemInfo info = getItemInfo ( item );
		if ( info == null )
			return; // ignore
        
        // store the new value in the cache
        info.setCachedValue(variant);
		
		Set<SessionCommon> sessionsToClose = new HashSet<SessionCommon>();
		
		Set<SessionCommon> sessions = new HashSet<SessionCommon>(info.getSessions());
		
		for ( SessionCommon session : sessions )
		{
			ItemChangeListener listener = session.getListener();
			
			if ( listener == null )
				continue; // if no listener is set simply ignore it
			
			try
			{
				listener.valueChanged ( item.getInformation().getName(), variant, false );
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
		
		info.mergeAttributes ( attributes );
        
		Set<SessionCommon> sessionsToClose = new HashSet<SessionCommon>();
		
		Set<SessionCommon> sessions = new HashSet<SessionCommon>(info.getSessions());
		
		for ( SessionCommon session : sessions )
		{
			ItemChangeListener listener = session.getListener();
			
			if ( listener == null )
				continue; // if no listener is set simply ignore it
			
			try
			{
				listener.attributesChanged ( item.getInformation().getName(), attributes, false);
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

    public void registerItemList ( Session session ) throws InvalidSessionException
    {
        validateSession ( session );
        
        synchronized ( session )
        {
            SessionCommon sessionCommon = (SessionCommon)session;
            if ( sessionCommon.isItemListSubscriber() )
                return;
            
            // send initial content
            synchronized(_items)
            {
                Collection<DataItemInformation> items = _itemMap.keySet();
                sessionCommon.setItemListSubscriber(true);
                if ( sessionCommon.getItemListListener() != null )
                {
                    sessionCommon.getItemListListener().changed ( items, new ArrayList<String>(), true);
                }
            }
        }
    }

    public void unregisterItemList ( Session session ) throws InvalidSessionException
    {
        validateSession ( session );
        
        synchronized ( session )
        {
            SessionCommon sessionCommon = (SessionCommon)session;
            if ( !sessionCommon.isItemListSubscriber() )
                return;
            
            sessionCommon.setItemListSubscriber(false);
        }
    }
    
    private void fireAddItem ( DataItemInformation item )
    {
        Collection<DataItemInformation> added = new ArrayList<DataItemInformation>();
        added.add (item );
        fireItemListChange(added, new ArrayList<String>());
    }
    
    private void fireRemoveItem ( String item )
    {
        Collection<String> removed = new ArrayList<String>();
        removed.add(item);
        fireItemListChange(new ArrayList<DataItemInformation>(), removed);
    }
    
    private void fireItemListChange ( Collection<DataItemInformation> added, Collection<String> removed )
    {
        synchronized ( _sessions )
        {
            for ( SessionCommon session : _sessions )
            {
                if ( session.isItemListSubscriber() && session.getItemListListener() != null )
                {
                    session.getItemListListener().changed ( added, removed, false );
                }
            }
        }
    }

    public void startWrite ( Session session, String itemName, Variant value, final WriteOperationListener listener ) throws InvalidSessionException, InvalidItemException
    {
        validateSession(session);
        
        DataItem item = lookupItem(itemName);
        
        if ( item == null )
            throw new InvalidItemException(itemName);
        
        if ( listener == null )
            return; // FIXME: report as error
        
        new WriteOperation().startExecute ( new OperationResultHandler<Object>(){

            public void failure ( Exception e )
            {
               listener.failure ( e.getMessage() );
            }

            public void success ( Object result )
            {
                listener.success();
            }}, new WriteOperationArguments(item,value));
    }
	
    public synchronized HiveBrowser getBrowser ()
    {
        if ( _browser == null )
        {
            if ( _rootFolder != null )
                _browser = new HiveBrowserCommon ( this ) {

                    @Override
                    public Folder getRootFolder ()
                    {
                       return _rootFolder;
                    }};
        }            
        
        return _browser;
    } 
}
