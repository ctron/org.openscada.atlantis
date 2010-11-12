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

package org.openscada.da.server.test.items;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.openscada.core.InvalidOperationException;
import org.openscada.core.Variant;
import org.openscada.core.server.common.session.UserSession;
import org.openscada.da.core.WriteAttributeResult;
import org.openscada.da.core.WriteAttributeResults;
import org.openscada.da.core.WriteResult;
import org.openscada.da.server.common.DataItemOutput;
import org.openscada.utils.concurrent.InstantErrorFuture;
import org.openscada.utils.concurrent.InstantFuture;
import org.openscada.utils.concurrent.NotifyFuture;

public class TestItem1 extends DataItemOutput
{
    private static Logger _log = Logger.getLogger ( TestItem1.class );

    public TestItem1 ( final String name )
    {
        super ( name );
    }

    public Map<String, Variant> getAttributes ()
    {
        return new HashMap<String, Variant> ();
    }

    public NotifyFuture<WriteAttributeResults> startSetAttributes ( final UserSession session, final Map<String, Variant> attributes )
    {
        final WriteAttributeResults results = new WriteAttributeResults ();

        for ( final Map.Entry<String, Variant> entry : attributes.entrySet () )
        {
            _log.info ( String.format ( "Attribute: '%s' => '%s'", entry.getKey (), entry.getValue ().toString () ) );
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

    public NotifyFuture<WriteResult> startWriteValue ( final UserSession session, final Variant value )
    {
        _log.debug ( "set value: " + value.toString () );

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
