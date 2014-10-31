/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2011 TH4 SYSTEMS GmbH (http://th4-systems.com)
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openscada.da.server.opc.connection;

import java.util.concurrent.Future;

import org.eclipse.scada.core.OperationException;
import org.eclipse.scada.da.core.WriteResult;
import org.eclipse.scada.utils.concurrent.AbstractFuture;
import org.eclipse.scada.utils.concurrent.FutureListener;
import org.eclipse.scada.utils.concurrent.NotifyFuture;
import org.openscada.opc.dcom.common.Result;
import org.openscada.opc.dcom.da.WriteRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    private final static Logger logger = LoggerFactory.getLogger ( WriteFuture.class );

    private final NotifyFuture<Result<WriteRequest>> opcFuture;

    private final OPCItem opcItem;

    public WriteFuture ( final OPCItem opcItem, final NotifyFuture<Result<WriteRequest>> opcFuture )
    {
        this.opcItem = opcItem;

        this.opcFuture = opcFuture;
        this.opcFuture.addListener ( new FutureListener<Result<WriteRequest>> () {

            @Override
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
