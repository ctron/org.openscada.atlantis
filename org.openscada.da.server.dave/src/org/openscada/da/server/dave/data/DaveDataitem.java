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

package org.openscada.da.server.dave.data;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;

import org.openscada.core.Variant;
import org.openscada.da.core.OperationParameters;
import org.openscada.da.core.WriteAttributeResults;
import org.openscada.da.core.WriteResult;
import org.openscada.da.server.common.chain.DataItemInputOutputChained;
import org.openscada.utils.concurrent.NotifyFuture;

public class DaveDataitem extends DataItemInputOutputChained
{

    private final ScalarVariable variable;

    public DaveDataitem ( final String itemId, final Executor executor, final ScalarVariable variable )
    {
        super ( itemId, executor );
        this.variable = variable;
    }

    @Override
    protected WriteAttributeResults handleUnhandledAttributes ( final WriteAttributeResults initialResults, final Map<String, Variant> attributes )
    {
        // check for null
        WriteAttributeResults writeAttributeResults = initialResults;
        if ( writeAttributeResults == null )
        {
            writeAttributeResults = new WriteAttributeResults ();
        }

        // gather the list of open requests
        final Map<String, Variant> requests = new HashMap<String, Variant> ( 0 );

        for ( final Map.Entry<String, Variant> entry : attributes.entrySet () )
        {
            if ( !writeAttributeResults.containsKey ( entry.getKey () ) )
            {
                requests.put ( entry.getKey (), entry.getValue () );
            }
        }

        writeAttributeResults.putAll ( this.variable.handleAttributes ( requests ) );

        // default for the rest
        return super.handleUnhandledAttributes ( writeAttributeResults, attributes );
    }

    @Override
    protected NotifyFuture<WriteResult> startWriteCalculatedValue ( final Variant value, final OperationParameters operationParameters )
    {
        return this.variable.handleWrite ( value );
    }

}
