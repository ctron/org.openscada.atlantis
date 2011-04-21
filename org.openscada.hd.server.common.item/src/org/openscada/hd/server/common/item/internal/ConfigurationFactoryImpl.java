/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2011 TH4 SYSTEMS GmbH (http://th4-systems.com)
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

package org.openscada.hd.server.common.item.internal;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

import org.openscada.ca.ConfigurationFactory;
import org.openscada.core.Variant;
import org.openscada.da.datasource.DataSource;
import org.openscada.hd.server.common.HistoricalItem;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceRegistration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfigurationFactoryImpl implements ConfigurationFactory
{
    private final static Logger logger = LoggerFactory.getLogger ( ConfigurationFactoryImpl.class );

    protected final class ItemWrapper
    {
        private final HistoricalItemImpl item;

        private final ServiceRegistration registration;

        public ItemWrapper ( final HistoricalItemImpl item, final ServiceRegistration registration )
        {
            this.item = item;
            this.registration = registration;
        }

        public HistoricalItemImpl getItem ()
        {
            return this.item;
        }

        public ServiceRegistration getRegistration ()
        {
            return this.registration;
        }

        public void update ( final Map<String, String> properties ) throws Exception
        {
            this.item.update ( properties );
        }
    }

    private final Map<String, ItemWrapper> items = new HashMap<String, ItemWrapper> ();

    private final BundleContext context;

    public ConfigurationFactoryImpl ( final BundleContext context )
    {
        this.context = context;
    }

    public void dispose ()
    {
        final Set<ItemWrapper> items;
        synchronized ( this )
        {
            items = new HashSet<ItemWrapper> ( this.items.values () );
            this.items.clear ();
        }

        for ( final ItemWrapper item : items )
        {
            unregisterItem ( item );
        }
    }

    @Override
    public void delete ( final String configurationId ) throws Exception
    {
        final ItemWrapper item;
        synchronized ( this.items )
        {
            item = this.items.remove ( configurationId );
        }
        if ( item != null )
        {
            unregisterItem ( item );
        }
    }

    private void unregisterItem ( final ItemWrapper item )
    {
        item.getRegistration ().unregister ();
        item.getItem ().stop ();
    }

    @Override
    public void update ( final String configurationId, final Map<String, String> properties ) throws Exception
    {
        logger.info ( "Update call for {} -> {}", new Object[] { configurationId, properties } );

        synchronized ( this )
        {
            ItemWrapper item = this.items.get ( configurationId );
            if ( item == null )
            {
                logger.info ( "Creating new item: {}", configurationId );
                item = createItem ( configurationId, properties );
            }
            else
            {
                logger.info ( "Updating {}", configurationId );
                item.update ( properties );
            }
            this.items.put ( configurationId, item );
        }
    }

    private ItemWrapper createItem ( final String configurationId, final Map<String, String> properties ) throws InvalidSyntaxException
    {
        final String masterId = properties.get ( DataSource.DATA_SOURCE_ID );
        if ( masterId == null )
        {
            throw new IllegalArgumentException ( String.format ( "'%s' is not set", DataSource.DATA_SOURCE_ID ) );
        }

        final Dictionary<String, String> serviceProperties = new Hashtable<String, String> ();
        serviceProperties.put ( Constants.SERVICE_PID, configurationId );
        serviceProperties.put ( Constants.SERVICE_DESCRIPTION, "A historical item implementation" );
        serviceProperties.put ( Constants.SERVICE_VENDOR, "TH4 SYSTEMS GmbH" );

        final Map<String, Variant> attributes = new HashMap<String, Variant> ();
        attributes.put ( Constants.SERVICE_DESCRIPTION, Variant.valueOf ( "A historical item implementation" ) );
        attributes.put ( Constants.SERVICE_VENDOR, Variant.valueOf ( "TH4 SYSTEMS GmbH" ) );
        attributes.put ( Constants.SERVICE_PID, Variant.valueOf ( configurationId ) );
        attributes.put ( "master.id", Variant.valueOf ( masterId ) );

        final HistoricalItemImpl item = new HistoricalItemImpl ( configurationId, attributes, masterId, this.context );

        final ServiceRegistration registration = this.context.registerService ( HistoricalItem.class.getName (), item, serviceProperties );

        item.start ();

        return new ItemWrapper ( item, registration );
    }

}
