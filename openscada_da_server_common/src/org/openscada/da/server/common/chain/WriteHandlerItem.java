/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2009 inavare GmbH (http://inavare.com)
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

package org.openscada.da.server.common.chain;

import java.util.concurrent.Callable;
import java.util.concurrent.Executor;

import org.openscada.core.InvalidOperationException;
import org.openscada.core.Variant;
import org.openscada.da.core.DataItemInformation;
import org.openscada.da.core.WriteResult;
import org.openscada.utils.concurrent.FutureTask;
import org.openscada.utils.concurrent.InstantErrorFuture;
import org.openscada.utils.concurrent.NotifyFuture;

public class WriteHandlerItem extends DataItemInputOutputChained
{

    private volatile WriteHandler writeHandler;

    public WriteHandlerItem ( final DataItemInformation di, final WriteHandler writeHandler, final Executor executor )
    {
        super ( di, executor );
        this.writeHandler = writeHandler;
    }

    public WriteHandlerItem ( final String itemId, final WriteHandler writeHandler, final Executor executor )
    {
        super ( itemId, executor );
        this.writeHandler = writeHandler;
    }

    /**
     * Change the write handler
     * <p>
     * The write handler will not be called for the last written value
     * only for the next one.
     * 
     * @param writeHandler the new write handler
     */
    public void setWriteHandler ( final WriteHandler writeHandler )
    {
        this.writeHandler = writeHandler;
    }

    @Override
    protected NotifyFuture<WriteResult> startWriteCalculatedValue ( final Variant value )
    {
        final WriteHandler writeHandler = this.writeHandler;

        // if we don't have a write handler this is not allowed
        if ( writeHandler == null )
        {
            return new InstantErrorFuture<WriteResult> ( new InvalidOperationException ().fillInStackTrace () );
        }

        final FutureTask<WriteResult> task = new FutureTask<WriteResult> ( new Callable<WriteResult> () {

            public WriteResult call () throws Exception
            {
                writeHandler.handleWrite ( value );
                return new WriteResult ();
            }
        } );

        this.executor.execute ( task );

        return task;
    }

}
