/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2010 TH4 SYSTEMS GmbH (http://inavare.com)
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
import org.openscada.da.core.WriteAttributeResults;

public class WriteRequestResult
{
    private final Throwable error;

    private final Variant value;

    private final Map<String, Variant> attributes;

    private final WriteAttributeResults attributeResults;

    public WriteRequestResult ( final Variant value, final Map<String, Variant> attributes, final WriteAttributeResults attributeResults )
    {
        this.value = value;

        if ( attributes != null )
        {
            this.attributes = new HashMap<String, Variant> ( attributes );
        }
        else
        {
            this.attributes = Collections.emptyMap ();
        }

        if ( attributeResults != null )
        {
            this.attributeResults = (WriteAttributeResults)attributeResults.clone ();
        }
        else
        {
            this.attributeResults = new WriteAttributeResults ();
        }
        this.error = null;
    }

    public WriteRequestResult ( final Throwable error )
    {
        this.value = null;
        this.attributes = null;
        this.attributeResults = null;
        this.error = error;
    }

    public WriteAttributeResults getAttributeResults ()
    {
        return this.attributeResults;
    }

    public Map<String, Variant> getAttributes ()
    {
        return this.attributes;
    }

    public Throwable getError ()
    {
        return this.error;
    }

    public Variant getValue ()
    {
        return this.value;
    }
}