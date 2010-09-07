/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2010 TH4 SYSTEMS GmbH (http://inavare.com)
 *
 * OpenSCADA is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 3
 * only, as published by the Free Software Foundation.
 *
 * OpenSCADA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License version 3 for more details
 * (a copy is included in the LICENSE file that accompanied this code).
 *
 * You should have received a copy of the GNU Lesser General Public License
 * version 3 along with OpenSCADA. If not, see
 * <http://opensource.org/licenses/lgpl-3.0.html> for a copy of the LGPLv3 License.
 */

package org.openscada.da.server.common;

import java.util.EnumSet;
import java.util.Set;

import org.openscada.da.core.DataItemInformation;
import org.openscada.da.core.IODirection;
import org.openscada.utils.lang.Immutable;

/**
 * Default implementation of data {@link DataItemInformation}
 * @author Jens Reimann
 *
 */
@Immutable
public class DataItemInformationBase implements org.openscada.da.core.DataItemInformation
{
    private final String id;

    private final Set<IODirection> ioDirection;

    public DataItemInformationBase ( final String name, final Set<IODirection> ioDirection )
    {
        super ();
        this.id = new String ( name );
        this.ioDirection = EnumSet.copyOf ( ioDirection );
    }

    public DataItemInformationBase ( final String id )
    {
        super ();
        this.id = new String ( id );
        this.ioDirection = EnumSet.allOf ( IODirection.class );
    }

    public DataItemInformationBase ( final DataItemInformation information )
    {
        super ();

        this.id = new String ( information.getName () );
        this.ioDirection = EnumSet.copyOf ( information.getIODirection () );
    }

    public Set<IODirection> getIODirection ()
    {
        return this.ioDirection;
    }

    public String getName ()
    {
        return this.id;
    }

    @Override
    public int hashCode ()
    {
        if ( this.id == null )
        {
            return "".hashCode ();
        }
        else
        {
            return this.id.hashCode ();
        }
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
        if ( ! ( obj instanceof DataItemInformation ) )
        {
            return false;
        }

        final DataItemInformation other = (DataItemInformation)obj;
        if ( this.id == null )
        {
            if ( other.getName () != null )
            {
                return false;
            }
        }
        else if ( !this.id.equals ( other.getName () ) )
        {
            return false;
        }
        return true;
    }

}
