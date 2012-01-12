/*
 * This file is part of the openSCADA project
 * Copyright (C) 2006-2011 TH4 SYSTEMS GmbH (http://th4-systems.com)
 *
 * openSCADA is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 3
 * only, as published by the Free Software Foundation.
 *
 * openSCADA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License version 3 for more details
 * (a copy is included in the LICENSE file that accompanied this code).
 *
 * You should have received a copy of the GNU Lesser General Public License
 * version 3 along with openSCADA. If not, see
 * <http://opensource.org/licenses/lgpl-3.0.html> for a copy of the LGPLv3 License.
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
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.openscada.core.InvalidSessionException;
import org.openscada.core.UnableToCreateSessionException;
import org.openscada.core.Variant;
import org.openscada.core.server.common.ServiceCommon;
import org.openscada.core.subscription.SubscriptionListener;
import org.openscada.core.subscription.SubscriptionManager;
import org.openscada.core.subscription.SubscriptionValidator;
import org.openscada.core.subscription.ValidationException;
import org.openscada.da.core.DataItemInformation;
import org.openscada.da.core.OperationParameters;
import org.openscada.da.core.WriteAttributeResults;
import org.openscada.da.core.WriteResult;
import org.openscada.da.core.server.Hive;
import org.openscada.da.core.server.InvalidItemException;
import org.openscada.da.core.server.Session;
import org.openscada.da.core.server.browser.HiveBrowser;
import org.openscada.da.server.browser.common.Folder;
import org.openscada.da.server.browser.common.FolderCommon;
import org.openscada.da.server.common.DataItem;
import org.openscada.da.server.common.DataItemInformationBase;
import org.openscada.da.server.common.HiveService;
import org.openscada.da.server.common.HiveServiceRegistry;
import org.openscada.da.server.common.ValidationStrategy;
import org.openscada.da.server.common.configuration.ConfigurableHive;
import org.openscada.da.server.common.factory.DataItemFactory;
import org.openscada.da.server.common.factory.DataItemFactoryListener;
import org.openscada.da.server.common.factory.DataItemValidator;
import org.openscada.da.server.common.factory.FactoryTemplate;
import org.openscada.da.server.common.impl.stats.HiveCommonStatisticsGenerator;
import org.openscada.sec.AuthorizationResult;
import org.openscada.sec.PermissionDeniedException;
import org.openscada.sec.UserInformation;
import org.openscada.utils.concurrent.NamedThreadFactory;
import org.openscada.utils.concurrent.NotifyFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HiveCommon extends ServiceCommon implements Hive, ConfigurableHive, HiveServiceRegistry
{
    private final static Logger logger = LoggerFactory.getLogger ( HiveCommon.class );

    private final Set<SessionCommon> sessions = new HashSet<SessionCommon> ();

    private final Map<DataItemInformation, DataItem> itemMap = new HashMap<DataItemInformation, DataItem> ();

    private HiveBrowserCommon browser = null;

    private Folder rootFolder = null;

    private final Set<SessionListener> sessionListeners = new CopyOnWriteArraySet<SessionListener> ();

    private ExecutorService operationService;

    private final List<DataItemFactory> factoryList = new CopyOnWriteArrayList<DataItemFactory> ();

    private final Set<DataItemFactoryListener> factoryListeners = new HashSet<DataItemFactoryListener> ();

    private final List<FactoryTemplate> templates = new LinkedList<FactoryTemplate> ();

    private final SubscriptionManager itemSubscriptionManager = new SubscriptionManager ();

    private final Set<DataItemValidator> itemValidators = new CopyOnWriteArraySet<DataItemValidator> ();

    private ValidationStrategy validationStrategy = ValidationStrategy.GRANT_ALL;

    private HiveCommonStatisticsGenerator statisticsGenerator;

    private boolean autoEnableStats = true;

    /**
     * Services that are provided by this hive for internal use
     */
    private final Map<String, HiveService> services = new HashMap<String, HiveService> ();

    public HiveCommon ()
    {
        super ();

        // set the validator of the subscription manager
        this.itemSubscriptionManager.setValidator ( new SubscriptionValidator () {

            @Override
            public boolean validate ( final SubscriptionListener listener, final Object topic )
            {
                return validateItem ( topic.toString () );
            }
        } );

    }

    @Override
    public void start () throws Exception
    {
        logger.info ( "Starting Hive" );

        this.operationService = Executors.newFixedThreadPool ( 1, new NamedThreadFactory ( "HiveCommon" ) );
    }

    @Override
    public void stop () throws Exception
    {
        logger.info ( "Stopping hive" );

        disableStats ();

        if ( this.browser != null )
        {
            this.browser.stop ();
            this.browser = null;
        }

        this.operationService.shutdown ();
        this.operationService = null;

        unregisterAllServices ();
    }

    public void addSessionListener ( final SessionListener listener )
    {
        this.sessionListeners.add ( listener );
    }

    public void removeSessionListener ( final SessionListener listener )
    {
        this.sessionListeners.remove ( listener );
    }

    private void fireSessionCreate ( final SessionCommon session )
    {
        if ( this.statisticsGenerator != null )
        {
            this.statisticsGenerator.sessionCreated ( session );
        }

        for ( final SessionListener listener : this.sessionListeners )
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
        if ( this.statisticsGenerator != null )
        {
            this.statisticsGenerator.sessionDestroyed ( session );
        }

        synchronized ( this.sessionListeners )
        {
            for ( final SessionListener listener : this.sessionListeners )
            {
                try
                {
                    listener.destroy ( session );
                }
                catch ( final Throwable e )
                {
                }
            }
        }
    }

    /**
     * Get the root folder
     * @return the root folder or <code>null</code> if browsing is not supported
     */
    @Override
    public Folder getRootFolder ()
    {
        return this.rootFolder;
    }

    /**
     * Set the root folder. The root folder can only be set once. All
     * further set requests are ignored.
     */
    @Override
    public synchronized void setRootFolder ( final Folder rootFolder )
    {
        if ( this.rootFolder == null )
        {
            this.rootFolder = rootFolder;
            if ( rootFolder instanceof FolderCommon && this.autoEnableStats )
            {
                enableStats ( (FolderCommon)this.rootFolder );
            }
        }
    }

    private void enableStats ( final FolderCommon rootFolder )
    {
        disableStats ();

        final HiveCommonStatisticsGenerator stats = new HiveCommonStatisticsGenerator ( "statistics" );
        this.statisticsGenerator = stats;

        final FolderCommon statsFolder = new FolderCommon ();
        rootFolder.add ( "statistics", statsFolder, new HashMap<String, Variant> () );

        stats.register ( this, statsFolder );
    }

    private void disableStats ()
    {
        if ( this.statisticsGenerator == null )
        {
            return;
        }

        this.statisticsGenerator.unregister ();
        this.statisticsGenerator = null;

        if ( this.rootFolder instanceof FolderCommon && this.autoEnableStats )
        {
            ( (FolderCommon)this.rootFolder ).remove ( "statistics" );
        }
    }

    /**
     * Validate a session and return the session common instance if the session is valid
     * @param session the session to validate
     * @return the session common instance
     * @throws InvalidSessionException in the case of an invalid session
     */
    public SessionCommon validateSession ( final org.openscada.core.server.Session session ) throws InvalidSessionException
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

        if ( !this.sessions.contains ( sessionCommon ) )
        {
            throw new InvalidSessionException ();
        }

        return sessionCommon;
    }

    // implementation of hive interface

    @Override
    public Session createSession ( final Properties props ) throws UnableToCreateSessionException
    {
        final Map<String, String> sessionProperties = new HashMap<String, String> ();
        final UserInformation user = createUserInformation ( props, sessionProperties );
        final SessionCommon session = new SessionCommon ( this, user, sessionProperties );

        synchronized ( this.sessions )
        {
            this.sessions.add ( session );
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
    @Override
    public void closeSession ( final org.openscada.core.server.Session session ) throws InvalidSessionException
    {
        final SessionCommon sessionCommon = validateSession ( session );

        synchronized ( this.sessions )
        {
            this.sessions.remove ( session );
        }

        logger.debug ( "Close session: " + session );
        fireSessionDestroy ( (SessionCommon)session );

        // destroy all subscriptions for this session
        this.itemSubscriptionManager.unsubscribeAll ( sessionCommon );

        // cancel all pending operations
        sessionCommon.dispose ();
    }

    @Override
    public void subscribeItem ( final Session session, final String itemId ) throws InvalidSessionException, InvalidItemException
    {
        // validate the session first
        final SessionCommon sessionCommon = validateSession ( session );

        // subscribe using the new item subscription manager
        try
        {
            retrieveItem ( itemId );
            this.itemSubscriptionManager.subscribe ( itemId, sessionCommon );
        }
        catch ( final ValidationException e )
        {
            throw new InvalidItemException ( itemId );
        }
    }

    /**
     * Unsubscribe a session from an item
     */
    @Override
    public void unsubscribeItem ( final Session session, final String itemId ) throws InvalidSessionException, InvalidItemException
    {
        final SessionCommon sessionCommon = validateSession ( session );

        // unsubscribe using the new item subscription manager
        this.itemSubscriptionManager.unsubscribe ( itemId, sessionCommon );
    }

    /**
     * Register a new item with the hive
     * @param item the item to register
     */
    @Override
    public void registerItem ( final DataItem item )
    {
        synchronized ( this.itemMap )
        {
            final String id = item.getInformation ().getName ();
            final DataItemInformationBase information = new DataItemInformationBase ( item.getInformation () );

            if ( !this.itemMap.containsKey ( information ) )
            {
                // first add internally ...
                this.itemMap.put ( information, item );

                if ( this.statisticsGenerator != null )
                {
                    this.statisticsGenerator.itemRegistered ( item );
                }
            }
            else
            {
                logger.warn ( String.format ( "Duplicate error: item %s already registered with hive", item.getInformation ().getName () ) );
            }

            // add new topic to the new item subscription manager
            this.itemSubscriptionManager.setSource ( id, new DataItemSubscriptionSource ( getOperationService (), item, this.statisticsGenerator ) );
        }
    }

    private Executor getOperationServiceInstance ()
    {
        return this.operationService;
    }

    public Executor getOperationService ()
    {
        return new Executor () {
            @Override
            public void execute ( final Runnable command )
            {
                getOperationServiceInstance ().execute ( command );
            }
        };
    }

    /**
     * Remove an item from the hive.
     * @param item the item to remove
     */
    public void unregisterItem ( final DataItem item )
    {
        synchronized ( this.itemMap )
        {
            final DataItemInformationBase information = new DataItemInformationBase ( item.getInformation () );
            if ( this.itemMap.containsKey ( information ) )
            {
                this.itemMap.remove ( information );
                if ( this.statisticsGenerator != null )
                {
                    this.statisticsGenerator.itemUnregistered ( item );
                }
            }

            // remove the source from the manager
            this.itemSubscriptionManager.setSource ( item.getInformation ().getName (), null );
        }
    }

    private DataItem factoryCreate ( final String id )
    {
        for ( final DataItemFactory factory : this.factoryList )
        {
            if ( factory.canCreate ( id ) )
            {
                // we can create the item

                final DataItem dataItem = factory.create ( id );

                // stats
                fireDataItemCreated ( dataItem );
                registerItem ( dataItem );
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
    @Override
    public boolean validateItem ( final String id )
    {
        if ( this.validationStrategy == ValidationStrategy.GRANT_ALL )
        {
            return true;
        }

        // First check if the item already exists
        if ( lookupItem ( id ) != null )
        {
            return true;
        }

        // now check if the item passes the validators
        for ( final DataItemValidator dataItemValidator : this.itemValidators )
        {
            if ( dataItemValidator.isValid ( id ) )
            {
                return true;
            }
        }

        synchronized ( this.factoryList )
        {
            for ( final DataItemFactory factory : this.factoryList )
            {
                if ( factory.canCreate ( id ) )
                {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public DataItem lookupItem ( final String id )
    {
        return this.itemMap.get ( new DataItemInformationBase ( id ) );
    }

    public FactoryTemplate findFactoryTemplate ( final String item )
    {
        synchronized ( this.templates )
        {
            for ( final FactoryTemplate template : this.templates )
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
        synchronized ( this.itemMap )
        {
            DataItem dataItem = lookupItem ( id );
            if ( dataItem == null )
            {
                dataItem = factoryCreate ( id );
            }
            return dataItem;
        }
    }

    private static final String DATA_ITEM_OBJECT_TYPE = "ITEM";

    @Override
    public NotifyFuture<WriteAttributeResults> startWriteAttributes ( final Session session, final String itemId, final Map<String, Variant> attributes, final OperationParameters operationParameters ) throws InvalidSessionException, InvalidItemException, PermissionDeniedException
    {
        final SessionCommon sessionCommon = validateSession ( session );

        final OperationParameters effectiveOperationParameters = makeOperationParameters ( sessionCommon, operationParameters );

        final AuthorizationResult result = authorize ( DATA_ITEM_OBJECT_TYPE, itemId, "WRITE_ATTRIBUTES", effectiveOperationParameters.getUserInformation (), makeSetAttributesContext ( attributes ) );
        if ( !result.isGranted () )
        {
            logger.info ( "Write request was rejected: {}", result );
            throw new PermissionDeniedException ( result );
        }

        final DataItem item = retrieveItem ( itemId );

        if ( item == null )
        {
            throw new InvalidItemException ( itemId );
        }

        // stats
        if ( this.statisticsGenerator != null )
        {
            this.statisticsGenerator.startWriteAttributes ( session, itemId, attributes.size () );
        }

        // go
        final NotifyFuture<WriteAttributeResults> future = item.startSetAttributes ( attributes, effectiveOperationParameters );
        sessionCommon.addFuture ( future );

        return future;
    }

    private Map<String, Object> makeSetAttributesContext ( final Map<String, Variant> attributes )
    {
        final Map<String, Object> context = new HashMap<String, Object> ( 1 );
        context.put ( "attributes", attributes );
        return context;
    }

    private Map<String, Object> makeWriteValueContext ( final Variant value )
    {
        final Map<String, Object> context = new HashMap<String, Object> ( 1 );
        context.put ( "value", value );
        return context;
    }

    private OperationParameters makeOperationParameters ( final SessionCommon session, final OperationParameters operationParameters ) throws PermissionDeniedException
    {
        UserInformation sessionInformation = session.getUserInformation ();
        if ( sessionInformation == null )
        {
            logger.debug ( "Session has no user information. Using anonymous" );
            sessionInformation = UserInformation.ANONYMOUS;
        }

        if ( operationParameters == null )
        {
            logger.debug ( "No operation parameters provided. Creating default for user ({}).", sessionInformation );
            return new OperationParameters ( sessionInformation );
        }

        final UserInformation userInformation;

        if ( operationParameters.getUserInformation () != null && operationParameters.getUserInformation ().getName () != null )
        {
            final String proxyUser = operationParameters.getUserInformation ().getName ();

            // check if user differs
            if ( !proxyUser.equals ( sessionInformation.getName () ) )
            {
                logger.debug ( "Trying to set proxy user: {}", proxyUser );

                // try to set proxy user
                final AuthorizationResult result = authorize ( "SESSION", proxyUser, "PROXY_USER", session.getUserInformation (), null );
                if ( !result.isGranted () )
                {
                    logger.info ( "Proxy user is not allowed" );
                    // not allowed to use proxy user
                    throw new PermissionDeniedException ( result.getErrorCode (), result.getMessage () );
                }

                userInformation = new UserInformation ( operationParameters.getUserInformation ().getName (), operationParameters.getUserInformation ().getPassword (), sessionInformation.getRoles () );

            }
            else
            {
                logger.debug ( "Session user and proxy user match ... using session user" );
                // session is already is proxy user
                userInformation = sessionInformation;
            }
        }
        else
        {
            userInformation = sessionInformation;
        }

        return new OperationParameters ( userInformation );
    }

    @Override
    public NotifyFuture<WriteResult> startWrite ( final Session session, final String itemId, final Variant value, final OperationParameters operationParameters ) throws InvalidSessionException, InvalidItemException, PermissionDeniedException
    {
        final SessionCommon sessionCommon = validateSession ( session );

        final OperationParameters effectiveOperationParameters = makeOperationParameters ( sessionCommon, operationParameters );

        final AuthorizationResult result = authorize ( DATA_ITEM_OBJECT_TYPE, itemId, "WRITE", effectiveOperationParameters.getUserInformation (), makeWriteValueContext ( value ) );
        if ( !result.isGranted () )
        {
            logger.info ( "Write request was rejected: {}", result );
            throw new PermissionDeniedException ( result );
        }

        logger.debug ( "Processing write - granted - itemId: {}", itemId );

        final DataItem item = retrieveItem ( itemId );

        if ( item == null )
        {
            throw new InvalidItemException ( itemId );
        }

        // stats
        if ( this.statisticsGenerator != null )
        {
            this.statisticsGenerator.startWrite ( session, itemId, value );
        }

        // go
        final NotifyFuture<WriteResult> future = item.startWriteValue ( value, effectiveOperationParameters );
        sessionCommon.addFuture ( future );
        return future;
    }

    @Override
    public synchronized HiveBrowser getBrowser ()
    {
        if ( this.browser == null )
        {
            if ( this.rootFolder != null )
            {
                this.browser = new HiveBrowserCommon ( this ) {

                    @Override
                    public Folder getRootFolder ()
                    {
                        return HiveCommon.this.rootFolder;
                    }
                };
                this.browser.start ();
            }
        }

        return this.browser;
    }

    /* (non-Javadoc)
     * @see org.openscada.da.server.common.impl.ConfigurableHive#addItemFactory(org.openscada.da.server.common.DataItemFactory)
     */
    @Override
    public void addItemFactory ( final DataItemFactory factory )
    {
        this.factoryList.add ( factory );
    }

    public void removeItemFactory ( final DataItemFactory factory )
    {
        this.factoryList.remove ( factory );
    }

    public void addItemFactoryListener ( final DataItemFactoryListener listener )
    {
        this.factoryListeners.add ( listener );
    }

    public void removeItemFactoryListener ( final DataItemFactoryListener listener )
    {
        this.factoryListeners.remove ( listener );
    }

    private void fireDataItemCreated ( final DataItem dataItem )
    {
        for ( final DataItemFactoryListener listener : this.factoryListeners )
        {
            listener.created ( dataItem );
        }
    }

    @Override
    public void registerTemplate ( final FactoryTemplate template )
    {
        synchronized ( this.templates )
        {
            this.templates.add ( template );
        }
    }

    /**
     * Will re-check all items in granted state. Call when your list of known items
     * has changed in order to give granted but not connected subscriptions a chance
     * to be created by the factories.
     */
    public void recheckGrantedItems ()
    {
        final List<Object> topics = this.itemSubscriptionManager.getAllGrantedTopics ();

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
        final List<Object> topics = this.itemSubscriptionManager.getAllGrantedTopics ();

        final Set<String> items = new HashSet<String> ();

        for ( final Object topic : topics )
        {
            items.add ( topic.toString () );
        }
        return items;
    }

    public void addDataItemValidator ( final DataItemValidator dataItemValidator )
    {
        this.itemValidators.add ( dataItemValidator );
    }

    public void removeItemValidator ( final DataItemValidator dataItemValidator )
    {
        this.itemValidators.remove ( dataItemValidator );
    }

    protected ValidationStrategy getValidationStrategy ()
    {
        return this.validationStrategy;
    }

    protected void setValidatonStrategy ( final ValidationStrategy validatonStrategy )
    {
        this.validationStrategy = validatonStrategy;
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
    @Override
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
    @Override
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
    @Override
    public HiveService getService ( final String serviceName )
    {
        return this.services.get ( serviceName );
    }
}
