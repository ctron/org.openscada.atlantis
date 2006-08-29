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

package org.openscada.da.core.common;

import java.util.Map;

import org.openscada.core.Variant;
import org.openscada.da.core.server.WriteAttributesOperationListener.Result;
import org.openscada.da.core.server.WriteAttributesOperationListener.Results;

public class WriteAttributesHelper
{
    public static Results errorUnhandled ( Results initialResults, Map<String, Variant> attributes )
    {
        Results results = initialResults;
        
        if ( results == null )
        {
            results = new Results ();
        }
        
        for ( String name : attributes.keySet () )
        {
            if ( !results.containsKey ( name ) )
                results.put ( name, new Result ( new UnsupportedOperationException ( "Operation not supported" ) ) );
        }
        return results;
    }
}
