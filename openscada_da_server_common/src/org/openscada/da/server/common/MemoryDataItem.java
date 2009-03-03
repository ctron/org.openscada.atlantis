/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2007 inavare GmbH (http://inavare.com)
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

package org.openscada.da.server.common;

import java.util.EnumSet;
import java.util.Map;

import org.openscada.core.InvalidOperationException;
import org.openscada.core.NotConvertableException;
import org.openscada.core.NullValueException;
import org.openscada.core.Variant;
import org.openscada.da.core.IODirection;
import org.openscada.da.core.WriteAttributeResult;
import org.openscada.da.core.WriteAttributeResults;

public class MemoryDataItem extends DataItemBase
{
    private Variant _value = new Variant ();

    private AttributeManager _attributes = null;

    public MemoryDataItem ( final String name )
    {
        this ( name, EnumSet.of ( IODirection.INPUT, IODirection.OUTPUT ) );
    }

    protected MemoryDataItem ( final String name, final EnumSet<IODirection> ioDirection )
    {
        super ( new DataItemInformationBase ( name, ioDirection ) );
        this._attributes = new AttributeManager ( this );
    }

    public Variant readValue () throws InvalidOperationException
    {
        return new Variant ( this._value );
    }

    public void writeValue ( final Variant value ) throws InvalidOperationException, NullValueException, NotConvertableException
    {
        if ( !this._value.equals ( value ) )
        {
            this._value = new Variant ( value );
            notifyData ( value, null );
        }
    }

    public Map<String, Variant> getAttributes ()
    {
        return this._attributes.get ();
    }

    @Override
    protected Map<String, Variant> getCacheAttributes ()
    {
        return this._attributes.get ();
    }

    @Override
    protected Variant getCacheValue ()
    {
        return this._value;
    }

    public WriteAttributeResults setAttributes ( final Map<String, Variant> attributes )
    {
        final WriteAttributeResults writeAttributeResults = new WriteAttributeResults ();

        this._attributes.update ( null, attributes );

        for ( final Map.Entry<String, Variant> entry : attributes.entrySet () )
        {
            writeAttributeResults.put ( entry.getKey (), new WriteAttributeResult () );
        }

        return writeAttributeResults;
    }

}
