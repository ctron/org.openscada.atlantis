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

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import org.openscada.da.core.IODirection;
import org.openscada.da.core.InvalidOperationException;
import org.openscada.da.core.WriteAttributesOperationListener.Result;
import org.openscada.da.core.WriteAttributesOperationListener.Results;
import org.openscada.da.core.data.NotConvertableException;
import org.openscada.da.core.data.NullValueException;
import org.openscada.da.core.data.Variant;

public class MemoryDataItem extends DataItemBase {

	public MemoryDataItem ( String name )
    {
		super ( new DataItemInformationBase ( name, EnumSet.of(IODirection.INPUT, IODirection.OUTPUT) ) );
        _attributes = new AttributeManager ( this );
	}

	private Variant _value = new Variant();
	private AttributeManager _attributes = null;
	
	public Variant getValue () throws InvalidOperationException
    {
		return new Variant ( _value );
	}

	public void setValue ( Variant value ) throws InvalidOperationException, NullValueException, NotConvertableException
    {
		if ( !_value.equals ( value ) )
		{
			_value = new Variant ( value );
			notifyValue ( value );
		}
	}

	public Map<String, Variant> getAttributes()
    {		
		return _attributes.get ();
	}

	public Results setAttributes ( Map<String, Variant> attributes )
    {
        Results results = new Results ();
        
        _attributes.update ( attributes );
        
        for ( Map.Entry<String, Variant> entry : attributes.entrySet () )
        {
            results.put ( entry.getKey (), new Result () );
        }
        
        return results;
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
