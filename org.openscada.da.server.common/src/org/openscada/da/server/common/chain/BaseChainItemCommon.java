/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2010 TH4 SYSTEMS GmbH (http://th4-systems.com)
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

package org.openscada.da.server.common.chain;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.openscada.core.Variant;
import org.openscada.da.core.WriteAttributeResult;
import org.openscada.da.core.WriteAttributeResults;
import org.openscada.da.server.common.DataItem;
import org.openscada.da.server.common.HiveService;
import org.openscada.da.server.common.HiveServiceRegistry;
import org.openscada.da.server.common.chain.storage.ChainStorageService;

public abstract class BaseChainItemCommon implements ChainItem
{
    private static Logger log = Logger.getLogger ( BaseChainItemCommon.class );

    protected HiveServiceRegistry serviceRegistry;

    protected String itemId;

    private final Set<String> reservedAttributes = new HashSet<String> ();

    private final Map<String, AttributeBinder> binders = new HashMap<String, AttributeBinder> ();

    public BaseChainItemCommon ( final HiveServiceRegistry serviceRegistry )
    {
        this.serviceRegistry = serviceRegistry;
    }

    /**
     * called when the chain item is assigned to a new data item
     */
    public void dataItemChanged ( final DataItem item )
    {
        this.itemId = item.getInformation ().getName ();
        loadInitialProperties ();
    }

    protected void loadInitialProperties ()
    {
        if ( !isPersistent () )
        {
            return;
        }

        final Set<String> values = new HashSet<String> ();
        for ( final String binderName : this.binders.keySet () )
        {
            values.add ( binderName );
        }

        for ( final Map.Entry<String, Variant> entry : loadStoredValues ( values ).entrySet () )
        {
            final AttributeBinder binder = this.binders.get ( entry.getKey () );
            if ( binder != null )
            {
                try
                {
                    binder.bind ( entry.getValue () );
                }
                catch ( final Exception e )
                {
                    log.error ( String.format ( "Failed to apply binder value to %s item %s, value %s", entry.getKey (), this.itemId, entry.getValue () ) );
                }
            }
        }
    }

    public WriteAttributeResults setAttributes ( final Map<String, Variant> attributes )
    {
        final WriteAttributeResults writeAttributeResults = new WriteAttributeResults ();

        for ( final Map.Entry<String, Variant> entry : attributes.entrySet () )
        {
            if ( this.reservedAttributes.contains ( entry.getKey () ) )
            {
                writeAttributeResults.put ( entry.getKey (), new WriteAttributeResult ( new Exception ( "Attribute may not be set" ) ) );
            }
            else if ( this.binders.containsKey ( entry.getKey () ) )
            {
                try
                {
                    this.binders.get ( entry.getKey () ).bind ( entry.getValue () );
                    writeAttributeResults.put ( entry.getKey (), WriteAttributeResult.OK );
                }
                catch ( final Throwable e )
                {
                    writeAttributeResults.put ( entry.getKey (), new WriteAttributeResult ( e ) );
                }
            }
        }

        writeBinders ();

        return writeAttributeResults;
    }

    protected void writeBinders ()
    {
        if ( !isPersistent () )
        {
            return;
        }

        if ( this.serviceRegistry == null )
        {
            return;
        }

        // get the service and check the type
        final HiveService service = this.serviceRegistry.getService ( ChainStorageService.SERVICE_ID );
        if ( ! ( service instanceof ChainStorageService ) )
        {
            return;
        }

        final ChainStorageService storageService = (ChainStorageService)service;
        final Map<String, Variant> attributes = new HashMap<String, Variant> ();
        performWriteBinders ( attributes );
        storageService.storeValues ( this.itemId, attributes );
    }

    /**
     * Perform the write operation
     * @param attributes add your attributes to this map to persist them
     */
    protected void performWriteBinders ( final Map<String, Variant> attributes )
    {
        addAttributes ( attributes );
    }

    public void setReservedAttributes ( final String... reservedAttributes )
    {
        this.reservedAttributes.addAll ( Arrays.asList ( reservedAttributes ) );
    }

    public void addBinder ( final String name, final AttributeBinder binder )
    {
        this.binders.put ( name, binder );

        if ( isPersistent () )
        {
            // apply the stored value if we found one
            final Variant storedValue = loadStoredValue ( name );
            if ( storedValue != null )
            {
                try
                {
                    binder.bind ( storedValue );
                }
                catch ( final Exception e )
                {
                    log.warn ( "Failed to set stored binder value" );
                }
            }
        }
    }

    private Variant loadStoredValue ( final String name )
    {
        final Set<String> values = new HashSet<String> ();
        values.add ( name );
        return loadStoredValues ( values ).get ( name );
    }

    protected Map<String, Variant> loadStoredValues ( final Set<String> values )
    {
        if ( this.serviceRegistry == null )
        {
            return new HashMap<String, Variant> ();
        }

        if ( this.itemId == null )
        {
            return new HashMap<String, Variant> ();
        }

        // get the service and check the type
        final HiveService service = this.serviceRegistry.getService ( ChainStorageService.SERVICE_ID );
        if ( ! ( service instanceof ChainStorageService ) )
        {
            return new HashMap<String, Variant> ();
        }

        final ChainStorageService storageService = (ChainStorageService)service;

        // load an return the value
        return storageService.loadValues ( this.itemId, values );
    }

    public void removeBinder ( final String name )
    {
        this.binders.remove ( name );
    }

    /**
     * Add the attributes from the value binders
     * @param attributes the map to which the attributes should be bound
     */
    public void addAttributes ( final Map<String, Variant> attributes )
    {
        for ( final Map.Entry<String, AttributeBinder> entry : this.binders.entrySet () )
        {
            attributes.put ( entry.getKey (), entry.getValue ().getAttributeValue () );
        }
    }

    public boolean isPersistent ()
    {
        return this.serviceRegistry != null;
    }
}
