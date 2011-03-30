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

package org.openscada.da.server.common.exporter;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.openscada.core.NotConvertableException;
import org.openscada.core.NullValueException;
import org.openscada.core.Variant;
import org.openscada.da.core.OperationParameters;
import org.openscada.da.server.browser.common.FolderCommon;
import org.openscada.da.server.common.AttributeMode;
import org.openscada.da.server.common.DataItem;
import org.openscada.da.server.common.DataItemCommand;
import org.openscada.da.server.common.chain.DataItemInputChained;
import org.openscada.da.server.common.chain.WriteHandler;
import org.openscada.da.server.common.impl.HiveCommon;
import org.openscada.da.server.common.item.factory.FolderItemFactory;
import org.openscada.utils.lang.Disposable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractObjectExporter implements Disposable
{

    private final static Logger logger = LoggerFactory.getLogger ( AbstractObjectExporter.class );

    protected FolderItemFactory factory;

    protected final Map<String, DataItem> items = new HashMap<String, DataItem> ();

    public AbstractObjectExporter ( final String localId, final HiveCommon hive, final FolderCommon rootFolder )
    {
        this.factory = new FolderItemFactory ( hive, rootFolder, localId, localId );
    }

    public AbstractObjectExporter ( final String localId, final FolderItemFactory rootFactory )
    {
        this.factory = rootFactory.createSubFolderFactory ( localId );
    }

    @Override
    public void dispose ()
    {
        this.factory.dispose ();
    }

    /**
     * create data items from the properties
     */
    protected void createDataItems ( final Class<?> targetClazz )
    {
        try
        {
            final BeanInfo bi = Introspector.getBeanInfo ( targetClazz );
            for ( final PropertyDescriptor pd : bi.getPropertyDescriptors () )
            {
                final DataItem item = createItem ( pd );
                this.items.put ( pd.getName (), item );
                initAttribute ( pd );
            }
        }
        catch ( final IntrospectionException e )
        {
            logger.info ( "Failed to read initial item", e );
        }
    }

    protected void updateItemsFromTarget ()
    {
        final Set<String> updatedAttributes = new HashSet<String> ();
        try
        {
            final Object target = getTarget ();
            if ( target != null )
            {
                final BeanInfo bi = Introspector.getBeanInfo ( target.getClass () );
                for ( final PropertyDescriptor pd : bi.getPropertyDescriptors () )
                {
                    updatedAttributes.add ( pd.getName () );
                    initAttribute ( pd );
                }
            }
        }
        catch ( final IntrospectionException e )
        {
            logger.info ( "Failed to read item", e );
        }

        for ( final String key : this.items.keySet () )
        {
            if ( !updatedAttributes.contains ( key ) )
            {
                updateAttribute ( key, null, null, null );
            }
        }
    }

    /**
     * read the initial value of the property
     * @param pd
     */
    protected void initAttribute ( final PropertyDescriptor pd )
    {
        final Map<String, Variant> attributes = new HashMap<String, Variant> ();
        fillAttributes ( pd, attributes );

        final Object target = getTarget ();

        final Method m = pd.getReadMethod ();
        if ( m != null )
        {
            try
            {
                if ( target != null )
                {
                    updateAttribute ( pd.getName (), m.invoke ( target ), null, attributes );
                }
                else
                {
                    updateAttribute ( pd.getName (), null, null, attributes );
                }
            }
            catch ( final Throwable e )
            {
                updateAttribute ( pd.getName (), null, e, attributes );
            }
        }
    }

    protected void fillAttributes ( final PropertyDescriptor pd, final Map<String, Variant> attributes )
    {
        attributes.put ( "property.writeable", Variant.valueOf ( pd.getWriteMethod () != null ) );
        attributes.put ( "property.readable", Variant.valueOf ( pd.getReadMethod () != null ) );
        attributes.put ( "property.bound", Variant.valueOf ( pd.isBound () ) );
        attributes.put ( "property.expert", Variant.valueOf ( pd.isExpert () ) );
        attributes.put ( "property.constrained", Variant.valueOf ( pd.isConstrained () ) );
        attributes.put ( "property.label", Variant.valueOf ( pd.getDisplayName () ) );
        attributes.put ( "property.type", Variant.valueOf ( pd.getPropertyType ().getName () ) );
        attributes.put ( "description", Variant.valueOf ( pd.getShortDescription () ) );
    }

    private DataItem createItem ( final PropertyDescriptor pd )
    {
        final boolean writeable = pd.getWriteMethod () != null;
        final boolean readable = pd.getReadMethod () != null;

        if ( writeable && readable )
        {
            return this.factory.createInputOutput ( pd.getName (), new WriteHandler () {

                @Override
                public void handleWrite ( final Variant value, final OperationParameters operationParameters ) throws Exception
                {
                    writeAttribute ( pd, value );
                }
            } );
        }
        else if ( readable )
        {
            return this.factory.createInput ( pd.getName () );
        }
        else if ( writeable )
        {
            final DataItemCommand item = this.factory.createCommand ( pd.getName () );
            item.addListener ( new DataItemCommand.Listener () {

                @Override
                public void command ( final Variant value ) throws Exception
                {
                    writeAttribute ( pd, value );
                }
            } );
            return item;
        }
        return null;
    }

    protected void updateAttribute ( final String propertyName, final Object newValue, final Throwable e, final Map<String, Variant> additionalAttributes )
    {
        final DataItem item = this.items.get ( propertyName );
        if ( item == null )
        {
            return;
        }

        if ( item instanceof DataItemInputChained )
        {
            final Map<String, Variant> attributes = new HashMap<String, Variant> ();

            if ( additionalAttributes != null )
            {
                attributes.putAll ( additionalAttributes );
            }

            if ( e != null )
            {
                attributes.put ( "value.error", Variant.TRUE );
                attributes.put ( "value.error.message", Variant.valueOf ( e.getMessage () ) );
            }
            else
            {
                attributes.put ( "value.error", null );
                attributes.put ( "value.error.message", null );
            }

            final DataItemInputChained inputItem = (DataItemInputChained)item;
            inputItem.updateData ( Variant.valueOf ( newValue ), attributes, AttributeMode.UPDATE );
        }
    }

    /**
     * Get the current target or <code>null</code> if there is none
     * @return the current target
     */
    protected abstract Object getTarget ();

    protected void writeAttribute ( final PropertyDescriptor pd, final Variant value ) throws Exception
    {
        final Method m = pd.getWriteMethod ();
        if ( m == null )
        {
            throw new RuntimeException ( "Failed to write since write method cannot be found" );
        }

        final Object target = getTarget ();

        if ( target == null )
        {
            throw new RuntimeException ( "No current target attached" );
        }

        final Class<?> targetType = pd.getPropertyType ();
        final Object o = convertWriteType ( targetType, value );

        if ( o != null )
        {
            // try the direct approach
            m.invoke ( target, o );
        }
        else
        {
            // try a "by string" approach
            final PropertyEditor pe = PropertyEditorManager.findEditor ( targetType );
            pe.setAsText ( value.asString () );
        }

    }

    /**
     * Convert the value to the target type if possible.
     * @param targetType The expected target type
     * @param value the source value
     * @return an instance of the source value in the target type (if possible)
     * or <code>null</code> otherwise 
     * @throws NotConvertableException 
     * @throws NullValueException 
     */
    private Object convertWriteType ( final Class<?> targetType, final Variant value ) throws NullValueException, NotConvertableException
    {
        if ( targetType.isAssignableFrom ( Variant.class ) )
        {
            return value;
        }
        if ( value == null || value.isNull () )
        {
            return null;
        }

        if ( targetType.isAssignableFrom ( Long.class ) || targetType.isAssignableFrom ( long.class ) )
        {
            return value.asLong ();
        }
        if ( targetType.isAssignableFrom ( Integer.class ) || targetType.isAssignableFrom ( int.class ) )
        {
            return value.asInteger ();
        }
        if ( targetType.isAssignableFrom ( Double.class ) || targetType.isAssignableFrom ( double.class ) )
        {
            return value.asDouble ();
        }
        if ( targetType.isAssignableFrom ( Boolean.class ) || targetType.isAssignableFrom ( boolean.class ) )
        {
            return value.asBoolean ();
        }
        if ( targetType.isAssignableFrom ( String.class ) )
        {
            return value.asString ();
        }

        return null;
    }

}
