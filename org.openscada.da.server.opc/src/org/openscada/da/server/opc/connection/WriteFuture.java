/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2010 TH4 SYSTEMS GmbH (http://inavare.com)
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
