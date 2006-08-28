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

import org.openscada.da.core.server.DataItemInformation;
import org.openscada.da.core.server.IODirection;

public class DataItemInformationBase implements
        org.openscada.da.core.server.DataItemInformation
{
    private String _name = "";
    private EnumSet<IODirection> _ioDirection = EnumSet.noneOf ( IODirection.class );
    
    public DataItemInformationBase ( String name, EnumSet<IODirection> ioDirection )
    {
        super ();
        _name = new String ( name );
        _ioDirection = ioDirection.clone ();
    }
    
    public DataItemInformationBase ( String name )
    {
        super ();
        _name = new String ( name );
    }
    
    public DataItemInformationBase ( DataItemInformation information )
    {
        super();
        
        _name = new String ( information.getName() );
        _ioDirection = information.getIODirection ().clone ();
    }

    public EnumSet<IODirection> getIODirection ()
    {
        return _ioDirection;
    }
    
    public String getName ()
    {
        return _name;
    }

    @Override
    public int hashCode ()
    {
        if ( _name == null )
            return "".hashCode ();
        else
            return _name.hashCode ();
    }

    @Override
    public boolean equals ( Object obj )
    {
        if ( this == obj )
            return true;
        if ( obj == null )
            return false;
        if ( !( obj instanceof DataItemInformation) )
            return false;
        
        final DataItemInformation other = (DataItemInformation)obj;
        if ( _name == null )
        {
            if ( other.getName() != null )
                return false;
        }
        else
            if ( !_name.equals ( other.getName() ) )
                return false;
        return true;
    }

}
