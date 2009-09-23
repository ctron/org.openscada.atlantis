/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2008-2009 inavare GmbH (http://inavare.com)
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

package org.openscada.hd;

import org.openscada.utils.lang.Immutable;

@Immutable
public class Value
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
}
