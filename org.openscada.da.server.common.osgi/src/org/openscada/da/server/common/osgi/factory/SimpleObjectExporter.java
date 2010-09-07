/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2010 TH4 SYSTEMS GmbH (http://inavare.com)
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

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.HashMap;
import java.util.Map;

import org.openscada.core.Variant;
import org.openscada.da.server.common.AttributeMode;
import org.openscada.da.server.common.chain.DataItemInputChained;
import org.openscada.da.server.common.chain.item.ChainCreator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleObjectExporter<T>
{

    private final static Logger logger = LoggerFactory.getLogger ( SimpleObjectExporter.class );

    private final Class<? extends T> objectClass;

    private final DataItemFactory factory;

    private final String prefix;

    private final Map<String, DataItemInputChained> itemMap = new HashMap<String, DataItemInputChained> ();

    public SimpleObjectExporter ( final Class<? extends T> objectClass, final DataItemFactory factory, final String prefix )
    {
        this.objectClass = objectClass;
        this.factory = factory;
        this.prefix = prefix;

        createFields ();
        setValue ( null );
    }

    /**
     * Set the current value with default timestamp handling
     * @param value the new value to set
     */
    public void setValue ( final T value )
    {
        setValue ( value, false );
    }

    /**
     * Set the current value and apply the current timestamp to all values
     * <p>
     * All values will receive exactly the same timestamp
     * </p>
     * @param value the new value
     * @param forceTimestamp the flag whether to force a timestamp update for all values
     */
    public void setValue ( final T value, final boolean forceTimestamp )
    {
        final Long timestamp;
        if ( forceTimestamp )
        {
            timestamp = System.currentTimeMillis ();
        }
        else
        {
            timestamp = null;
        }

        try
        {
            final BeanInfo bi = Introspector.getBeanInfo ( this.objectClass );
            for ( final PropertyDescriptor pd : bi.getPropertyDescriptors () )
            {
                final DataItemInputChained item = this.itemMap.get ( pd.getName () );

                if ( value != null )
                {
                    try
                    {
                        final Object data = pd.getReadMethod ().invoke ( value );
                        setItemValue ( pd, item, data, timestamp );
                    }
                    catch ( final Exception e )
                    {
                        setItemError ( pd, item, e );
                    }
                }
                else
                {
                    setItemError ( pd, item, null );
                }

            }
        }
        catch ( final Exception e )
        {
            logger.warn ( "Failed to set value" );
            for ( final DataItemInputChained item : this.itemMap.values () )
            {
                setItemError ( null, item, e );
            }
        }
    }

    private void setItemError ( final PropertyDescriptor pd, final DataItemInputChained item, final Exception e )
    {
        final Map<String, Variant> attributes = new HashMap<String, Variant> ();

        if ( pd != null )
        {
            fillAttributes ( pd, attributes );
        }

        if ( e != null )
        {
            attributes.put ( "invocation.error", Variant.TRUE );
            attributes.put ( "invocation.error.message", new Variant ( e.getMessage () ) );
        }
        else
        {
            attributes.put ( "null.error", Variant.TRUE );
        }

        item.updateData ( Variant.NULL, attributes, AttributeMode.SET );
    }

    private void fillAttributes ( final PropertyDescriptor pd, final Map<String, Variant> attributes )
    {
        attributes.put ( "property.name", new Variant ( pd.getName () ) );
        attributes.put ( "property.type", new Variant ( pd.getPropertyType ().getName () ) );
    }

    private void setItemValue ( final PropertyDescriptor pd, final DataItemInputChained item, final Object data, final Long timestamp )
    {
        final Map<String, Variant> attributes = new HashMap<String, Variant> ();

        fillAttributes ( pd, attributes );

        if ( timestamp != null )
        {
            attributes.put ( "timestamp", Variant.valueOf ( timestamp ) );
        }

        item.updateData ( new Variant ( data ), attributes, AttributeMode.SET );
    }

    private void createFields ()
    {
        try
        {
            final BeanInfo bi = Introspector.getBeanInfo ( this.objectClass );
            for ( final PropertyDescriptor pd : bi.getPropertyDescriptors () )
            {
                if ( pd.getReadMethod () != null )
                {
                    createDataItem ( pd );
                }
            }
        }
        catch ( final Exception e )
        {
            logger.warn ( "Failed to create fields", e );
        }
    }

    private void createDataItem ( final PropertyDescriptor pd )
    {
        final String name = pd.getName ();

        final Map<String, Variant> properties = new HashMap<String, Variant> ();
        if ( pd.getShortDescription () != null )
        {
            properties.put ( "description", new Variant ( pd.getShortDescription () ) );
        }
        else
        {
            properties.put ( "description", new Variant ( "Field: " + pd.getName () ) );
        }

        final DataItemInputChained item = this.factory.createInput ( this.prefix + "." + name, properties );

        ChainCreator.applyDefaultInputChain ( item, null );

        this.itemMap.put ( name, item );
    }
}
