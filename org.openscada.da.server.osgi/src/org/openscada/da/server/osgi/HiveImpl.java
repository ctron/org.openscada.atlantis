/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2008-2010 inavare GmbH (http://inavare.com)
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

package org.openscada.da.server.osgi;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.openscada.core.ConnectionInformation;
import org.openscada.core.Variant;
import org.openscada.da.server.browser.common.FolderCommon;
import org.openscada.da.server.browser.common.query.GroupFolder;
import org.openscada.da.server.browser.common.query.IDNameProvider;
import org.openscada.da.server.browser.common.query.InvisibleStorage;
import org.openscada.da.server.browser.common.query.ItemDescriptor;
import org.openscada.da.server.browser.common.query.SplitGroupProvider;
import org.openscada.da.server.common.DataItem;
import org.openscada.da.server.common.ValidationStrategy;
import org.openscada.da.server.common.impl.HiveCommon;
import org.openscada.sec.AuthenticationException;
import org.openscada.sec.AuthorizationResult;
import org.openscada.sec.UserInformation;
import org.openscada.sec.osgi.AuthenticationHelper;
import org.openscada.sec.osgi.AuthorizationHelper;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;

public class HiveImpl extends HiveCommon
{
    private final static Logger logger = Logger.getLogger ( HiveImpl.class );

    private FolderCommon rootFolder;

    private final BundleContext context;

    private final InvisibleStorage storage;

    private final Map<ServiceReference, ItemDescriptor> items;

    private final AuthenticationHelper authenticationManager;

    private final AuthorizationHelper authorizationManager;

    public HiveImpl ( final BundleContext context )
    {
        this.context = context;

        this.authenticationManager = new AuthenticationHelper ( context );
        this.authorizationManager = new AuthorizationHelper ( context );

        setValidatonStrategy ( ValidationStrategy.GRANT_ALL );

        setRootFolder ( this.rootFolder = new FolderCommon () );

        this.items = new HashMap<ServiceReference, ItemDescriptor> ();

        this.storage = new InvisibleStorage ();
        final GroupFolder allItemsFolder = new GroupFolder ( new SplitGroupProvider ( new IDNameProvider (), "\\.", 0, 1 ), new IDNameProvider () );
        this.rootFolder.add ( "all", allItemsFolder, new HashMap<String, Variant> () );
        this.storage.addChild ( allItemsFolder );
    }

    @Override
    public void start () throws Exception
    {
        this.authenticationManager.open ();
        this.authorizationManager.open ();
        super.start ();
    }

    @Override
    public void stop () throws Exception
    {
        super.stop ();
        this.authenticationManager.close ();
        this.authorizationManager.close ();
    }

    @Override
    protected UserInformation authenticate ( final Properties properties, final Properties sessionResultProperties ) throws AuthenticationException
    {
        final UserInformation result = this.authenticationManager.authenticate ( properties.getProperty ( ConnectionInformation.PROP_USER ), properties.getProperty ( ConnectionInformation.PROP_PASSWORD ) );

        authorizeSessionPriviliges ( properties, result, sessionResultProperties );

        return result;
    }

    private void authorizeSessionPriviliges ( final Properties properties, final UserInformation user, final Properties sessionResultProperties )
    {
        for ( final Map.Entry<Object, Object> entry : properties.entrySet () )
        {
            if ( entry.getKey () instanceof String && entry.getValue () instanceof String )
            {
                final String key = (String)entry.getKey ();
                final String value = (String)entry.getValue ();
                if ( key.startsWith ( "session.privilege." ) )
                {
                    final String priv = key.substring ( "session.privilege.".length () );
                    sessionResultProperties.put ( key, authenticateSessionPrivilege ( user, priv, value ) );
                }
            }
        }
    }

    private Object authenticateSessionPrivilege ( final UserInformation user, final String key, final String value )
    {
        final AuthorizationResult result = this.authorizationManager.authorize ( key, "SESSION", value, user, null );
        if ( result.isGranted () )
        {
            return true;
        }
        else
        {
            return result.getErrorCode ().toString ();
        }
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
            attributes.put ( "description", new Variant ( description ) );
        }
    }

    public synchronized void removeItem ( final DataItem item )
    {
        unregisterItem ( item );
        this.storage.removed ( new ItemDescriptor ( item, new HashMap<String, Variant> ( 0 ) ) );
    }

    public synchronized void addItem ( final ServiceReference serviceReference )
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
        logger.info ( String.format ( "Exporting %s as %s", serviceReference, item.getInformation ().getName () ) );
    }

    public synchronized void removeItem ( final ServiceReference serviceReference )
    {
        logger.info ( String.format ( "Removing %s", serviceReference ) );

        this.context.ungetService ( serviceReference );

        final ItemDescriptor descriptor = this.items.remove ( serviceReference );
        this.storage.removed ( descriptor );
        unregisterItem ( descriptor.getItem () );
    }
}
