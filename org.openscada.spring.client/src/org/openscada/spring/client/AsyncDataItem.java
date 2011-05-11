/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2011 TH4 SYSTEMS GmbH (http://th4-systems.com)
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

package org.openscada.spring.client;

import java.util.HashMap;
import java.util.Map;

import org.openscada.core.Variant;

public class AsyncDataItem extends org.openscada.da.client.AsyncDataItem
{

    public AsyncDataItem ( final String itemId, final Connection connection )
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
