/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2008 inavare GmbH (http://inavare.com)
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

package org.openscada.da.server.common.chain;

import java.util.EnumSet;

import org.openscada.da.core.IODirection;

public class ChainProcessEntry
{
    private EnumSet<IODirection> _when = EnumSet.noneOf ( IODirection.class );

    private ChainItem _what = null;

    public ChainProcessEntry ( final EnumSet<IODirection> when, final ChainItem what )
    {
        super ();
        this._when = when;
        this._what = what;
    }

    public ChainProcessEntry ()
    {
        super ();
    }

    public ChainItem getWhat ()
    {
        return this._what;
    }

    public void setWhat ( final ChainItem what )
    {
        this._what = what;
    }

    public EnumSet<IODirection> getWhen ()
    {
        return this._when;
    }

    public void setWhen ( final EnumSet<IODirection> when )
    {
        this._when = when;
    }

    @Override
    public int hashCode ()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ( this._what == null ? 0 : this._what.hashCode () );
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
        final ChainProcessEntry other = (ChainProcessEntry)obj;
        if ( this._what == null )
        {
            if ( other._what != null )
            {
                return false;
            }
        }
        else if ( !this._what.equals ( other._what ) )
        {
            return false;
        }
        return true;
    }
}
