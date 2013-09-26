/*
 * This file is part of the OpenSCADA project
 * 
 * Copyright (C) 2006-2011 TH4 SYSTEMS GmbH (http://th4-systems.com)
 * Copyright (C) 2013 Jens Reimann (ctron@dentrassi.de)
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

package org.openscada.da.server.common.chain;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.scada.core.Variant;
import org.eclipse.scada.da.core.WriteAttributeResult;
import org.eclipse.scada.da.core.WriteAttributeResults;

public abstract class BaseChainItemCommon implements ChainItem
{

    private final Set<String> reservedAttributes = new HashSet<String> ();

    @Override
    public WriteAttributeResults setAttributes ( final Map<String, Variant> attributes )
    {
        final WriteAttributeResults writeAttributeResults = new WriteAttributeResults ();

        for ( final Map.Entry<String, Variant> entry : attributes.entrySet () )
        {
            if ( this.reservedAttributes.contains ( entry.getKey () ) )
            {
                writeAttributeResults.put ( entry.getKey (), new WriteAttributeResult ( new Exception ( "Attribute may not be set" ) ) );
            }
        }

        return writeAttributeResults;
    }

    public void setReservedAttributes ( final String... reservedAttributes )
    {
        this.reservedAttributes.addAll ( Arrays.asList ( reservedAttributes ) );
    }

}
