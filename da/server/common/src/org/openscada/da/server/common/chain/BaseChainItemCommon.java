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

    private Set<String> reservedAttributes = new HashSet<String> ();
    private Map<String, AttributeBinder> binders = new HashMap<String, AttributeBinder> ();

    public BaseChainItemCommon ( HiveServiceRegistry serviceRegistry )
    {
        this.serviceRegistry = serviceRegistry;
    }

    /**
     * called when the chain item is assigned to a new data item
     */
    public void dataItemChanged ( DataItem item )
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

        Set<String> values = new HashSet<String> ();
        for ( String binderName : this.binders.keySet () )
        {
            values.add ( binderName );
        }

        for ( Map.Entry<String, Variant> entry : loadStoredValues ( values ).entrySet () )
        {
            AttributeBinder binder = this.binders.get ( entry.getKey () );
            if ( binder != null )
            {
                try
                {
                    binder.bind ( entry.getValue () );
                }
                catch ( Exception e )
                {
                    log.error ( String.format ( "Failed to apply binder value to %s item %s, value %s",
                            entry.getKey (), this.itemId, entry.getValue () ) );
                }
            }
        }
    }

    public WriteAttributeResults setAttributes ( Map<String, Variant> attributes )
    {
        WriteAttributeResults writeAttributeResults = new WriteAttributeResults ();

        for ( Map.Entry<String, Variant> entry : attributes.entrySet () )
        {
            if ( this.reservedAttributes.contains ( entry.getKey () ) )
            {
                writeAttributeResults.put ( entry.getKey (), new WriteAttributeResult ( new Exception (
                        "Attribute may not be set" ) ) );
            }
            else if ( binders.containsKey ( entry.getKey () ) )
            {
                try
                {
                    binders.get ( entry.getKey () ).bind ( entry.getValue () );
                    writeAttributeResults.put ( entry.getKey (), new WriteAttributeResult () );
                }
                catch ( Exception e )
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

        if ( serviceRegistry == null )
        {
            return;
        }

        // get the service and check the type
        HiveService service = serviceRegistry.getService ( ChainStorageService.SERVICE_ID );
        if ( ! ( service instanceof ChainStorageService ) )
        {
            return;
        }

        ChainStorageService storageService = (ChainStorageService)service;
        Map<String, Variant> attributes = new HashMap<String, Variant> ();
        performWriteBinders ( attributes );
        storageService.storeValues ( this.itemId, attributes );
    }

    /**
     * Perform the write operation
     * @param attributes add your attributes to this map to persist them
     */
    protected void performWriteBinders ( Map<String, Variant> attributes )
    {
        addAttributes ( attributes );
    }

    public void setReservedAttributes ( String... reservedAttributes )
    {
        this.reservedAttributes.addAll ( Arrays.asList ( reservedAttributes ) );
    }

    public void addBinder ( String name, AttributeBinder binder )
    {
        binders.put ( name, binder );

        if ( isPersistent () )
        {
            // apply the stored value if we found one
            Variant storedValue = loadStoredValue ( name );
            if ( storedValue != null )
            {
                try
                {
                    binder.bind ( storedValue );
                }
                catch ( Exception e )
                {
                    log.warn ( "Failed to set stored binder value" );
                }
            }
        }
    }

    private Variant loadStoredValue ( String name )
    {
        Set<String> values = new HashSet<String> ();
        values.add ( name );
        return loadStoredValues ( values ).get ( name );
    }

    protected Map<String, Variant> loadStoredValues ( Set<String> values )
    {
        if ( serviceRegistry == null )
        {
            return new HashMap<String, Variant> ();
        }

        if ( this.itemId == null )
        {
            return new HashMap<String, Variant> ();
        }

        // get the service and check the type
        HiveService service = serviceRegistry.getService ( ChainStorageService.SERVICE_ID );
        if ( ! ( service instanceof ChainStorageService ) )
        {
            return new HashMap<String, Variant> ();
        }

        ChainStorageService storageService = (ChainStorageService)service;

        // load an return the value
        return storageService.loadValues ( this.itemId, values );
    }

    public void removeBinder ( String name )
    {
        binders.remove ( name );
    }

    /**
     * Add the attributes from the value binders
     * @param attributes the map to which the attributes should be bound
     */
    public void addAttributes ( Map<String, Variant> attributes )
    {
        for ( Map.Entry<String, AttributeBinder> entry : binders.entrySet () )
        {
            attributes.put ( entry.getKey (), entry.getValue ().getAttributeValue () );
        }
    }

    public boolean isPersistent ()
    {
        return serviceRegistry != null;
    }
}
