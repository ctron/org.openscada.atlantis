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

/**
 * A subscription information object which holds the information that where used
 * when the listener binds to the subscription.
 * 
 * Two subcsription information objects are equal if their listeners are equal.
 * @author Jens Reimann
 *
 */
public class SubscriptionInformation
{
    private SubscriptionListener _listener = null;

    private Object _hint = null;

    public SubscriptionInformation ()
    {
        super ();
    }

    public SubscriptionInformation ( final SubscriptionListener listener, final Object hint )
    {
        this._listener = listener;
        this._hint = hint;
    }

    public Object getHint ()
    {
        return this._hint;
    }

    public void setHint ( final Object hint )
    {
        this._hint = hint;
    }

    public SubscriptionListener getListener ()
    {
        return this._listener;
    }

    public void setListener ( final SubscriptionListener listener )
    {
        this._listener = listener;
    }

    @Override
    public int hashCode ()
    {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + ( this._listener == null ? 0 : this._listener.hashCode () );
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
        final SubscriptionInformation other = (SubscriptionInformation)obj;
        if ( this._listener == null )
        {
            if ( other._listener != null )
            {
                return false;
            }
        }
        else if ( !this._listener.equals ( other._listener ) )
        {
            return false;
        }
        return true;
    }
}
