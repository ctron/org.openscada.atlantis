/*
 * This file is part of the openSCADA project
 * 
 * Copyright (C) 2011-2012 TH4 SYSTEMS GmbH (http://th4-systems.com)
 * Copyright (C) 2013 Jens Reimann (ctron@dentrassi.de)
 *
 * openSCADA is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 3
 * only, as published by the Free Software Foundation.
 *
 * openSCADA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License version 3 for more details
 * (a copy is included in the LICENSE file that accompanied this code).
 *
 * You should have received a copy of the GNU Lesser General Public License
 * version 3 along with openSCADA. If not, see
 * <http://opensource.org/licenses/lgpl-3.0.html> for a copy of the LGPLv3 License.
 */

package org.openscada.da.server.ngp;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.scada.core.Variant;

public class AttributesTransport
{
    private final Map<String, Variant> addedOrUpdated;

    private final Set<String> removed;

    public AttributesTransport ( final Map<String, Variant> attributes )
    {
        if ( attributes == null )
        {
            this.addedOrUpdated = Collections.emptyMap ();
            this.removed = Collections.emptySet ();
        }
        else
        {
            this.addedOrUpdated = new HashMap<String, Variant> ();
            this.removed = new HashSet<String> ();

            for ( final Map.Entry<String, Variant> entry : attributes.entrySet () )
            {
                if ( entry.getValue () == null )
                {
                    this.removed.add ( entry.getKey () );
                }
                else
                {
                    this.addedOrUpdated.put ( entry.getKey (), entry.getValue () );
                }
            }
        }
    }

    public Map<String, Variant> getAddedOrUpdated ()
    {
        return this.addedOrUpdated;
    }

    public Set<String> getRemoved ()
    {
        return this.removed;
    }
}