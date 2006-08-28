/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006 inavare GmbH (http://inavare.com)
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

package org.openscada.da.core.common;

import java.util.Map;

import org.openscada.da.core.Variant;
import org.openscada.da.core.server.InvalidOperationException;
import org.openscada.da.core.server.WriteAttributesOperationListener.Results;

public class DataItemInputCommon extends DataItemInput
{
    private Variant _value = new Variant ();
    private AttributeManager _attributes = null;
    
	public DataItemInputCommon ( String name )
    {
		super ( name );
        _attributes = new AttributeManager ( this );
	}
	
	synchronized public Variant getValue () throws InvalidOperationException
    {
		return _value;
	}

	public Map<String, Variant> getAttributes ()
    {		
		return _attributes.get ();
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
    public Results setAttributes(Map<String, Variant> attributes)
    {
        return WriteAttributesHelper.errorUnhandled ( null, attributes );
    }
	
	/** Update the value of this data item
	 * 
	 * @param value the new value
	 */
	synchronized public void updateValue ( Variant value )
	{
		if ( !_value.equals ( value ) )
		{
			_value = new Variant(value);
			notifyValue ( value );
		}
	}
	
    public AttributeManager getAttributeManager ()
    {
        return _attributes;
    }
    
    @Override
    public void setListener ( ItemListener listener )
    {
        super.setListener ( listener );
        if ( listener != null )
        {
            if ( !_value.isNull () )
                notifyValue ( _value );
            if ( _attributes.get ().size () > 0 )
                notifyAttributes ( _attributes.get () );
        }
    }
}
