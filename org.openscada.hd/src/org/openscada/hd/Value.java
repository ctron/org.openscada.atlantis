/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2010 TH4 SYSTEMS GmbH (http://th4-systems.com)
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

package org.openscada.hd;

import org.openscada.utils.lang.Immutable;

@Immutable
public final class Value
{
    private final Long longValue;

    private final Double doubleValue;

    public Value ( final long value )
    {
        this.longValue = value;
        this.doubleValue = null;
    }

    public Value ( final double value )
    {
        this.doubleValue = value;
        this.longValue = null;
    }

    public long toLong ()
    {
        if ( this.longValue != null )
        {
            return this.longValue;
        }
        else
        {
            return this.doubleValue.longValue ();
        }
    }

    public double toDouble ()
    {
        if ( this.doubleValue != null )
        {
            return this.doubleValue;
        }
        else
        {
            return this.longValue.doubleValue ();
        }
    }

    public Number toNumber ()
    {
        if ( this.doubleValue != null )
        {
            return this.doubleValue;
        }
        else
        {
            return this.longValue;
        }
    }

    @Override
    public String toString ()
    {
        if ( this.doubleValue != null )
        {
            return String.format ( "%s", this.doubleValue );
        }
        else
        {
            return String.format ( "%s", this.longValue );
        }
    }

    @Override
    public int hashCode ()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ( this.doubleValue == null ? 0 : this.doubleValue.hashCode () );
        result = prime * result + ( this.longValue == null ? 0 : this.longValue.hashCode () );
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
        final Value other = (Value)obj;
        if ( this.doubleValue == null )
        {
            if ( other.doubleValue != null )
            {
                return false;
            }
        }
        else if ( !this.doubleValue.equals ( other.doubleValue ) )
        {
            return false;
        }
        if ( this.longValue == null )
        {
            if ( other.longValue != null )
            {
                return false;
            }
        }
        else if ( !this.longValue.equals ( other.longValue ) )
        {
            return false;
        }
        return true;
    }
}
