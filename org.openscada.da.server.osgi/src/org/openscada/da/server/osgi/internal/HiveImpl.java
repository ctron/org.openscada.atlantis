/*
 * This file is part of the OpenSCADA project
 * 
 * Copyright (C) 2006-2012 TH4 SYSTEMS GmbH (http://th4-systems.com)
 * Copyright (C) 2013 Jens Reimann (ctron@dentrassi.de)
 *
 * OpenSCADA is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 3
 * only, as published by the Free Software Foundation.
 *
 * OpenSCADA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License version 3 for more details
 * (a copy is included in the LICENSE file that accompanied this code).
 *
 * You should have received a copy of the GNU Lesser General Public License
 * version 3 along with OpenSCADA. If not, see
 * <http://opensource.org/licenses/lgpl-3.0.html> for a copy of the LGPLv3 License.
 */

package org.openscada.da.server.osgi.internal;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.Executor;

import org.openscada.ae.sec.AuthorizationHelper;
import org.openscada.core.ConnectionInformation;
import org.openscada.core.Variant;
import org.openscada.core.server.common.osgi.SessionPrivilegeTracker;
import org.openscada.core.server.common.session.AbstractSessionImpl.DisposeListener;
import org.openscada.core.server.common.session.PrivilegeListenerImpl;
import org.openscada.da.server.browser.common.FolderCommon;
import org.openscada.da.server.browser.common.query.GroupFolder;
import org.openscada.da.server.browser.common.query.IDNameProvider;
import org.openscada.da.server.browser.common.query.InvisibleStorage;
import org.openscada.da.server.browser.common.query.ItemDescriptor;
import org.openscada.da.server.browser.common.query.SplitGroupProvider;
import org.openscada.da.server.browser.common.query.SplitNameProvider;
import org.openscada.da.server.common.DataItem;
import org.openscada.da.server.common.ValidationStrategy;
import org.openscada.da.server.common.impl.HiveCommon;
import org.openscada.da.server.common.impl.SessionCommon;
import org.openscada.sec.AuthenticationException;
import org.openscada.sec.AuthorizationResult;
import org.openscada.sec.UserInformation;
import org.openscada.sec.osgi.AuthenticationHelper;
import org.openscada.sec.osgi.AuthorizationTracker;
import org.openscada.utils.collection.MapBuilder;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HiveImpl extends HiveCommon
{

    private final static Logger logger = LoggerFactory.getLogger ( HiveImpl.class );

    private FolderCommon rootFolder;

    private final BundleContext context;

    private final InvisibleStorage storage;

    private final Map<ServiceReference<?>, ItemDescriptor> items;

    private final AuthenticationHelper authenticationManager;

    private final AuthorizationHelper authorizationManager;

    private final AuthorizationTracker authorizationTracker;

    private final Executor executor;

    public HiveImpl ( final BundleContext context, final Executor executor ) throws InvalidSyntaxException
    {
        this.context = context;
        this.executor = executor;

        this.authenticationManager = new AuthenticationHelper ( context );
        this.authorizationManager = new AuthorizationHelper ( context );
        this.authorizationTracker = new AuthorizationTracker ( context, executor );

        setValidatonStrategy ( ValidationStrategy.GRANT_ALL );

        setRootFolder ( this.rootFolder = new FolderCommon () );

        this.items = new HashMap<ServiceReference<?>, ItemDescriptor> ();

        this.storage = new InvisibleStorage ();
        final GroupFolder allItemsFolder = new GroupFolder ( new SplitGroupProvider ( new IDNameProvider (), "\\.", 0, 2 ), new SplitNameProvider ( new IDNameProvider (), "\\.", 0, 2, "." ) );
        this.rootFolder.add ( "all", allItemsFolder, new MapBuilder<String, Variant> ().put ( "description", Variant.valueOf ( "A folder containing the full item space" ) ).getMap () );
        this.storage.addChild ( allItemsFolder );
    }

    @Override
    public String getHiveId ()
    {
        return "org.openscada.da.server.osgi";
    }

    @Override
    public void start () throws Exception
    {
        this.authenticationManager.open ();
        this.authorizationManager.open ();
        this.authorizationTracker.open ();
        super.start ();
    }

    @Override
    public void stop () throws Exception
    {
        super.stop ();
        this.authorizationTracker.close ();
        this.authenticationManager.close ();
        this.authorizationManager.close ();
    }

    @Override
    protected void handleSessionCreated ( final SessionCommon session, final Properties properties, final UserInformation userInformation )
    {
        super.handleSessionCreated ( session, properties, userInformation );

        final Set<String> privileges = extractPrivileges ( properties );
        final SessionPrivilegeTracker privTracker = new SessionPrivilegeTracker ( this.executor, new PrivilegeListenerImpl ( session ), this.authorizationTracker, privileges, userInformation );

        session.addDisposeListener ( new DisposeListener () {

            @Override
            public void disposed ()
            {
                privTracker.dispose ();
            }
        } );
    }

    @Override
    protected UserInformation authenticate ( final Properties properties, final Map<String, String> sessionResultProperties ) throws AuthenticationException
    {
        return this.authenticationManager.authenticate ( properties.getProperty ( ConnectionInformation.PROP_USER ), properties.getProperty ( ConnectionInformation.PROP_PASSWORD ) );
    }

    @Override
    protected AuthorizationResult authorize ( final String objectType, final String objectId, final String action, final UserInformation userInformation, final Map<String, Object> context, final AuthorizationResult defaultResult )
    {
        return this.authorizationManager.authorize ( objectType, objectId, action, userInformation, context, defaultResult );
    }

    public synchronized void addItem ( final DataItem item, final Dictionary<?, ?> properties )
    {
        registerItem ( item );

        final Map<String, Variant> attributes = new HashMap<String, Variant> ( 0 );
        fillAttributes ( attributes, properties );

        final ItemDescriptor descriptor = new ItemDescriptor ( item, attributes );

        this.storage.added ( descriptor );
    }

    private static void fillAttributes ( final Map<String, Variant> attributes, final Dictionary<?, ?> properties )
    {
        if ( properties == null )
        {
            return;
        }

        final Object description = properties.get ( Constants.SERVICE_DESCRIPTION );
        if ( description != null )
        {
            attributes.put ( "description", Variant.valueOf ( description ) );
        }
    }

    public synchronized void removeItem ( final DataItem item )
    {
        unregisterItem ( item );
        this.storage.removed ( new ItemDescriptor ( item, null ) );
    }

    public synchronized void addItem ( final ServiceReference<?> serviceReference )
    {
        if ( !serviceReference.isAssignableTo ( this.context.getBundle (), DataItem.class.getName () ) )
        {
            return;
        }
        final DataItem item = (DataItem)this.context.getService ( serviceReference );
        registerItem ( item );

        final Map<String, Variant> attributes = new HashMap<String, Variant> ( 0 );

        final ItemDescriptor descriptor = new ItemDescriptor ( item, attributes );
        this.storage.added ( descriptor );

        this.items.put ( serviceReference, descriptor );
        logger.info ( "Exporting {} as {}", serviceReference, item.getInformation ().getName () );
    }

    public synchronized void removeItem ( final ServiceReference<?> serviceReference )
    {
        logger.info ( "Removing {}", serviceReference );

        this.context.ungetService ( serviceReference );

        final ItemDescriptor descriptor = this.items.remove ( serviceReference );
        this.storage.removed ( descriptor );
        unregisterItem ( descriptor.getItem () );
    }
}
