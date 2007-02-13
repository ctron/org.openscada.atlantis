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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.log4j.Logger;
import org.openscada.core.CancellationNotSupportedException;
import org.openscada.core.InvalidSessionException;
import org.openscada.core.Variant;
import org.openscada.core.subscription.SubscriptionManager;
import org.openscada.core.subscription.ValidationException;
import org.openscada.da.core.server.DataItemInformation;
import org.openscada.da.core.server.Hive;
import org.openscada.da.core.server.InvalidItemException;
import org.openscada.da.core.server.Session;
import org.openscada.da.core.server.WriteAttributesOperationListener;
import org.openscada.da.core.server.WriteOperationListener;
import org.openscada.da.core.server.browser.HiveBrowser;
import org.openscada.da.server.browser.common.Folder;
import org.openscada.da.server.common.DataItem;
import org.openscada.da.server.common.DataItemInformationBase;
import org.openscada.da.server.common.configuration.ConfigurableHive;
import org.openscada.da.server.common.configuration.ConfigurationError;
import org.openscada.da.server.common.factory.DataItemFactory;
import org.openscada.da.server.common.factory.DataItemFactoryListener;
import org.openscada.da.server.common.factory.DataItemFactoryRequest;
import org.openscada.da.server.common.factory.FactoryHelper;
import org.openscada.da.server.common.factory.FactoryTemplate;
import org.openscada.utils.jobqueue.CancelNotSupportedException;
import org.openscada.utils.jobqueue.Operation;
import org.openscada.utils.jobqueue.OperationManager;
import org.openscada.utils.jobqueue.OperationProcessor;
import org.openscada.utils.jobqueue.RunnableCancelOperation;
import org.openscada.utils.jobqueue.OperationManager.Handle;

public class HiveCommon implements Hive, ConfigurableHive
{

    private static Logger _log = Logger.getLogger ( HiveCommon.class );

    private Set<SessionCommon> _sessions = new HashSet<SessionCommon> ();

    private Map<DataItemInformation, DataItem> _itemMap = new HashMap<DataItemInformation, DataItem> ();

    private HiveBrowserCommon _browser = null;

    private Folder _rootFolder = null;

    private Set<SessionListener> _sessionListeners = new HashSet<SessionListener> ();

    private OperationManager _opManager = new OperationManager ();

    private OperationProcessor _opProcessor = new OperationProcessor ();

    private Thread _jobQueueThread = null;

    private List<DataItemFactory> _factoryList = new LinkedList<DataItemFactory> ();

    private Set<DataItemFactoryListener> _factoryListeners = new HashSet<DataItemFactoryListener> ();

    private List<FactoryTemplate> _templates = new LinkedList<FactoryTemplate> ();
    
    private SubscriptionManager _itemSubscriptionManager = new SubscriptionManager ();

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
                {
                }
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
                {
                }
            }
        }
    }

    public Folder getRootFolder ()
    {
        return _rootFolder;
    }

    public synchronized void setRootFolder ( Folder rootFolder )
    {
        if ( _rootFolder == null )
        {
            _rootFolder = rootFolder;
        }
    }

    public SessionCommon validateSession ( Session session ) throws InvalidSessionException
    {
        if ( ! ( session instanceof SessionCommon ) )
            throw new InvalidSessionException ();

        SessionCommon sessionCommon = (SessionCommon)session;
        if ( sessionCommon.getHive () != this )
            throw new InvalidSessionException ();

        if ( !_sessions.contains ( sessionCommon ) )
            throw new InvalidSessionException ();

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

    public void closeSession ( Session session ) throws InvalidSessionException
    {
        SessionCommon sessionCommon = validateSession ( session );

        synchronized ( _sessions )
        {
            fireSessionDestroy ( (SessionCommon)session );
            
            // destroy all subscriptions for this session
            _itemSubscriptionManager.unsubscribeAll ( sessionCommon );

            // cancel all pending operations
            sessionCommon.getOperations ().cancelAll ();

            _sessions.remove ( session );
        }
    }

    public void subscribeItem ( Session session, String itemId ) throws InvalidSessionException, InvalidItemException
    {
        // validate the session first
        SessionCommon sessionCommon = validateSession ( session );
        
        // subscribe using the new item subscription manager
        try
        {
            _itemSubscriptionManager.subscribe ( itemId, sessionCommon );
        }
        catch ( ValidationException e )
        {
            throw new InvalidItemException ( itemId );
        }
    }

    public void unsubscribeItem ( Session session, String itemId ) throws InvalidSessionException, InvalidItemException
    {
        SessionCommon sessionCommon = validateSession ( session );
        
        // unsubscribe using the new item subscription manager
        _itemSubscriptionManager.unsubscribe ( itemId, sessionCommon );
    }

    // data item
    /* (non-Javadoc)
     * @see org.openscada.da.server.common.impl.ConfigurableHive#registerItem(org.openscada.da.server.common.DataItem)
     */
    public void registerItem ( DataItem item )
    {
        synchronized ( _itemMap )
        {
            String id = item.getInformation ().getName ();
            
            if ( !_itemMap.containsKey ( new DataItemInformationBase ( item.getInformation () ) ) )
            {
                // first add internally ...
                _itemMap.put ( new DataItemInformationBase ( item.getInformation () ), item );
            }
            
            // add new topic to the new item subscription manager
            _itemSubscriptionManager.setSource ( id, new DataItemSubscriptionSource ( item ) );
        }
    }

    public void unregisterItem ( DataItem item )
    {
        synchronized ( _itemMap )
        {
            String id = item.getInformation ().getName ();
            if ( _itemMap.containsKey ( new DataItemInformationBase ( item.getInformation () ) ) )
            {
                _itemMap.remove ( new DataItemInformationBase ( item.getInformation () ) );
            }
            
            // remove the source from the manager
            _itemSubscriptionManager.setSource ( id, null );
        }
    }

    private DataItem factoryCreate ( DataItemFactoryRequest request )
    {
        synchronized ( _factoryList )
        {
            for ( DataItemFactory factory : _factoryList )
            {
                if ( factory.canCreate ( request ) )
                {
                    DataItem dataItem = factory.create ( request );
                    registerItem ( dataItem );
                    fireDataItemCreated ( dataItem );
                    return dataItem;
                }
            }
        }
        return null;
    }

    public boolean validateItem ( String id )
    {
        if ( lookupItem ( id ) != null )
        {
            return true;
        }

        DataItemFactoryRequest request = new DataItemFactoryRequest ();
        request.setId ( id );

        synchronized ( _factoryList )
        {
            for ( DataItemFactory factory : _factoryList )
            {
                if ( factory.canCreate ( request ) )
                {
                    return true;
                }
            }
        }
        return false;
    }

    public DataItem lookupItem ( String id )
    {
        synchronized ( _itemMap )
        {
            return _itemMap.get ( new DataItemInformationBase ( id ) );
        }
    }

    public DataItem retrieveItem ( String id )
    {
        DataItem dataItem = lookupItem ( id );
        if ( dataItem != null )
        {
            return dataItem;
        }

        DataItemFactoryRequest request = new DataItemFactoryRequest ();
        request.setId ( id );

        synchronized ( _templates )
        {
            for ( FactoryTemplate template : _templates )
            {
                if ( template.getPattern ().matcher ( id ).matches () )
                {
                    request.setBrowserAttributes ( template.getBrowserAttributes () );
                    request.setItemAttributes ( template.getItemAttributes () );
                    try
                    {
                        request.setItemChain ( FactoryHelper.instantiateChainList ( template.getChainEntries () ) );
                    }
                    catch ( ConfigurationError e )
                    {
                        _log.warn ( String.format ( "Unable to create item %s", id ), e );
                        return null;
                    }
                    break;
                }
            }
        }

        return retrieveItem ( request );
    }

    public DataItem retrieveItem ( DataItemFactoryRequest request )
    {
        synchronized ( _itemMap )
        {
            DataItem dataItem = lookupItem ( request.getId () );
            if ( dataItem == null )
            {
                dataItem = factoryCreate ( request );
            }
            return dataItem;
        }
    }

    public long startWriteAttributes ( Session session, String itemId, Map<String, Variant> attributes, WriteAttributesOperationListener listener ) throws InvalidSessionException, InvalidItemException
    {
        SessionCommon sessionCommon = validateSession ( session );

        final DataItem item = retrieveItem ( itemId );

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

        final DataItem item = retrieveItem ( itemName );

        if ( item == null )
            throw new InvalidItemException ( itemName );

        if ( listener == null )
            throw new NullPointerException ();

        Handle handle = scheduleOperation ( sessionCommon, new RunnableCancelOperation () {

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
            }
        } );

        return handle.getId ();
    }

    /**
     * Schedule an operation for this session
     * @param sessionCommon The session to which this operation is attached
     * @param operation The operation to perfom
     * @return The operation handle
     */
    public synchronized Handle scheduleOperation ( SessionCommon sessionCommon, Operation operation )
    {
        Handle handle = _opManager.schedule ( operation );
        sessionCommon.getOperations ().addOperation ( handle );
        return handle;
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
                    }
                };
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

    /* (non-Javadoc)
     * @see org.openscada.da.server.common.impl.ConfigurableHive#addItemFactory(org.openscada.da.server.common.DataItemFactory)
     */
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

    public void registerTemplate ( FactoryTemplate template )
    {
        synchronized ( _templates )
        {
            _templates.add ( template );
        }
    }
}
