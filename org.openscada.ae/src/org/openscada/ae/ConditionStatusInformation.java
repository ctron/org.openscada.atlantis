/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2008-2010 inavare GmbH (http://inavare.com)
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

package org.openscada.ae;

import java.util.Date;

import org.openscada.core.Variant;
import org.openscada.utils.lang.Immutable;

@Immutable
public class ConditionStatusInformation
{
    private final String id;

    private final ConditionStatus status;

    private final Date statusTimestamp;

    private final Variant value;

    private final String lastAknUser;

    private final Date lastAknTimestamp;

    public ConditionStatusInformation ( final String id, final ConditionStatus status, final Date statusTimestamp, final Variant value, final Date lastAknTimestamp, final String lastAknUser )
    {
        super ();

        this.id = id;
        this.status = status;
        this.statusTimestamp = statusTimestamp;
        this.value = value;
        this.lastAknTimestamp = lastAknTimestamp;
        this.lastAknUser = lastAknUser;

        if ( id == null )
        {
            throw new NullPointerException ( "'status' must not be null" );
        }
        if ( status == null )
        {
            throw new NullPointerException ( "'status' must not be null" );
        }
        if ( statusTimestamp == null )
        {
            throw new NullPointerException ( "'statusTimestamp' must not be null" );
        }
    }

    public String getId ()
    {
        return this.id;
    }

    public ConditionStatus getStatus ()
    {
        return this.status;
    }

    public Date getStatusTimestamp ()
    {
        return this.statusTimestamp;
    }

    public Variant getValue ()
    {
        return this.value;
    }

    public String getLastAknUser ()
    {
        return this.lastAknUser;
    }

    public Date getLastAknTimestamp ()
    {
        return this.lastAknTimestamp;
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
        if ( ! ( obj instanceof ConditionStatusInformation ) )
        {
            return false;
        }
        final ConditionStatusInformation other = (ConditionStatusInformation)obj;
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
        final StringBuilder sb = new StringBuilder ();

        sb.append ( this.id + "(" );
        sb.append ( "status=" + this.status );
        sb.append ( ")" );

        return sb.toString ();
    }

}
