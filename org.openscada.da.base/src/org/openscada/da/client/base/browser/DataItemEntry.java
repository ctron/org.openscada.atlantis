/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006 inavare GmbH (http://inavare.com)
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package org.openscada.da.client.base.browser;

import java.util.EnumSet;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.openscada.core.Variant;
import org.openscada.da.client.base.item.Item;
import org.openscada.da.core.IODirection;

public class DataItemEntry extends BrowserEntry implements IAdaptable
{
    private String _id = null;

    private EnumSet<IODirection> _ioDirection = EnumSet.noneOf ( IODirection.class );

    private enum Properties
    {
        ITEM_ID,
        IO_DIRECTION
    }

    public DataItemEntry ( final String name, final Map<String, Variant> attributes, final FolderEntry parent, final HiveConnection connection, final String id, final EnumSet<IODirection> ioDirection )
    {
        super ( name, attributes, connection, parent );
        this._id = id;
        this._ioDirection = ioDirection;
    }

    public String getId ()
    {
        return this._id;
    }

    public String getAsSecondaryId ()
    {
        return this._id.replace ( "_", "__" ).replace ( ':', '_' ); //$NON-NLS-1$ //$NON-NLS-2$
    }

    @Override
    protected void fillPropertyDescriptors ( final List<IPropertyDescriptor> list )
    {
        {
            final PropertyDescriptor pd = new PropertyDescriptor ( Properties.ITEM_ID, Messages.getString ( "DataItemEntry.PropertyDescriptor.item.name" ) ); //$NON-NLS-1$
            pd.setCategory ( Messages.getString ( "DataItemEntry.PropertyDescriptor.item.category" ) ); //$NON-NLS-1$
            pd.setAlwaysIncompatible ( true );
            list.add ( pd );
        }
        {
            final PropertyDescriptor pd = new PropertyDescriptor ( Properties.IO_DIRECTION, Messages.getString ( "DataItemEntry.PropertyDescriptor.ioDirection.name" ) ); //$NON-NLS-1$
            pd.setCategory ( Messages.getString ( "DataItemEntry.PropertyDescriptor.item.category" ) ); //$NON-NLS-1$
            list.add ( pd );
        }
        super.fillPropertyDescriptors ( list );
    }

    @Override
    public Object getPropertyValue ( final Object id )
    {
        if ( id.equals ( Properties.ITEM_ID ) )
        {
            return this._id;
        }
        if ( id.equals ( Properties.IO_DIRECTION ) )
        {
            return this._ioDirection;
        }

        return super.getPropertyValue ( id );
    }

    public EnumSet<IODirection> getIoDirection ()
    {
        return this._ioDirection;
    }

    @SuppressWarnings ( "unchecked" )
    public Object getAdapter ( final Class adapter )
    {
        if ( adapter == Item.class )
        {
            return new Item ( this.getConnection ().getConnectionInformation ().toString (), this._id );
        }
        return null;
    }
}
