/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2012 TH4 SYSTEMS GmbH (http://th4-systems.com)
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

package org.eclipse.scada.core.subscription;

import org.eclipse.scada.core.subscription.SubscriptionSource;

public class SubscriptionSourceEvent
{
    private Boolean _added = null;

    private SubscriptionSource _source = null;

    public SubscriptionSourceEvent ( final boolean added, final SubscriptionSource source )
    {
        super ();
        this._added = added;
        this._source = source;
    }

    public Boolean getAdded ()
    {
        return this._added;
    }

    public void setAdded ( final Boolean added )
    {
        this._added = added;
    }

    public SubscriptionSource getSource ()
    {
        return this._source;
    }

    public void setSource ( final SubscriptionSource source )
    {
        this._source = source;
    }

    @Override
    public int hashCode ()
    {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + ( this._added == null ? 0 : this._added.hashCode () );
        result = PRIME * result + ( this._source == null ? 0 : this._source.hashCode () );
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
        final SubscriptionSourceEvent other = (SubscriptionSourceEvent)obj;
        if ( this._added == null )
        {
            if ( other._added != null )
            {
                return false;
            }
        }
        else if ( !this._added.equals ( other._added ) )
        {
            return false;
        }
        if ( this._source == null )
        {
            if ( other._source != null )
            {
                return false;
            }
        }
        else if ( !this._source.equals ( other._source ) )
        {
            return false;
        }
        return true;
    }
}
