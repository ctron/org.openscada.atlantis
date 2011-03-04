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

package org.openscada.da.server.test.items;

import java.util.HashMap;
import java.util.Map;

import org.openscada.core.InvalidOperationException;
import org.openscada.core.Variant;
import org.openscada.da.core.OperationParameters;
import org.openscada.da.core.WriteAttributeResult;
import org.openscada.da.core.WriteAttributeResults;
import org.openscada.da.core.WriteResult;
import org.openscada.da.server.common.DataItemOutput;
import org.openscada.utils.concurrent.InstantErrorFuture;
import org.openscada.utils.concurrent.InstantFuture;
import org.openscada.utils.concurrent.NotifyFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestItem1 extends DataItemOutput
{

    private final static Logger logger = LoggerFactory.getLogger ( TestItem1.class );

    public TestItem1 ( final String name )
    {
        super ( name );
    }

    @Override
    public Map<String, Variant> getAttributes ()
    {
        return new HashMap<String, Variant> ();
    }

    @Override
    public NotifyFuture<WriteAttributeResults> startSetAttributes ( final Map<String, Variant> attributes, final OperationParameters operationParameters )
    {
        final WriteAttributeResults results = new WriteAttributeResults ();

        for ( final Map.Entry<String, Variant> entry : attributes.entrySet () )
        {
            logger.info ( "Attribute: '{}' => '{}'", entry.getKey (), entry.getValue () );
            if ( entry.getKey ().startsWith ( "error" ) )
            {
                results.put ( entry.getKey (), new WriteAttributeResult ( new Exception ( "Testing error" ) ) );
            }
            else
            {
                results.put ( entry.getKey (), WriteAttributeResult.OK );
            }
        }

        return new InstantFuture<WriteAttributeResults> ( results );
    }

    @Override
    public NotifyFuture<WriteResult> startWriteValue ( final Variant value, final OperationParameters operationParameters )
    {
        logger.debug ( "set value: {}", value.toString () );

        String data;
        try
        {
            data = value.asString ();
            if ( data.startsWith ( "error" ) )
            {
                return new InstantErrorFuture<WriteResult> ( new InvalidOperationException () );
            }
            return new InstantFuture<WriteResult> ( new WriteResult () );
        }
        catch ( final Throwable e )
        {
            return new InstantErrorFuture<WriteResult> ( e );
        }
    }
}
