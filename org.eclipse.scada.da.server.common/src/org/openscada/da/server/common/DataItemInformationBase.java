/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2012 TH4 SYSTEMS GmbH (http://th4-systems.com)
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

import org.eclipse.scada.da.data.IODirection;
import org.eclipse.scada.utils.lang.Immutable;
import org.eclipse.scada.utils.str.StringHelper;
import org.openscada.da.core.DataItemInformation;

/**
 * Default implementation of data {@link DataItemInformation}
 * 
 * @author Jens Reimann
 */
@Immutable
public class DataItemInformationBase implements org.openscada.da.core.DataItemInformation
{
    private final String id;

    private final Set<IODirection> ioDirection;

    public DataItemInformationBase ( final String id, final Set<IODirection> ioDirection )
    {
        super ();
        this.id = id;
        this.ioDirection = EnumSet.copyOf ( ioDirection );
    }

    public DataItemInformationBase ( final String id )
    {
        super ();
        this.id = id;
        this.ioDirection = EnumSet.allOf ( IODirection.class );
    }

    public DataItemInformationBase ( final DataItemInformation information )
    {
        super ();

        this.id = information.getName ();
        this.ioDirection = EnumSet.copyOf ( information.getIODirection () );
    }

    @Override
    public Set<IODirection> getIODirection ()
    {
        return this.ioDirection;
    }

    @Override
    public String getName ()
    {
        return this.id;
    }

    @Override
    public int hashCode ()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ( this.id == null ? 0 : this.id.hashCode () );
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
        final DataItemInformationBase other = (DataItemInformationBase)obj;
        if ( this.id == null )
        {
            if ( other.id != null )
            {
                return false;
            }
        }
        else if ( !this.id.equals ( other.id ) )
        {
            return false;
        }
        return true;
    }

    @Override
    public String toString ()
    {
        return String.format ( "[%s, %s]", this.id, StringHelper.join ( this.ioDirection, ", " ) );
    }

}
