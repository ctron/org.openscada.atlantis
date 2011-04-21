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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Map;

import org.openscada.core.Variant;
import org.openscada.da.server.browser.common.FolderCommon;
import org.openscada.da.server.common.impl.HiveCommon;
import org.openscada.da.server.common.item.factory.FolderItemFactory;
import org.openscada.utils.lang.Disposable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ObjectExporter extends AbstractObjectExporter implements PropertyChangeListener, Disposable
{
    private final static Logger logger = LoggerFactory.getLogger ( ObjectExporter.class );

    private Object target;

    private boolean bound;

    private Map<String, Variant> additionalAttributes;

    public ObjectExporter ( final String localId, final FolderItemFactory rootFactory )
    {
        super ( localId, rootFactory );
    }

    public ObjectExporter ( final String localId, final HiveCommon hive, final FolderCommon rootFolder )
    {
        super ( localId, hive, rootFolder );
    }

    public ObjectExporter ( final String localId, final FolderItemFactory rootFactory, final boolean readOnly )
    {
        this ( localId, rootFactory, readOnly, false );
    }

    public ObjectExporter ( final String localId, final FolderItemFactory rootFactory, final boolean readOnly, final boolean nullIsError )
    {
        super ( localId, rootFactory, readOnly, nullIsError );
    }

    public ObjectExporter ( final String localId, final HiveCommon hive, final FolderCommon rootFolder, final boolean readOnly )
    {
        this ( localId, hive, rootFolder, readOnly, false );
    }

    public ObjectExporter ( final String localId, final HiveCommon hive, final FolderCommon rootFolder, final boolean readOnly, final boolean nullIsError )
    {
        super ( localId, hive, rootFolder, readOnly, nullIsError );
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

        createDataItems ( target.getClass () );
    }

    public synchronized void setAttributes ( final Map<String, Variant> additionalAttributes )
    {
        if ( additionalAttributes != null )
        {
            this.additionalAttributes = Collections.unmodifiableMap ( additionalAttributes );
        }
        else
        {
            this.additionalAttributes = null;
        }

        updateItemsFromTarget ();
    }

    @Override
    protected Map<String, Variant> getAdditionalAttributes ()
    {
        return this.additionalAttributes;
    }

    @Override
    protected void fillAttributes ( final PropertyDescriptor pd, final Map<String, Variant> attributes )
    {
        super.fillAttributes ( pd, attributes );
        attributes.put ( "exporter.bound", Variant.valueOf ( this.bound ) );
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

        this.items.clear ();
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

    @Override
    public synchronized void propertyChange ( final PropertyChangeEvent evt )
    {
        updateAttribute ( evt.getPropertyName (), evt.getNewValue (), null, null );
    }

    @Override
    protected Object getTarget ()
    {
        return this.target;
    }

}
