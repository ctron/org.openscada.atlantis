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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.log4j.Logger;
import org.openscada.core.InvalidSessionException;
import org.openscada.core.Variant;
import org.openscada.da.core.browser.common.Folder;
import org.openscada.da.core.common.DataItem;
import org.openscada.da.core.common.DataItemFactory;
import org.openscada.da.core.common.DataItemFactoryListener;
import org.openscada.da.core.common.DataItemInformationBase;
import org.openscada.da.core.common.ItemListener;
import org.openscada.da.core.server.CancellationNotSupportedException;
import org.openscada.da.core.server.DataItemInformation;
import org.openscada.da.core.server.Hive;
import org.openscada.da.core.server.InvalidItemException;
import org.openscada.da.core.server.ItemChangeListener;
import org.openscada.da.core.server.Session;
import org.openscada.da.core.server.WriteAttributesOperationListener;
import org.openscada.da.core.server.WriteOperationListener;
import org.openscada.da.core.server.browser.HiveBrowser;
import org.openscada.utils.jobqueue.CancelNotSupportedException;
import org.openscada.utils.jobqueue.OperationManager;
import org.openscada.utils.jobqueue.OperationProcessor;
import org.openscada.utils.jobqueue.RunnableCancelOperation;
import org.openscada.utils.jobqueue.OperationManager.Handle;

public class HiveCommon implements Hive, ItemListener
{
	
    private static Logger _log = Logger.getLogger ( HiveCommon.class );
    
	private Set<SessionCommon> _sessions = new HashSet<SessionCommon>();
	
    private Map<DataItem,DataItemInfo> _items = new HashMap<DataItem,DataItemInfo>();
	private Map<DataItemInformation,DataItem> _itemMap = new HashMap<DataItemInformation,DataItem>();
    
    private HiveBrowserCommon _browser = null;
    private Folder _rootFolder = null;
    
    private Set<SessionListener> _sessionListeners = new HashSet<SessionListener> ();
    
    private OperationManager _opManager = new OperationManager ();
    private OperationProcessor _opProcessor = new OperationProcessor ();
    private Thread _jobQueueThread = null;
    
    private List<DataItemFactory> _factoryList = new LinkedList<DataItemFactory> ();
    private Set<DataItemFactoryListener> _factoryListeners = new HashSet<DataItemFactoryListener> ();
	
    public HiveCommon ()
    {
        super ();
        
        _jobQueueThread = new Thread ( _opProcessor );
        _jobQueueThread.start ();
    }
    
    @Override
    protected void finalize () throws Throwable
    {
        _jobQueueThread.interrupt ();
        super.finalize ();
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
    
	public SessionCommon validateSession ( Session session ) throws InvalidSessionException
	{
		if ( !(session instanceof SessionCommon) )
			throw new InvalidSessionException();
		
		SessionCommon sessionCommon = (SessionCommon)session;
		if ( sessionCommon.getHive () != this )
			throw new InvalidSessionException();
		
		if ( !_sessions.contains( sessionCommon ) )
			throw new InvalidSessionException();
        
        return sessionCommon;
	}
	
	// implementation of hive interface
	
	public Session createSession ( Properties props )
	{
		SessionCommon session = new SessionCommon ( this );
		synchronized ( _sessions )
		{
			_sessions.add ( session );
            _opManager.addListener ( session.getOperations () );
            fireSessionCreate ( session );
		}
		return session;
	}
	
	private void closeSessions ( Set<SessionCommon> sessions )
	{
		try
        {
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
            
            // cancel pending operations
            Set<Handle> operations = sessionCommon.getOperations ().getOperations ();
            for ( Handle handle : operations )
            {
                try
                {
                    _log.info ( "Stopping operation: " + handle );
                    handle.cancel ();
                }
                catch ( CancelNotSupportedException e )
                {
                    _log.warn ( "Failed to cancel job on session destruction", e );
                    // ignore it .. we can't do anything
                }
            }
            sessionCommon.getOperations ().clear ();
			
			_sessions.remove ( session );
		}
	}
	
	public void registerForItem ( Session session, String itemName, boolean initial ) throws InvalidSessionException, InvalidItemException
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
	
	public Collection<DataItemInformation> listItems ( Session session ) throws InvalidSessionException
    {
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
                // first add internally ...
				_items.put ( item, new DataItemInfo(item) );
				_itemMap.put ( new DataItemInformationBase(item.getInformation()), item );

                fireAddItem ( item.getInformation () );
                
                // then hook up the listener since the item may
                // flush its current state 
                item.setListener ( this );
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
	
	private DataItemInfo getItemInfo ( DataItem item )
	{
		synchronized ( _items )
		{
			return _items.get ( item );
		}
	}
	
    private DataItem factoryCreate ( String id )
    {
        synchronized ( _factoryList )
        {
            for ( DataItemFactory factory : _factoryList )
            {
                if ( factory.canCreate ( id ) )
                {
                    DataItem dataItem = factory.create ( id );
                    registerItem ( dataItem );
                    fireDataItemCreated ( dataItem );
                    return dataItem;
                }
            }
        }
        return null;
    }
    
	private DataItem lookupItem ( String id )
	{
        synchronized ( _items )
        {
            DataItem dataItem = _itemMap.get ( new DataItemInformationBase ( id ) );
            if ( dataItem == null )
            {
                dataItem = factoryCreate ( id );
            }
            return dataItem;
        }
	}
	
	// ItemListener Interface
	public void valueChanged(DataItem item, Variant variant)
	{
		DataItemInfo info = getItemInfo ( item );
		if ( info == null )
			return; // ignore
        
        // store the new value in the cache
        info.setCachedValue ( variant );
		
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
			closeSessions ( sessionsToClose );
		
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

    public long startWriteAttributes ( Session session, String itemId, Map<String, Variant> attributes, WriteAttributesOperationListener listener ) throws InvalidSessionException, InvalidItemException
    {
        SessionCommon sessionCommon = validateSession ( session );
        
        final DataItem item = lookupItem ( itemId );
        
        if ( item == null )
            throw new InvalidItemException ( itemId );
        
        if ( listener == null )
            throw new NullPointerException ();
        
        WriteAttributesOperation op = new WriteAttributesOperation ( item, listener, attributes );
        Handle handle = _opManager.schedule ( op );
        
        synchronized ( sessionCommon )
        {
            sessionCommon.getOperations ().addOperation ( handle );
        }
        
        return handle.getId ();
    }

    public long startWrite ( Session session, String itemName, final Variant value, final WriteOperationListener listener ) throws InvalidSessionException, InvalidItemException
    {
        SessionCommon sessionCommon = validateSession ( session );
        
        final DataItem item = lookupItem ( itemName );
        
        if ( item == null )
            throw new InvalidItemException ( itemName );
        
        if ( listener == null )
            throw new NullPointerException ();
        
        Handle handle = _opManager.schedule ( new RunnableCancelOperation () {

            public void run ()
            {
                try
                {
                    item.setValue ( value );
                    if ( !isCanceled () )
                        listener.success ();
                }
                catch ( Exception e )
                {
                    if ( !isCanceled () )
                        listener.failure ( e );
                }
            }} );
        
        sessionCommon.getOperations ().addOperation ( handle );
        
        return handle.getId ();
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

    public void cancelOperation ( Session session, long id ) throws CancellationNotSupportedException, InvalidSessionException
    {
        SessionCommon sessionCommon = validateSession ( session );
        
        synchronized ( sessionCommon )
        {
            _log.info ( String.format ( "Cancelling operation: %d", id ) );

            Handle handle = _opManager.get ( id );
            if ( handle != null )
            {
                if ( sessionCommon.getOperations ().containsOperation ( handle ) )
                {
                    try
                    {
                        handle.cancel ();
                    }
                    catch ( CancelNotSupportedException e )
                    {
                        throw new CancellationNotSupportedException ();
                    }
                }
            }
        }
    }

    public void thawOperation ( Session session, long id ) throws InvalidSessionException
    {
        SessionCommon sessionCommon = validateSession ( session );

        synchronized ( sessionCommon )
        {
            _log.info ( String.format ( "Thawing operation %d", id ) );

            Handle handle = _opManager.get ( id );
            if ( handle != null )
            {
                if ( sessionCommon.getOperations ().containsOperation ( handle ) )
                {
                    _opProcessor.add ( handle );
                }
            }
            else
                _log.warn ( String.format ( "%d is not a valid operation id", id ) );
        }
    }
    
    public void addItemFactory ( DataItemFactory factory )
    {
        synchronized ( _factoryList )
        {
            _factoryList.add ( factory );
        }
    }
    
    public void removeItemFactory ( DataItemFactory factory )
    {
        synchronized ( _factoryList )
        {
            _factoryList.remove ( factory );
        }
    }
    
    public void addItemFactoryListener ( DataItemFactoryListener listener )
    {
        synchronized ( _factoryListeners )
        {
            _factoryListeners.add ( listener );
        }
    }
    
    public void removeItemFactoryListener ( DataItemFactoryListener listener )
    {
        synchronized ( _factoryListeners )
        {
            _factoryListeners.remove ( listener );
        }
    }
    
    private void fireDataItemCreated ( DataItem dataItem )
    {
        synchronized ( _factoryListeners )
        {
            for ( DataItemFactoryListener listener : _factoryListeners )
            {
                listener.created ( dataItem );
            }
        }
    }
}
