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

import java.io.Serializable;
import java.util.Calendar;

import org.openscada.utils.lang.Immutable;

@Immutable
public final class QueryParameters implements Serializable
{
    private static final long serialVersionUID = -7843048077890250710L;

    private final Calendar startTimestamp;

    private final Calendar endTimestamp;

    private final int numberOfEntries;

    public QueryParameters ( final Calendar startTimestamp, final Calendar endTimestamp, final int numberOfEntries )
    {
        if ( startTimestamp == null )
        {
            throw new NullPointerException ( "'startTimestamp' must not be null" );
        }

        if ( endTimestamp == null )
        {
            throw new NullPointerException ( "'endTimestamp' must not be null" );
        }

        if ( numberOfEntries < 0 )
        {
            throw new IllegalArgumentException ( "'numberOfEntries' must be greater than or equal to zero" );
        }

        this.startTimestamp = startTimestamp;
        this.endTimestamp = endTimestamp;
        this.numberOfEntries = numberOfEntries;
    }

    public Calendar getStartTimestamp ()
    {
        return this.startTimestamp;
    }

    public Calendar getEndTimestamp ()
    {
        return this.endTimestamp;
    }

    public int getEntries ()
    {
        return this.numberOfEntries;
    }

    @Override
    public String toString ()
    {
        return String.format ( "%1$tF-%1$tT.%1$tL -> %2$tF-%2$tT.%2$tL (%3$s)", this.startTimestamp, this.endTimestamp, this.numberOfEntries );
    }

    @Override
    public int hashCode ()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ( this.endTimestamp == null ? 0 : this.endTimestamp.hashCode () );
        result = prime * result + this.numberOfEntries;
        result = prime * result + ( this.startTimestamp == null ? 0 : this.startTimestamp.hashCode () );
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
        final QueryParameters other = (QueryParameters)obj;
        if ( this.endTimestamp == null )
        {
            if ( other.endTimestamp != null )
            {
                return false;
            }
        }
        else if ( !this.endTimestamp.equals ( other.endTimestamp ) )
        {
            return false;
        }
        if ( this.numberOfEntries != other.numberOfEntries )
        {
            return false;
        }
        if ( this.startTimestamp == null )
        {
            if ( other.startTimestamp != null )
            {
                return false;
            }
        }
        else if ( !this.startTimestamp.equals ( other.startTimestamp ) )
        {
            return false;
        }
        return true;
    }
}
