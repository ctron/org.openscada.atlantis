/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2012 TH4 SYSTEMS GmbH (http://th4-systems.com)
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

package org.openscada.da.server.common.osgi.factory;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.Executor;

import org.openscada.core.Variant;
import org.openscada.da.server.common.DataItem;
import org.openscada.da.server.common.chain.AttributeWriteHandler;
import org.openscada.da.server.common.chain.AttributeWriteHandlerItem;
import org.openscada.da.server.common.chain.DataItemInputChained;
import org.openscada.da.server.common.chain.WriteHandler;
import org.openscada.da.server.common.chain.WriteHandlerItem;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;

public class DataItemFactory
{
    private final BundleContext context;

    private final String globalId;

    private final Executor executor;

    private final Map<String, DataItem> items = new HashMap<String, DataItem> ();

    private final Map<String, ServiceRegistration<DataItem>> itemRegs = new HashMap<String, ServiceRegistration<DataItem>> ();

    private final String delimiter = ".";

    public DataItemFactory ( final BundleContext context, final Executor executor, final String globalId )
    {
        this.executor = executor;
        this.context = context;
        this.globalId = globalId;
    }

    public synchronized DataItemInputChained createInput ( final String localId, final Map<String, Variant> properties )
    {
        final Map<String, Variant> localProperties = fixProperties ( properties );

        final DataItem item = this.items.get ( localId );
        if ( item == null )
        {
            final String id = getId ( localId );
            final DataItemInputChained newItem = new DataItemInputChained ( id, this.executor );
            registerItem ( newItem, localId, localProperties );
            return newItem;
        }
        else
        {
            if ( item instanceof DataItemInputChained )
            {
                return (DataItemInputChained)item;
            }
            else
            {
                return null;
            }
        }
    }

    public synchronized WriteHandlerItem createOutput ( final String localId, final Map<String, Variant> properties, final WriteHandler writeHandler )
    {
        final Map<String, Variant> localProperties = fixProperties ( properties );

        final DataItem item = this.items.get ( localId );
        if ( item == null )
        {
            final String id = getId ( localId );
            final WriteHandlerItem newItem = new WriteHandlerItem ( id, writeHandler, this.executor );
            registerItem ( newItem, localId, localProperties );
            return newItem;
        }
        else
        {
            if ( item instanceof WriteHandlerItem )
            {
                return (WriteHandlerItem)item;
            }
            else
            {
                return null;
            }
        }
    }

    public synchronized AttributeWriteHandlerItem createOutput ( final String localId, final Map<String, Variant> properties, final AttributeWriteHandler writeHandler )
    {
        final Map<String, Variant> localProperties = fixProperties ( properties );

        final DataItem item = this.items.get ( localId );
        if ( item == null )
        {
            final String id = getId ( localId );
            final AttributeWriteHandlerItem newItem = new AttributeWriteHandlerItem ( id, writeHandler, this.executor );
            registerItem ( newItem, localId, localProperties );
            return newItem;
        }
        else
        {
            if ( item instanceof AttributeWriteHandlerItem )
            {
                return (AttributeWriteHandlerItem)item;
            }
            else
            {
                return null;
            }
        }
    }

    private Map<String, Variant> fixProperties ( final Map<String, Variant> properties )
    {
        final Map<String, Variant> localProperties;
        if ( properties != null )
        {
            localProperties = properties;
        }
        else
        {
            localProperties = new HashMap<String, Variant> ();
        }
        return localProperties;
    }

    protected void registerItem ( final DataItem newItem, final String localId, final Map<String, Variant> properties )
    {
        final Dictionary<String, String> props = new Hashtable<String, String> ();

        fillProperties ( properties, props );

        final ServiceRegistration<DataItem> handle = this.context.registerService ( DataItem.class, newItem, props );
        this.items.put ( localId, newItem );
        this.itemRegs.put ( localId, handle );
    }

    protected void fillProperties ( final Map<String, Variant> properties, final Dictionary<String, String> props )
    {
        final Variant description = properties.get ( "description" );
        if ( description != null )
        {
            final String str = description.asString ( null );
            if ( str != null )
            {
                props.put ( Constants.SERVICE_DESCRIPTION, str );
            }
        }
    }

    protected String getId ( final String localId )
    {
        return this.globalId + this.delimiter + localId;
    }

    public synchronized void dispose ()
    {
        for ( final ServiceRegistration<DataItem> reg : this.itemRegs.values () )
        {
            reg.unregister ();
        }
        this.items.clear ();
        this.itemRegs.clear ();
    }
}
