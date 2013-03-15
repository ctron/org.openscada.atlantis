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
import org.openscada.core.server.OperationParameters;
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

    @Override
    public Map<String, Variant> getAttributes ()
    {
        return new HashMap<String, Variant> ();
    }

    @Override
    public NotifyFuture<WriteAttributeResults> startSetAttributes ( final Map<String, Variant> attributes, final OperationParameters operationParameters )
    {
        return new InstantFuture<WriteAttributeResults> ( WriteAttributesHelper.errorUnhandled ( null, attributes ) );
    }

    @Override
    public NotifyFuture<WriteResult> startWriteValue ( final Variant value, final OperationParameters operationParameters )
    {
        final FutureTask<WriteResult> task = new FutureTask<WriteResult> ( new Callable<WriteResult> () {

            @Override
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
