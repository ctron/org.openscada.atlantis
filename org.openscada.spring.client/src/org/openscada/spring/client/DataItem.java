/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2009 inavare GmbH (http://inavare.com)
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

package org.openscada.spring.client;

import java.util.HashMap;
import java.util.Map;

import org.openscada.core.Variant;

public class DataItem extends org.openscada.da.client.AsyncDataItem
{

    public DataItem ( final String itemId, final Connection connection )
    {
        super ( itemId, connection.getItemManager () );
    }

    public String getValueString ()
    {
        return getSnapshotValue ().toString ();
    }

    public String getSubscriptionStateString ()
    {
        return getSnapshotValue ().getSubscriptionState ().toString ();
    }

    public String getSubcriptionStringErrorString ()
    {
        return getSnapshotValue ().getSubscriptionErrorString ();
    }

    public Map<String, String> getAttributesString ()
    {
        final HashMap<String, String> result = new HashMap<String, String> ();

        final Map<String, Variant> attributes = new HashMap<String, Variant> ( getSnapshotValue ().getAttributes () );

        for ( final Map.Entry<String, Variant> entry : attributes.entrySet () )
        {
            result.put ( entry.getKey (), entry.getValue ().toString () );
        }

        return result;
    }
}
