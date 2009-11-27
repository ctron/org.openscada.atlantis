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

package org.openscada.core.subscription;


public class SubscriptionStateEvent
{
    private SubscriptionState _state = null;

    public SubscriptionStateEvent ( final SubscriptionState subscriptionState )
    {
        this._state = subscriptionState;
    }

    public SubscriptionState getState ()
    {
        return this._state;
    }

    public void setState ( final SubscriptionState status )
    {
        this._state = status;
    }

    @Override
    public int hashCode ()
    {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + ( this._state == null ? 0 : this._state.hashCode () );
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
        final SubscriptionStateEvent other = (SubscriptionStateEvent)obj;
        if ( this._state == null )
        {
            if ( other._state != null )
            {
                return false;
            }
        }
        else if ( !this._state.equals ( other._state ) )
        {
            return false;
        }
        return true;
    }
}
