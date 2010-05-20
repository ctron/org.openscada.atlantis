/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2010 inavare GmbH (http://inavare.com)
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

package org.openscada.da.master;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.openscada.core.Variant;
import org.openscada.da.datasource.WriteInformation;

public class WriteRequest
{
    private final WriteInformation writeInformation;

    private final Variant value;

    private final Map<String, Variant> attributes;

    public WriteRequest ( final WriteInformation writeInformation, final Variant value )
    {
        this ( writeInformation, value, null );
    }

    public WriteRequest ( final WriteInformation writeInformation, final Map<String, Variant> attributes )
    {
        this ( writeInformation, null, attributes );
    }

    public WriteRequest ( final WriteInformation writeInformation, final Variant value, final Map<String, Variant> attributes )
    {
        this.value = value;
        this.writeInformation = writeInformation;

        if ( attributes != null )
        {
            this.attributes = new HashMap<String, Variant> ( attributes );
        }
        else
        {
            this.attributes = new HashMap<String, Variant> ();
        }
    }

    public Map<String, Variant> getAttributes ()
    {
        return Collections.unmodifiableMap ( this.attributes );
    }

    public WriteInformation getWriteInformation ()
    {
        return this.writeInformation;
    }

    public Variant getValue ()
    {
        return this.value;
    }

    /**
     * Check if the request is an empty request. A request
     * is empty if neither attributes nor the primary value
     * is requested to be written.
     * @return <code>true</code>if the request is empty, <code>false</code>
     * otherwise
     */
    public boolean isEmpty ()
    {
        return this.value == null && ( this.attributes == null || this.attributes.isEmpty () );
    }
}
