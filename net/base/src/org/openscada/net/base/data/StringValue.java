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

package org.openscada.net.base.data;


public class StringValue extends Value {

	private String _value = null;
	
	public StringValue ()
	{
	}
	
	public StringValue ( String value )
	{
        if ( value != null )
            _value = new String(value);
	}

	public String getValue()
    {
        if ( _value == null )
            return "";
        
		return new String(_value);
	}

	public void setValue ( String value )
    {
		_value = new String(value);
	}
	
	@Override
	public String toString()
    {
		if ( _value == null )
			return "";
		else
			return _value;
	}

    @Override
    public int hashCode ()
    {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + ( ( _value == null ) ? 0 : _value.hashCode () );
        return result;
    }

    @Override
    public boolean equals ( Object obj )
    {
        if ( this == obj )
            return true;
        if ( obj == null )
            return false;
        if ( getClass () != obj.getClass () )
            return false;
        final StringValue other = (StringValue)obj;
        if ( _value == null )
        {
            if ( other._value != null )
                return false;
        }
        else
            if ( !_value.equals ( other._value ) )
                return false;
        return true;
    }
}
