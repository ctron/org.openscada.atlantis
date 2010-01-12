/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2009 inavare GmbH (http://inavare.com)
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

package org.openscada.da.server.opc.connection;

import java.util.concurrent.Future;

import org.apache.log4j.Logger;
import org.openscada.core.OperationException;
import org.openscada.da.core.WriteResult;
import org.openscada.opc.dcom.common.Result;
import org.openscada.opc.dcom.da.WriteRequest;
import org.openscada.utils.concurrent.AbstractFuture;
import org.openscada.utils.concurrent.FutureListener;
import org.openscada.utils.concurrent.NotifyFuture;

/**
 * A write future wrapper for OPC write futures
 * <p>
 * This write future wraps the OPC write future and hooks up to its listeners.
 * Once the write if completed by OPC the result will be forwarded to the
 * data item write future. 
 * </p>
 * @author Jens Reimann
 * 
 */
public class WriteFuture extends AbstractFuture<WriteResult>
{
    private final static Logger logger = Logger.getLogger ( WriteFuture.class );

    private final NotifyFuture<Result<WriteRequest>> opcFuture;

    private final OPCItem opcItem;

    public WriteFuture ( final OPCItem opcItem, final NotifyFuture<Result<WriteRequest>> opcFuture )
    {
        this.opcItem = opcItem;

        this.opcFuture = opcFuture;
        this.opcFuture.addListener ( new FutureListener<Result<WriteRequest>> () {

            public void complete ( final Future<Result<WriteRequest>> future )
            {
                handleComplete ();
            }
        } );
    }

    protected void handleComplete ()
    {
        final Result<WriteRequest> result;
        try
        {
            result = this.opcFuture.get ();
            logger.info ( "Write returned" );
            setResult ( new WriteResult () );
        }
        catch ( final Throwable e )
        {
            logger.info ( "Failed to write", e );
            this.opcItem.setLastWriteError ( null );
            setError ( new OperationException ( "Failed to write", e ).fillInStackTrace () );
            return;
        }

        if ( result.isFailed () )
        {
            this.opcItem.setLastWriteError ( result );
            setError ( new OperationException ( String.format ( "Write returned with failure: 0x%08X", result.getErrorCode () ) ).fillInStackTrace () );
        }
    }
}
