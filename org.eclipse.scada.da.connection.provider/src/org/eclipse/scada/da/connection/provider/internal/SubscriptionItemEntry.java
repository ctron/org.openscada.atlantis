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

package org.eclipse.scada.da.connection.provider.internal;

import org.eclipse.scada.da.client.ItemUpdateListener;

final class SubscriptionItemEntry
{
    private final String itemId;

    private final ItemUpdateListener listener;

    public SubscriptionItemEntry ( final String itemId, final ItemUpdateListener listener )
    {
        this.itemId = itemId;
        this.listener = listener;
    }

    @Override
    public int hashCode ()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ( this.itemId == null ? 0 : this.itemId.hashCode () );
        result = prime * result + ( this.listener == null ? 0 : this.listener.hashCode () );
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
        final SubscriptionItemEntry other = (SubscriptionItemEntry)obj;
        if ( this.itemId == null )
        {
            if ( other.itemId != null )
            {
                return false;
            }
        }
        else if ( !this.itemId.equals ( other.itemId ) )
        {
            return false;
        }
        if ( this.listener == null )
        {
            if ( other.listener != null )
            {
                return false;
            }
        }
        else if ( !this.listener.equals ( other.listener ) )
        {
            return false;
        }
        return true;
    }

}