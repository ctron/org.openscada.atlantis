/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2010 TH4 SYSTEMS GmbH (http://th4-systems.com)
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

package org.openscada.da.server.common;

import java.util.Map;

import org.openscada.core.Variant;
import org.openscada.da.core.WriteAttributeResult;
import org.openscada.da.core.WriteAttributeResults;

public class WriteAttributesHelper
{
    /**
     * This method sets all unprocessed attribute write requests to an <q>unsupported</q>
     * error ({@link UnsupportedOperationException});
     * @param initialResults The results genereted so far
     * @param attributes The attributes to write
     * @return the initial results including the unprocessed results
     */
    public static WriteAttributeResults errorUnhandled ( final WriteAttributeResults initialResults, final Map<String, Variant> attributes )
    {
        WriteAttributeResults writeAttributeResults = initialResults;

        if ( writeAttributeResults == null )
        {
            writeAttributeResults = new WriteAttributeResults ();
        }

        for ( final String name : attributes.keySet () )
        {
            if ( !writeAttributeResults.containsKey ( name ) )
            {
                writeAttributeResults.put ( name, new WriteAttributeResult ( new UnsupportedOperationException ( "Operation not supported" ) ) );
            }
        }
        return writeAttributeResults;
    }
}
