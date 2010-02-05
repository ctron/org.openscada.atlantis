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
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;

import org.openscada.core.InvalidOperationException;
import org.openscada.core.NotConvertableException;
import org.openscada.core.NullValueException;
import org.openscada.core.OperationException;
import org.openscada.core.Variant;
import org.openscada.core.server.common.session.UserSession;
import org.openscada.da.core.WriteAttributeResults;
import org.openscada.da.core.WriteResult;
import org.openscada.da.server.common.DataItemOutput;
import org.openscada.da.server.common.WriteAttributesHelper;
import org.openscada.utils.concurrent.FutureTask;
import org.openscada.utils.concurrent.InstantFuture;
import org.openscada.utils.concurrent.NotifyFuture;

public class WriteDelayItem extends DataItemOutput
{

    private final Executor executor;

    public WriteDelayItem ( final String name, final Executor executor )
    {
        super ( name );
        this.executor = executor;
    }

    public Map<String, Variant> getAttributes ()
    {
        return new HashMap<String, Variant> ();
    }

    public NotifyFuture<WriteAttributeResults> startSetAttributes ( final UserSession session, final Map<String, Variant> attributes )
    {
        return new InstantFuture<WriteAttributeResults> ( WriteAttributesHelper.errorUnhandled ( null, attributes ) );
    }

    public NotifyFuture<WriteResult> startWriteValue ( final UserSession session, final Variant value )
    {
        final FutureTask<WriteResult> task = new FutureTask<WriteResult> ( new Callable<WriteResult> () {

            public WriteResult call () throws Exception
            {
                WriteDelayItem.this.processWrite ( value );
                return new WriteResult ();
            }
        } );

        this.executor.execute ( task );

        return task;
    }

    public void processWrite ( final Variant value ) throws InvalidOperationException, NullValueException, NotConvertableException, OperationException
    {
        final int delay = value.asInteger ();

        System.out.println ( "Start write: " + delay + "ms" );
        try
        {
            Thread.sleep ( delay );
            System.out.println ( "End write" );
        }
        catch ( final InterruptedException e )
        {
            System.err.println ( "Write failed" );
            e.printStackTrace ();
            throw new OperationException ( "Interrupted" );
        }
        finally
        {
            System.out.println ( "leave write" );
        }

    }

}
