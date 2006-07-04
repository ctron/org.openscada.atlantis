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

package org.openscada.da.client.test.impl;

import java.util.EnumSet;
import java.util.List;
import java.util.Map;

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.openscada.da.core.IODirection;
import org.openscada.da.core.data.Variant;

public class DataItemEntry extends BrowserEntry
{
    private String _id = null;
    private EnumSet<IODirection> _ioDirection = EnumSet.noneOf ( IODirection.class );
    
    private enum Properties
    {
        ITEM_ID,
        IO_DIRECTION
    }
    
    public DataItemEntry ( String name, Map<String, Variant> attributes, FolderEntry parent, HiveConnection connection, String id, EnumSet<IODirection> ioDirection )
    {
        super ( name, attributes, connection, parent );
        _id = id;
        _ioDirection = ioDirection;
    }

    public String getId ()
    {
        return _id;
    }
    
    @Override
    protected void fillPropertyDescriptors ( List<IPropertyDescriptor> list )
    {
        {
            PropertyDescriptor pd = new PropertyDescriptor ( Properties.ITEM_ID, "Item ID" );
            pd.setCategory ( "Data Item Info" );
            pd.setAlwaysIncompatible ( true );
            list.add ( pd );
        }
        {
            PropertyDescriptor pd = new PropertyDescriptor ( Properties.IO_DIRECTION, "IO Direction" );
            pd.setCategory ( "Data Item Info" );
            list.add ( pd );
        }
        super.fillPropertyDescriptors ( list );
    }
    
    @Override
    public Object getPropertyValue ( Object id )
    {
        if ( id.equals ( Properties.ITEM_ID ) )
            return _id;
        if ( id.equals ( Properties.IO_DIRECTION ))
            return _ioDirection;
        
        return super.getPropertyValue ( id );
    }

    public EnumSet<IODirection> getIoDirection ()
    {
        return _ioDirection;
    }
}
