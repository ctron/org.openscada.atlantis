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
import org.openscada.da.core.DataItemInformation;
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

    private final Set<SessionCommon> _sessions = new HashSet<SessionCommon> ();

    private final Map<DataItemInformation, DataItem> _itemMap = new HashMap<DataItemInformation, DataItem> ();

    private HiveBrowserCommon _browser = null;

    private Folder _rootFolder = null;

    private final Set<SessionListener> _sessionListeners = new CopyOnWriteArraySet<SessionListener> ();

    private final OperationManager _opManager = new OperationManager ();

    private final OperationProcessor _opProcessor = new OperationProcessor ();

    private Thread _jobQueueThread = null;

    private final List<DataItemFactory> _factoryList = new CopyOnWriteArrayList<DataItemFactory> ();

    private final Set<DataItemFactoryListener> _factoryListeners = new HashSet<DataItemFactoryListener> ();

    private final List<FactoryTemplate> _templates = new LinkedList<FactoryTemplate> ();

    private final SubscriptionManager _itemSubscriptionManager = new SubscriptionManager ();

    private final Set<DataItemValidator> _itemValidators = new CopyOnWriteArraySet<DataItemValidator> ();

    private ValidationStrategy _validatonStrategy = ValidationStrategy.FULL_CHECK;

    private HiveEventListener _hiveEventListener;

    private boolean autoEnableStats = true;

    /**
     * Services that are provided by this hive for internal use
     */
    private final Map<String, HiveService> services = new HashMap<String, HiveService> ();

    public HiveCommon ()
    {
        super ();

        this._jobQueueThread = new Thread ( this._opProcessor );
        this._jobQueueThread.setName ( "HiveOpProcessor" );
        this._jobQueueThread.setDaemon ( true );
        this._jobQueueThread.start ();

        // set the validator of the subscription manager
        this._itemSubscriptionManager.setValidator ( new SubscriptionValidator () {

            public boolean validate ( final SubscriptionListener listener, final Object topic )
            {
                return validateItem ( topic.toString () );
            }
        } );
    }

    public void start () throws Exception
    {
    }

    public void stop () throws Exception
    {
    }

    @Override
    protected void finalize () throws Throwable
    {
        this._jobQueueThread.interrupt ();
        super.finalize ();
    }

    public void dispose ()
    {
        unregisterAllServices ();
    }

    public void addSessionListener ( final SessionListener listener )
    {
        this._sessionListeners.add ( listener );
    }

    public void removeSessionListener ( final SessionListener listener )
    {
        this._sessionListeners.remove ( listener );
    }

    private void fireSessionCreate ( final SessionCommon session )
    {
        if ( this._hiveEventListener != null )
        {
            this._hiveEventListener.sessionCreated ( session );
        }

        for ( final SessionListener listener : this._sessionListeners )
        {
            try
            {
                listener.create ( session );
            }
            catch ( final Throwable e )
            {
            }
        }

    }

    private void fireSessionDestroy ( final SessionCommon session )
    {
        if ( this._hiveEventListener != null )
        {
            this._hiveEventListener.sessionDestroyed ( session );
        }

        synchronized ( this._sessionListeners )
        {
            for ( final SessionListener listener : this._sessionListeners )
            {
                try
                {
                    listener.destroy ( session );
                }
                catch ( final Exception e )
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
        return this._rootFolder;
    }

    /**
     * Set the root folder. The root folder can only be set once. All
     * further set requests are ignored.
     */
    public synchronized void setRootFolder ( final Folder rootFolder )
    {
        if ( this._rootFolder == null )
        {
            this._rootFolder = rootFolder;
            if ( rootFolder instanceof FolderCommon && this.autoEnableStats )
            {
                enableStats ( (FolderCommon)this._rootFolder );
            }
        }
    }

    private void enableStats ( final FolderCommon rootFolder )
    {
        final HiveCommonStatisticsGenerator stats = new HiveCommonStatisticsGenerator ( "statistics" );
        this._hiveEventListener = stats;

        final FolderCommon statsFolder = new FolderCommon ();
        rootFolder.add ( "statistics", statsFolder, new HashMap<String, Variant> () );

        stats.register ( this, statsFolder );
    }

    /**
     * Validate a session and return the session common instance if the session is valid
     * @param session the session to validate
     * @return the session common instance
     * @throws InvalidSessionException in the case of an invalid session
     */
    public SessionCommon validateSession ( final Session session ) throws InvalidSessionException
    {
        if ( ! ( session instanceof SessionCommon ) )
        {
            throw new InvalidSessionException ();
        }

        final SessionCommon sessionCommon = (SessionCommon)session;
        if ( sessionCommon.getHive () != this )
        {
            throw new InvalidSessionException ();
        }

        if ( !this._sessions.contains ( sessionCommon ) )
        {
            throw new InvalidSessionException ();
        }

        return sessionCommon;
    }

    // implementation of hive interface

    public Session createSession ( final Properties props )
    {
        final SessionCommon session = new SessionCommon ( this );
        synchronized ( this._sessions )
        {
            this._sessions.add ( session );
            this._opManager.addListener ( session.getOperations () );
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
    public void closeSession ( final Session session ) throws InvalidSessionException
    {
        final SessionCommon sessionCommon = validateSession ( session );

        synchronized ( this._sessions )
        {
            _log.debug ( "Close session: " + session );
            fireSessionDestroy ( (SessionCommon)session );

            // destroy all subscriptions for this session
            this._itemSubscriptionManager.unsubscribeAll ( sessionCommon );

            // cancel all pending operations
            sessionCommon.getOperations ().cancelAll ();

            this._sessions.remove ( session );
        }
    }

    public void subscribeItem ( final Session session, final String itemId ) throws InvalidSessionException, InvalidItemException
    {
        // validate the session first
        final SessionCommon sessionCommon = validateSession ( session );

        // subscribe using the new item subscription manager
        try
        {
            retrieveItem ( itemId );
            this._itemSubscriptionManager.subscribe ( itemId, sessionCommon );
        }
        catch ( final ValidationException e )
        {
            throw new InvalidItemException ( itemId );
        }
    }

    /**
     * Unsubscribe a session from an item
     */
    public void unsubscribeItem ( final Session session, final String itemId ) throws InvalidSessionException, InvalidItemException
    {
        final SessionCommon sessionCommon = validateSession ( session );

        // unsubscribe using the new item subscription manager
        this._itemSubscriptionManager.unsubscribe ( itemId, sessionCommon );
    }

    /**
     * Register a new item with the hive
     * @param item the item to register
     */
    public void registerItem ( final DataItem item )
    {
        synchronized ( this._itemMap )
        {
            final String id = item.getInformation ().getName ();

            if ( !this._itemMap.containsKey ( new DataItemInformationBase ( item.getInformation () ) ) )
            {
                // first add internally ...
                this._itemMap.put ( new DataItemInformationBase ( item.getInformation () ), item );

                if ( this._hiveEventListener != null )
                {
                    this._hiveEventListener.itemRegistered ( item );
                }
            }

            // add new topic to the new item subscription manager
            this._itemSubscriptionManager.setSource ( id, new DataItemSubscriptionSource ( item, this._hiveEventListener ) );
        }
    }

    /**
     * Remove an item from the hive.
     * @param item the item to remove
     */
    public void unregisterItem ( final DataItem item )
    {
        synchronized ( this._itemMap )
        {
            final String id = item.getInformation ().getName ();
            if ( this._itemMap.containsKey ( new DataItemInformationBase ( item.getInformation () ) ) )
            {
                this._itemMap.remove ( new DataItemInformationBase ( item.getInformation () ) );
                if ( this._hiveEventListener != null )
                {
                    this._hiveEventListener.itemUnregistered ( item );
                }
            }

            // remove the source from the manager
            this._itemSubscriptionManager.setSource ( id, null );
        }
    }

    /**
     * Get an item from the list of registered items.
     * @param itemId the item to find
     * @return the data item or <code>null</code> if no item with that ID is registered
     */
    protected DataItem findRegisteredDataItem ( final String itemId )
    {
        synchronized ( this._itemMap )
        {
            return this._itemMap.get ( itemId );
        }
    }

    private DataItem factoryCreate ( final DataItemFactoryRequest request )
    {
        for ( final DataItemFactory factory : this._factoryList )
        {
            if ( factory.canCreate ( request ) )
            {
                final DataItem dataItem = factory.create ( request );
                registerItem ( dataItem );
                fireDataItemCreated ( dataItem );
                return dataItem;
            }
        }
        return null;
    }

    /**
     * Validate a data item id if it can be provided by the hive.
     * <p>
     * The hive will perform several methods to check if the item id is valid.
     * <p>
     * Implementations must not create items based an a validation check!
     *  
     * @return <code>true</code> if the item id is valid <code>false</code> otherwise
     */
    public boolean validateItem ( final String id )
    {
        if ( this._validatonStrategy == ValidationStrategy.GRANT_ALL )
        {
            return true;
        }

        // First check if the item already exists
        if ( lookupItem ( id ) != null )
        {
            return true;
        }

        // now check if the item passes the validators
        for ( final DataItemValidator dataItemValidator : this._itemValidators )
        {
            if ( dataItemValidator.isValid ( id ) )
            {
                return true;
            }
        }

        final DataItemFactoryRequest request = new DataItemFactoryRequest ();
        request.setId ( id );

        synchronized ( this._factoryList )
        {
            for ( final DataItemFactory factory : this._factoryList )
            {
                if ( factory.canCreate ( request ) )
                {
                    return true;
                }
            }
        }
        return false;
    }

    public DataItem lookupItem ( final String id )
    {
        return this._itemMap.get ( new DataItemInformationBase ( id ) );
    }

    public FactoryTemplate findFactoryTemplate ( final String item )
    {
        synchronized ( this._templates )
        {
            for ( final FactoryTemplate template : this._templates )
            {
                if ( template.getPattern ().matcher ( item ).matches () )
                {
                    return template;
                }
            }
        }
        return null;
    }

    public DataItem retrieveItem ( final String id )
    {
        // if we already have the item we don't need to create it
        final DataItem dataItem = lookupItem ( id );
        if ( dataItem != null )
        {
            return dataItem;
        }

        final DataItemFactoryRequest request = new DataItemFactoryRequest ();
        request.setId ( id );

        final FactoryTemplate template = findFactoryTemplate ( id );
        if ( template != null )
        {
            request.setBrowserAttributes ( template.getBrowserAttributes () );
            request.setItemAttributes ( template.getItemAttributes () );
            try
            {
                request.setItemChain ( FactoryHelper.instantiateChainList ( this, template.getChainEntries () ) );
            }
            catch ( final ConfigurationError e )
            {
                _log.warn ( String.format ( "Unable to create item %s", id ), e );
                return null;
            }
        }

        return retrieveItem ( request );
    }

    public DataItem retrieveItem ( final DataItemFactoryRequest request )
    {
        synchronized ( this._itemMap )
        {
            DataItem dataItem = lookupItem ( request.getId () );
            if ( dataItem == null )
            {
                dataItem = factoryCreate ( request );
            }
            return dataItem;
        }
    }

    public long startWriteAttributes ( final Session session, final String itemId, final Map<String, Variant> attributes, final WriteAttributesOperationListener listener ) throws InvalidSessionException, InvalidItemException
    {
        final SessionCommon sessionCommon = validateSession ( session );

        final DataItem item = retrieveItem ( itemId );

        if ( item == null )
        {
            throw new InvalidItemException ( itemId );
        }

        if ( listener == null )
        {
            throw new NullPointerException ();
        }

        final WriteAttributesOperation op = new WriteAttributesOperation ( item, listener, attributes );
        final Handle handle = this._opManager.schedule ( op );

        synchronized ( sessionCommon )
        {
            sessionCommon.getOperations ().addOperation ( handle );
        }

        if ( this._hiveEventListener != null )
        {
            this._hiveEventListener.startWriteAttributes ( session, itemId, attributes.size () );
        }

        return handle.getId ();
    }

    public long startWrite ( final Session session, final String itemName, final Variant value, final WriteOperationListener listener ) throws InvalidSessionException, InvalidItemException
    {
        final SessionCommon sessionCommon = validateSession ( session );

        final DataItem item = retrieveItem ( itemName );

        if ( item == null )
        {
            throw new InvalidItemException ( itemName );
        }

        if ( listener == null )
        {
            throw new NullPointerException ();
        }

        final Handle handle = scheduleOperation ( sessionCommon, new RunnableCancelOperation () {

            public void run ()
            {
                try
                {
                    item.writeValue ( value );
                    if ( !isCanceled () )
                    {
                        listener.success ();
                    }
                }
                catch ( final Throwable e )
                {
                    if ( !isCanceled () )
                    {
                        listener.failure ( e );
                    }
                }
            }
        } );

        if ( this._hiveEventListener != null )
        {
            this._hiveEventListener.startWrite ( session, itemName, value );
        }

        return handle.getId ();
    }

    /**
     * Schedule an operation for this session
     * @param sessionCommon The session to which this operation is attached
     * @param operation The operation to perfom
     * @return The operation handle
     */
    public synchronized Handle scheduleOperation ( final SessionCommon sessionCommon, final Operation operation )
    {
        final Handle handle = this._opManager.schedule ( operation );
        sessionCommon.getOperations ().addOperation ( handle );
        return handle;
    }

    public synchronized HiveBrowser getBrowser ()
    {
        if ( this._browser == null )
        {
            if ( this._rootFolder != null )
            {
                this._browser = new HiveBrowserCommon ( this ) {

                    @Override
                    public Folder getRootFolder ()
                    {
                        return HiveCommon.this._rootFolder;
                    }
                };
            }
        }

        return this._browser;
    }

    public void cancelOperation ( final Session session, final long id ) throws CancellationNotSupportedException, InvalidSessionException
    {
        final SessionCommon sessionCommon = validateSession ( session );

        synchronized ( sessionCommon )
        {
            _log.info ( String.format ( "Cancelling operation: %d", id ) );

            final Handle handle = this._opManager.get ( id );
            if ( handle != null )
            {
                if ( sessionCommon.getOperations ().containsOperation ( handle ) )
                {
                    try
                    {
                        handle.cancel ();
                    }
                    catch ( final CancelNotSupportedException e )
                    {
                        throw new CancellationNotSupportedException ();
                    }
                }
            }
        }
    }

    public void thawOperation ( final Session session, final long id ) throws InvalidSessionException
    {
        final SessionCommon sessionCommon = validateSession ( session );

        synchronized ( sessionCommon )
        {
            _log.debug ( String.format ( "Thawing operation %d", id ) );

            final Handle handle = this._opManager.get ( id );
            if ( handle != null )
            {
                if ( sessionCommon.getOperations ().containsOperation ( handle ) )
                {
                    this._opProcessor.add ( handle );
                }
            }
            else
            {
                _log.warn ( String.format ( "%d is not a valid operation id", id ) );
            }
        }
    }

    /* (non-Javadoc)
     * @see org.openscada.da.server.common.impl.ConfigurableHive#addItemFactory(org.openscada.da.server.common.DataItemFactory)
     */
    public void addItemFactory ( final DataItemFactory factory )
    {
        this._factoryList.add ( factory );
    }

    public void removeItemFactory ( final DataItemFactory factory )
    {
        this._factoryList.remove ( factory );
    }

    public void addItemFactoryListener ( final DataItemFactoryListener listener )
    {
        this._factoryListeners.add ( listener );
    }

    public void removeItemFactoryListener ( final DataItemFactoryListener listener )
    {
        this._factoryListeners.remove ( listener );
    }

    private void fireDataItemCreated ( final DataItem dataItem )
    {
        for ( final DataItemFactoryListener listener : this._factoryListeners )
        {
            listener.created ( dataItem );
        }
    }

    public void registerTemplate ( final FactoryTemplate template )
    {
        synchronized ( this._templates )
        {
            this._templates.add ( template );
        }
    }

    /**
     * Will re-check all items in granted state. Call when your list of known items
     * has changed in order to give granted but not connected subscriptions a chance
     * to be created by the factories.
     */
    public void recheckGrantedItems ()
    {
        final List<Object> topics = this._itemSubscriptionManager.getAllGrantedTopics ();

        for ( final Object topic : topics )
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
        final List<Object> topics = this._itemSubscriptionManager.getAllGrantedTopics ();

        final Set<String> items = new HashSet<String> ();

        for ( final Object topic : topics )
        {
            items.add ( topic.toString () );
        }
        return items;
    }

    public void addDataItemValidator ( final DataItemValidator dataItemValidator )
    {
        this._itemValidators.add ( dataItemValidator );
    }

    public void removeItemValidator ( final DataItemValidator dataItemValidator )
    {
        this._itemValidators.remove ( dataItemValidator );
    }

    protected ValidationStrategy getValidatonStrategy ()
    {
        return this._validatonStrategy;
    }

    protected void setValidatonStrategy ( final ValidationStrategy validatonStrategy )
    {
        this._validatonStrategy = validatonStrategy;
    }

    public boolean isAutoEnableStats ()
    {
        return this.autoEnableStats;
    }

    /**
     * This will disable the automatic generation of the stats module when setting
     * the root folder. Must be called before {@link #setRootFolder(Folder)}
     * @param autoEnableStats
     */
    public void setAutoEnableStats ( final boolean autoEnableStats )
    {
        this.autoEnableStats = autoEnableStats;
    }

    /* (non-Javadoc)
     * @see org.openscada.da.server.common.impl.HiveServiceRegistry#registerService(java.lang.String, org.openscada.da.server.common.HiveService)
     */
    public HiveService registerService ( final String serviceName, final HiveService service )
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
    public HiveService unregisterService ( final String serviceName )
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
        for ( final HiveService service : services )
        {
            service.dispose ();
        }
    }

    /* (non-Javadoc)
     * @see org.openscada.da.server.common.impl.HiveServiceRegistry#getService(java.lang.String)
     */
    public HiveService getService ( final String serviceName )
    {
        return this.services.get ( serviceName );
    }
}
