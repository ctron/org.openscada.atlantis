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

package org.openscada.da.base.browser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Observable;

import org.apache.log4j.Logger;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.openscada.core.Variant;

public class BrowserEntry extends Observable implements IPropertySource
{
    @SuppressWarnings ( "unused" )
    private static Logger _log = Logger.getLogger ( BrowserEntry.class );

    private String _name = null;

    private Map<String, Variant> _attributes = null;

    private HiveConnection _connection = null;

    private FolderEntry _parent = null;

    private enum Properties
    {
        NAME;
    }

    public BrowserEntry ( String name, Map<String, Variant> attributes, HiveConnection connection, FolderEntry parent )
    {
        _name = name;
        _connection = connection;
        _parent = parent;
        _attributes = attributes;
    }

    public String getName ()
    {
        return _name;
    }

    public FolderEntry getParent ()
    {
        return _parent;
    }

    public HiveConnection getConnection ()
    {
        return _connection;
    }

    public Map<String, Variant> getAttributes ()
    {
        return _attributes;
    }

    // IPropertySource

    public Object getEditableValue ()
    {
        return _name;
    }

    protected void fillPropertyDescriptors ( List<IPropertyDescriptor> list )
    {
        {
            PropertyDescriptor pd = new PropertyDescriptor ( Properties.NAME, Messages.getString ( "BrowserEntry.PropertyDescriptor.name.name" ) ); //$NON-NLS-1$
            pd.setCategory ( Messages.getString ( "BrowserEntry.PropertyDescriptor.entryInfo.category" ) ); //$NON-NLS-1$
            list.add ( pd );
        }

        for ( Map.Entry<String, Variant> entry : _attributes.entrySet () )
        {
            PropertyDescriptor pd = new PropertyDescriptor ( entry.getKey (), entry.getKey () );
            pd.setAlwaysIncompatible ( true );
            pd.setCategory ( Messages.getString ( "BrowserEntry.PropertyDescriptor.entryAttributes.category" ) ); //$NON-NLS-1$

            list.add ( pd );
        }
    }

    public IPropertyDescriptor[] getPropertyDescriptors ()
    {
        List<IPropertyDescriptor> list = new ArrayList<IPropertyDescriptor> ();

        fillPropertyDescriptors ( list );

        return list.toArray ( new IPropertyDescriptor[list.size ()] );
    }

    public Object getPropertyValue ( Object id )
    {
        if ( id.equals ( Properties.NAME ) )
            return _name;

        if ( ! ( id instanceof String ) )
            return null;

        String name = (String)id;

        return _attributes.get ( name ).asString ( null );
    }

    public boolean isPropertySet ( Object id )
    {
        return false;
    }

    public void resetPropertyValue ( Object id )
    {
        // no op
    }

    public void setPropertyValue ( Object id, Object value )
    {
        // no op
    }
}
