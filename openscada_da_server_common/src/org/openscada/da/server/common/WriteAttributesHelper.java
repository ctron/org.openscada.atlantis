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
