/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2009 inavare GmbH (http://inavare.com)
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

package org.openscada.da.server.common.exporter;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyDescriptor;
import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.openscada.core.NotConvertableException;
import org.openscada.core.NullValueException;
import org.openscada.core.Variant;
import org.openscada.da.server.browser.common.FolderCommon;
import org.openscada.da.server.common.AttributeMode;
import org.openscada.da.server.common.DataItem;
import org.openscada.da.server.common.DataItemCommand;
import org.openscada.da.server.common.chain.DataItemInputChained;
import org.openscada.da.server.common.chain.WriteHandler;
import org.openscada.da.server.common.impl.HiveCommon;
import org.openscada.da.server.common.item.factory.FolderItemFactory;
import org.openscada.sec.UserInformation;
import org.openscada.utils.lang.Disposable;

public class ObjectExporter implements PropertyChangeListener, Disposable
{
    private static Logger logger = Logger.getLogger ( ObjectExporter.class );

    private final FolderItemFactory factory;

    private final String localId;

    private Object target;

    private boolean bound;

    private final Map<String, DataItem> items = new HashMap<String, DataItem> ();

    public ObjectExporter ( final String localId, final HiveCommon hive, final FolderCommon rootFolder )
    {
        this.localId = localId;
        this.factory = createItemFactory ( hive, rootFolder );
    }

    public ObjectExporter ( final String localId, final FolderItemFactory rootFactory )
    {
        this.localId = localId;
        this.factory = rootFactory.createSubFolderFactory ( localId );
    }

    public void dispose ()
    {
        this.factory.dispose ();
    }

    public String getLocalId ()
    {
        return this.localId;
    }

    protected FolderItemFactory createItemFactory ( final HiveCommon hive, final FolderCommon rootFolder )
    {
        return new FolderItemFactory ( hive, rootFolder, this.localId, this.localId );
    }

    /**
     * attach a new target. the old target will get detached.
     * @param target the new target
     */
    public synchronized void attachTarget ( final Object target )
    {
        detachTarget ();

        this.target = target;
        if ( target == null )
        {
            return;
        }

        final Method m = getEventMethod ( "addPropertyChangeListener" );
        try
        {
            m.invoke ( this.target, (PropertyChangeListener)this );
            this.bound = true;
        }
        catch ( final Throwable e )
        {
            logger.info ( "Failed to add property listener", e );
        }

        createDataItems ();
    }

    /**
     * create data items from the properties
     */
    private void createDataItems ()
    {
        try
        {
            final BeanInfo bi = Introspector.getBeanInfo ( this.target.getClass () );
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

    /**
     * read the initial value of the property
     * @param pd
     */
    private void initAttribute ( final PropertyDescriptor pd )
    {
        final Map<String, Variant> attributes = new HashMap<String, Variant> ();
        attributes.put ( "property.writeable", Variant.valueOf ( pd.getWriteMethod () != null ) );
        attributes.put ( "property.readable", Variant.valueOf ( pd.getReadMethod () != null ) );
        attributes.put ( "property.bound", Variant.valueOf ( pd.isBound () ) );
        attributes.put ( "property.expert", Variant.valueOf ( pd.isExpert () ) );
        attributes.put ( "property.constrained", Variant.valueOf ( pd.isConstrained () ) );
        attributes.put ( "property.label", new Variant ( pd.getDisplayName () ) );
        attributes.put ( "property.type", new Variant ( pd.getPropertyType ().getName () ) );
        attributes.put ( "description", new Variant ( pd.getShortDescription () ) );
        attributes.put ( "exporter.bound", Variant.valueOf ( this.bound ) );

        final Method m = pd.getReadMethod ();
        if ( m != null )
        {
            try
            {
                updateAttribute ( pd.getName (), m.invoke ( this.target ), null, attributes );
            }
            catch ( final Throwable e )
            {
                updateAttribute ( pd.getName (), null, e, attributes );
            }
        }
    }

    private DataItem createItem ( final PropertyDescriptor pd )
    {
        final boolean writeable = pd.getWriteMethod () != null;
        final boolean readable = pd.getReadMethod () != null;

        if ( writeable && readable )
        {
            return this.factory.createInputOutput ( pd.getName (), new WriteHandler () {

                public void handleWrite ( final UserInformation userInformation, final Variant value ) throws Exception
                {
                    ObjectExporter.this.writeAttribute ( pd, value );
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

                public void command ( final Variant value ) throws Exception
                {
                    ObjectExporter.this.writeAttribute ( pd, value );
                }
            } );
            return item;
        }
        return null;
    }

    protected void writeAttribute ( final PropertyDescriptor pd, final Variant value ) throws Exception
    {
        final Method m = pd.getWriteMethod ();
        if ( m == null )
        {
            throw new RuntimeException ( "Failed to write since write method cannot be found" );
        }

        final Object target = this.target;

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

    /**
     * detach from the current target if we have one
     */
    public synchronized void detachTarget ()
    {
        if ( this.target == null )
        {
            return;
        }

        if ( this.bound )
        {
            final Method m = getEventMethod ( "removePropertyChangeListener" );
            try
            {
                m.invoke ( this.target, (PropertyChangeListener)this );
            }
            catch ( final Throwable e )
            {
                logger.info ( "Failed to dispose listener", e );
            }
        }

        this.factory.disposeAllItems ();
        this.target = null;
        this.bound = false;
    }

    protected Method getEventMethod ( final String methodName )
    {
        try
        {
            return this.target.getClass ().getMethod ( methodName, PropertyChangeListener.class );
        }
        catch ( final SecurityException e )
        {
            logger.info ( "Failed to get add method", e );
            return null;
        }
        catch ( final NoSuchMethodException e )
        {
            logger.info ( "Failed to get add method", e );
            return null;
        }
    }

    public synchronized void propertyChange ( final PropertyChangeEvent evt )
    {
        updateAttribute ( evt.getPropertyName (), evt.getNewValue (), null, null );
    }

    private void updateAttribute ( final String propertyName, final Object newValue, final Throwable e, final Map<String, Variant> additionalAttributes )
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
                attributes.put ( "value.error.message", new Variant ( e.getMessage () ) );
            }
            else
            {
                attributes.put ( "value.error", null );
                attributes.put ( "value.error.message", null );
            }

            final DataItemInputChained inputItem = (DataItemInputChained)item;
            inputItem.updateData ( new Variant ( newValue ), attributes, AttributeMode.UPDATE );
        }
    }
}
