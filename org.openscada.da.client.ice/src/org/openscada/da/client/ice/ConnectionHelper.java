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

package org.openscada.da.client.ice;

import org.apache.log4j.Logger;
import org.openscada.da.core.WriteAttributeResult;
import org.openscada.da.core.WriteAttributeResults;

import OpenSCADA.DA.WriteAttributesResultEntry;

public class ConnectionHelper
{
    private static Logger _log = Logger.getLogger ( ConnectionHelper.class );

    public static WriteAttributeResults fromIce ( final WriteAttributesResultEntry[] entries )
    {
        final WriteAttributeResults ret = new WriteAttributeResults ();

        for ( final WriteAttributesResultEntry entry : entries )
        {
            _log.debug ( String.format ( "Attribute result '%s': '%s'", entry.item, entry.result ) );

            if ( entry.result != null && entry.result.length () > 0 )
            {
                ret.put ( entry.item, new WriteAttributeResult ( new Exception ( entry.result ) ) );
            }
            else
            {
                ret.put ( entry.item, WriteAttributeResult.OK );
            }
        }
        return ret;
    }
}
