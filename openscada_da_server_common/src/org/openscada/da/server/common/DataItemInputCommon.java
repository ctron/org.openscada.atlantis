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

import java.util.Map;

import org.openscada.core.InvalidOperationException;
import org.openscada.core.Variant;
import org.openscada.da.core.WriteAttributeResults;

public class DataItemInputCommon extends DataItemInput
{
    private Variant _value = new Variant ();

    private AttributeManager _attributes = null;

    public DataItemInputCommon ( final String name )
    {
        super ( name );
        this._attributes = new AttributeManager ( this );
    }

    synchronized public Variant readValue () throws InvalidOperationException
    {
        return this._value;
    }

    public Map<String, Variant> getAttributes ()
    {
        return this._attributes.get ();
    }

    /**
     * Perform requests from the hive to update the items attributes
     * <br>
     * This method actually. Reacting to attribute set requests
     * is implementation dependent. So you will need to
     * subclass from DataItemInputCommon and override this
     * method.
     * <br>
     * If you simple need a memory container that simply stores
     * what you write into it consider using the MemoryDataItem.
     * <br>
     * If you are implementing a data item based on this item and
     * wish to change the data items attributes use {@link #getAttributeManager()}
     * to get the attribute manager which allows you so tweak the
     * items attributes from the side of the item implementation.
     */
    public WriteAttributeResults setAttributes ( final Map<String, Variant> attributes )
    {
        return WriteAttributesHelper.errorUnhandled ( null, attributes );
    }

    /**
     * Update the value of this data item
     * 
     * @param value the new value
     */
    synchronized public void updateData ( Variant value, final Map<String, Variant> attributes, final AttributeMode mode )
    {
        if ( !this._value.equals ( value ) )
        {
            this._value = new Variant ( value );
        }
        else
        {
            value = null;
        }

        this._attributes.update ( value, attributes, mode );
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
}
