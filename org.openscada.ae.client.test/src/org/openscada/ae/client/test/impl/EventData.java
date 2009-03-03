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

package org.openscada.ae.client.test.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.openscada.ae.core.Event;
import org.openscada.core.Variant;

public class EventData implements IPropertySource
{
    private enum Properties
    {
        ID,
        TIMESTAMP;
    }

    private Event _event = null;

    private QueryDataModel _query = null;

    public EventData ( final Event event, final QueryDataModel query )
    {
        super ();
        this._event = event;
        this._query = query;
    }

    public Event getEvent ()
    {
        return this._event;
    }

    public QueryDataModel getQuery ()
    {
        return this._query;
    }

    @Override
    public int hashCode ()
    {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + ( this._event == null ? 0 : this._event.hashCode () );
        return result;
    }

    @Override
    public boolean equals ( final Object obj )
    {
        if ( this == obj )
        {
            return true;
        }
        if ( obj == null )
        {
            return false;
        }
        if ( getClass () != obj.getClass () )
        {
            return false;
        }
        final EventData other = (EventData)obj;
        if ( this._event == null )
        {
            if ( other._event != null )
            {
                return false;
            }
        }
        else if ( !this._event.equals ( other._event ) )
        {
            return false;
        }
        return true;
    }

    public Object getEditableValue ()
    {
        return this._event.getId ();
    }

    protected void fillPropertyDescriptors ( final List<IPropertyDescriptor> list )
    {
        {
            final PropertyDescriptor pd = new PropertyDescriptor ( Properties.ID, "ID" );
            pd.setCategory ( "Event Information" );
            list.add ( pd );
        }
        {
            final PropertyDescriptor pd = new PropertyDescriptor ( Properties.TIMESTAMP, "Timestamp" );
            pd.setCategory ( "Event Information" );
            list.add ( pd );
        }

        for ( final Map.Entry<String, Variant> entry : this._event.getAttributes ().entrySet () )
        {
            final PropertyDescriptor pd = new PropertyDescriptor ( entry.getKey (), entry.getKey () );
            pd.setAlwaysIncompatible ( true );
            pd.setCategory ( "Event Data" );

            list.add ( pd );
        }
    }

    public IPropertyDescriptor[] getPropertyDescriptors ()
    {
        final List<IPropertyDescriptor> list = new ArrayList<IPropertyDescriptor> ();
        fillPropertyDescriptors ( list );
        return list.toArray ( new IPropertyDescriptor[list.size ()] );
    }

    public Object getPropertyValue ( final Object id )
    {
        if ( id.equals ( Properties.ID ) )
        {
            return this._event.getId ();
        }

        if ( id.equals ( Properties.TIMESTAMP ) )
        {
            return String.format ( "%1$TF %1$TT %1$TN", this._event.getTimestamp () );
        }

        if ( ! ( id instanceof String ) )
        {
            return null;
        }

        final String name = (String)id;

        return this._event.getAttributes ().get ( name ).asString ( null );
    }

    public boolean isPropertySet ( final Object id )
    {
        return false;
    }

    public void resetPropertyValue ( final Object id )
    {
    }

    public void setPropertyValue ( final Object id, final Object value )
    {
    }

}
