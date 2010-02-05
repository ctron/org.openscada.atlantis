/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2010 inavare GmbH (http://inavare.com)
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
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
