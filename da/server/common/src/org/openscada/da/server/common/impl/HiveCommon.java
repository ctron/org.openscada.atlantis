/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006, 2008 inavare GmbH (http://inavare.com)
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;

import org.apache.log4j.Logger;
import org.openscada.core.CancellationNotSupportedException;
import org.openscada.core.InvalidSessionException;
import org.openscada.core.Variant;
import org.openscada.core.subscription.SubscriptionListener;
import org.openscada.core.subscription.SubscriptionManager;
import org.openscada.core.subscription.SubscriptionValidator;
import org.openscada.core.subscription.ValidationException;
import org.openscada.da.core.server.DataItemInformation;
import org.openscada.da.core.server.Hive;
import org.openscada.da.core.server.InvalidItemException;
import org.openscada.da.core.server.Session;
import org.openscada.da.core.server.WriteAttributesOperationListener;
import org.openscada.da.core.server.WriteOperationListener;
import org.openscada.da.core.server.browser.HiveBrowser;
import org.openscada.da.server.browser.common.Folder;
import org.openscada.da.server.browser.common.FolderCommon;
import org.openscada.da.server.common.DataItem;
import org.openscada.da.server.common.DataItemInformationBase;
import org.openscada.da.server.common.HiveService;
import org.openscada.da.server.common.HiveServiceRegistry;
import org.openscada.da.server.common.ValidationStrategy;
import org.openscada.da.server.common.configuration.ConfigurableHive;
import org.openscada.da.server.common.configuration.ConfigurationError;
import org.openscada.da.server.common.factory.DataItemFactory;
import org.openscada.da.server.common.factory.DataItemFactoryListener;
import org.openscada.da.server.common.factory.DataItemFactoryRequest;
import org.openscada.da.server.common.factory.DataItemValidator;
import org.openscada.da.server.common.factory.FactoryHelper;
import org.openscada.da.server.common.factory.FactoryTemplate;
import org.openscada.da.server.common.impl.stats.HiveCommonStatisticsGenerator;
import org.openscada.da.server.common.impl.stats.HiveEventListener;
import org.openscada.utils.jobqueue.CancelNotSupportedException;
import org.openscada.utils.jobqueue.Operation;
import org.openscada.utils.jobqueue.OperationManager;
import org.openscada.utils.jobqueue.OperationProcessor;
import org.openscada.utils.jobqueue.RunnableCancelOperation;
import org.openscada.utils.jobqueue.OperationManager.Handle;

public class HiveCommon implements Hive, ConfigurableHive, HiveServiceRegistry
{

    private static Logger _log = Logger.getLogger ( HiveCommon.class );

    private Set<SessionCommon> _sessions = new HashSet<SessionCommon> ();

    private Map<DataItemInformation, DataItem> _itemMap = new HashMap<DataItemInformation, DataItem> ();

    private HiveBrowserCommon _browser = null;

    private Folder _rootFolder = null;

    private Set<SessionListener> _sessionListeners = new CopyOnWriteArraySet<SessionListener> ();

    private OperationManager _opManager = new OperationManager ();

    private OperationProcessor _opProcessor = new OperationProcessor ();

    private Thread _jobQueueThread = null;

    private List<DataItemFactory> _factoryList = new CopyOnWriteArrayList<DataItemFactory> ();

    private Set<DataItemFactoryListener> _factoryListeners = new HashSet<DataItemFactoryListener> ();

    private List<FactoryTemplate> _templates = new LinkedList<FactoryTemplate> ();

    private SubscriptionManager _itemSubscriptionManager = new SubscriptionManager ();

    private Set<DataItemValidator> _itemValidators = new CopyOnWriteArraySet<DataItemValidator> ();

    private ValidationStrategy _validatonStrategy = ValidationStrategy.FULL_CHECK;

    private HiveEventListener _hiveEventListener;

    private boolean autoEnableStats = true;

    /**
     * Services that are provided by this hive for internal use
     */
    private Map<String, HiveService> services = new HashMap<String, HiveService> ();

    public HiveCommon ()
    {
        super ();

        _jobQueueThread = new Thread ( _opProcessor );
        _jobQueueThread.setName ( "HiveOpProcessor" );
        _jobQueueThread.setDaemon ( true );
        _jobQueueThread.start ();

        // set the validator of the subscription manager
        _itemSubscriptionManager.setValidator ( new SubscriptionValidator () {

            public boolean validate ( SubscriptionListener listener, Object topic )
            {
                return validateItem ( topic.toString () );
            }
        } );
    }

    @Override
    protected void finalize () throws Throwable
    {
        _jobQueueThread.interrupt ();
        super.finalize ();
    }
    
    public void dispose ()
    {
        unregisterAllServices ();
    }

    public void addSessionListener ( SessionListener listener )
    {
        _sessionListeners.add ( listener );
    }

    public void removeSessionListener ( SessionListener listener )
    {
        _sessionListeners.remove ( listener );
    }

    private void fireSessionCreate ( SessionCommon session )
    {
        if ( _hiveEventListener != null )
        {
            _hiveEventListener.sessionCreated ( session );
        }

        for ( SessionListener listener : _sessionListeners )
        {
            try
            {
                listener.create ( session );
            }
            catch ( Throwable e )
            {
            }
        }

    }

    private void fireSessionDestroy ( SessionCommon session )
    {
        if ( _hiveEventListener != null )
        {
            _hiveEventListener.sessionDestroyed ( session );
        }

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

    /**
     * Get the root folder
     * @return the root folder or <code>null</code> if browsing is not supported
     */
    public Folder getRootFolder ()
    {
        return _rootFolder;
    }

    /**
     * Set the root folder. The root folder can only be set once. All
     * further set requests are ignored.
     */
    public synchronized void setRootFolder ( Folder rootFolder )
    {
        if ( _rootFolder == null )
        {
            _rootFolder = rootFolder;
            if ( rootFolder instanceof FolderCommon && autoEnableStats )
            {
                enableStats ( (FolderCommon)_rootFolder );
            }
        }
    }

    private void enableStats ( FolderCommon rootFolder )
    {
        HiveCommonStatisticsGenerator stats = new HiveCommonStatisticsGenerator ( "statistics" );
        _hiveEventListener = stats;

        FolderCommon statsFolder = new FolderCommon ();
        rootFolder.add ( "statistics", statsFolder, new HashMap<String, Variant> () );

        stats.register ( this, statsFolder );
    }

    /**
     * Validate a session and return the session common instance if the session is valid
     * @param session the session to validate
     * @return the session common instance
     * @throws InvalidSessionException in the case of an invalid session
     */
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

    /**
     * Close a session.
     * 
     * The session will be invalid after it has been closed. All subscriptions
     * will become invalid. All pending operation will get canceled. 
     */
    public void closeSession ( Session session ) throws InvalidSessionException
    {
        SessionCommon sessionCommon = validateSession ( session );

        synchronized ( _sessions )
        {
            _log.debug ( "Close session: " + session );
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
            retrieveItem ( itemId );
            _itemSubscriptionManager.subscribe ( itemId, sessionCommon );
        }
        catch ( ValidationException e )
        {
            throw new InvalidItemException ( itemId );
        }
    }

    /**
     * Unsubscribe a session from an item
     */
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

                if ( _hiveEventListener != null )
                {
                    _hiveEventListener.itemRegistered ( item );
                }
            }

            // add new topic to the new item subscription manager
            _itemSubscriptionManager.setSource ( id, new DataItemSubscriptionSource ( item, _hiveEventListener ) );
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
                if ( _hiveEventListener != null )
                {
                    _hiveEventListener.itemUnregistered ( item );
                }
            }

            // remove the source from the manager
            _itemSubscriptionManager.setSource ( id, null );
        }
    }

    /**
     * Get an item from the list of registered items.
     * @param itemId the item to find
     * @return the data item or <code>null</code> if no item with that ID is registered
     */
    protected DataItem findRegisteredDataItem ( String itemId )
    {
        synchronized ( _itemMap )
        {
            return _itemMap.get ( itemId );
        }
    }

    private DataItem factoryCreate ( DataItemFactoryRequest request )
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
        return null;
    }

    public boolean validateItem ( String id )
    {
        if ( _validatonStrategy == ValidationStrategy.GRANT_ALL )
        {
            return true;
        }

        // First check if the item already exists
        if ( lookupItem ( id ) != null )
        {
            return true;
        }

        // now check if the item passes the validators
        for ( DataItemValidator dataItemValidator : _itemValidators )
        {
            if ( dataItemValidator.isValid ( id ) )
            {
                return true;
            }
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
        return _itemMap.get ( new DataItemInformationBase ( id ) );
    }

    public FactoryTemplate findFactoryTemplate ( String item )
    {
        synchronized ( _templates )
        {
            for ( FactoryTemplate template : _templates )
            {
                if ( template.getPattern ().matcher ( item ).matches () )
                {
                    return template;
                }
            }
        }
        return null;
    }

    public DataItem retrieveItem ( String id )
    {
        // if we already have the item we don't need to create it
        DataItem dataItem = lookupItem ( id );
        if ( dataItem != null )
        {
            return dataItem;
        }

        DataItemFactoryRequest request = new DataItemFactoryRequest ();
        request.setId ( id );

        FactoryTemplate template = findFactoryTemplate ( id );
        if ( template != null )
        {
            request.setBrowserAttributes ( template.getBrowserAttributes () );
            request.setItemAttributes ( template.getItemAttributes () );
            try
            {
                request.setItemChain ( FactoryHelper.instantiateChainList ( this, template.getChainEntries () ) );
            }
            catch ( ConfigurationError e )
            {
                _log.warn ( String.format ( "Unable to create item %s", id ), e );
                return null;
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

        if ( _hiveEventListener != null )
        {
            _hiveEventListener.startWriteAttributes ( session, itemId, attributes.size () );
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
                    item.writeValue ( value );
                    if ( !isCanceled () )
                        listener.success ();
                }
                catch ( Throwable e )
                {
                    if ( !isCanceled () )
                        listener.failure ( e );
                }
            }
        } );

        if ( _hiveEventListener != null )
        {
            _hiveEventListener.startWrite ( session, itemName, value );
        }

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
            _log.debug ( String.format ( "Thawing operation %d", id ) );

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
        _factoryList.add ( factory );
    }

    public void removeItemFactory ( DataItemFactory factory )
    {
        _factoryList.remove ( factory );
    }

    public void addItemFactoryListener ( DataItemFactoryListener listener )
    {
        _factoryListeners.add ( listener );
    }

    public void removeItemFactoryListener ( DataItemFactoryListener listener )
    {
        _factoryListeners.remove ( listener );
    }

    private void fireDataItemCreated ( DataItem dataItem )
    {
        for ( DataItemFactoryListener listener : _factoryListeners )
        {
            listener.created ( dataItem );
        }
    }

    public void registerTemplate ( FactoryTemplate template )
    {
        synchronized ( _templates )
        {
            _templates.add ( template );
        }
    }

    /**
     * Will re-check all items in granted state. Call when your list of known items
     * has changed in order to give granted but not connected subscriptions a chance
     * to be created by the factories.
     */
    public void recheckGrantedItems ()
    {
        List<Object> topics = _itemSubscriptionManager.getAllGrantedTopics ();

        for ( Object topic : topics )
        {
            retrieveItem ( topic.toString () );
        }
    }

    /**
     * Gets a set of all items in granted state.
     * @return The list of granted items.
     */
    public Set<String> getGrantedItems ()
    {
        List<Object> topics = _itemSubscriptionManager.getAllGrantedTopics ();

        Set<String> items = new HashSet<String> ();

        for ( Object topic : topics )
        {
            items.add ( topic.toString () );
        }
        return items;
    }

    public void addDataItemValidator ( DataItemValidator dataItemValidator )
    {
        _itemValidators.add ( dataItemValidator );
    }

    public void removeItemValidator ( DataItemValidator dataItemValidator )
    {
        _itemValidators.remove ( dataItemValidator );
    }

    protected ValidationStrategy getValidatonStrategy ()
    {
        return _validatonStrategy;
    }

    protected void setValidatonStrategy ( ValidationStrategy validatonStrategy )
    {
        _validatonStrategy = validatonStrategy;
    }

    public boolean isAutoEnableStats ()
    {
        return autoEnableStats;
    }

    /**
     * This will disable the automatic generation of the stats module when setting
     * the root folder. Must be called before {@link #setRootFolder(Folder)}
     * @param autoEnableStats
     */
    public void setAutoEnableStats ( boolean autoEnableStats )
    {
        this.autoEnableStats = autoEnableStats;
    }

    /* (non-Javadoc)
     * @see org.openscada.da.server.common.impl.HiveServiceRegistry#registerService(java.lang.String, org.openscada.da.server.common.HiveService)
     */
    public HiveService registerService ( String serviceName, HiveService service )
    {
        HiveService oldService = null;
        synchronized ( this.services )
        {
            oldService = this.services.put ( serviceName, service );
        }

        if ( oldService != null )
        {
            oldService.dispose ();
        }
        if ( service != null )
        {
            service.init ();
        }
        
        return oldService;
    }

    /* (non-Javadoc)
     * @see org.openscada.da.server.common.impl.HiveServiceRegistry#unregisterService(java.lang.String)
     */
    public HiveService unregisterService ( String serviceName )
    {
        HiveService service = null;
        synchronized ( this.services )
        {
            service = this.services.remove ( serviceName );
        }

        if ( service != null )
        {
            service.dispose ();
        }
        
        return service;
    }

    /**
     * Unregister all the services at once
     */
    protected void unregisterAllServices ()
    {
        Collection<HiveService> services;
        synchronized ( this.services )
        {
            services = new ArrayList<HiveService> ( this.services.values () );
            this.services.clear ();
        }
        
        // now dispose all
        for ( HiveService service : services )
        {
            service.dispose ();
        }
    }

    /* (non-Javadoc)
     * @see org.openscada.da.server.common.impl.HiveServiceRegistry#getService(java.lang.String)
     */
    public HiveService getService ( String serviceName )
    {
        return this.services.get ( serviceName );
    }
}
