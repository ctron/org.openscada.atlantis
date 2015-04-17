/*******************************************************************************
 * Copyright (c) 2014, 2015 IBH SYSTEMS GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBH SYSTEMS GmbH - initial API and implementation
 *******************************************************************************/
package org.openscada.da.server.common;

import java.util.concurrent.Executor;

import org.eclipse.scada.core.InvalidSessionException;
import org.eclipse.scada.core.Variant;
import org.eclipse.scada.core.server.OperationParameters;
import org.eclipse.scada.da.core.WriteResult;
import org.eclipse.scada.da.core.server.InvalidItemException;
import org.eclipse.scada.da.server.common.DataItem;
import org.eclipse.scada.da.server.common.impl.SessionCommon;
import org.eclipse.scada.da.server.common.osgi.AbstractOsgiHiveCommon;
import org.eclipse.scada.utils.concurrent.InstantErrorFuture;
import org.eclipse.scada.utils.concurrent.NotifyFuture;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractWriteHandlerHive extends AbstractOsgiHiveCommon
{
    final static Logger logger = LoggerFactory.getLogger ( AbstractWriteHandlerHive.class );

    public AbstractWriteHandlerHive ( final BundleContext context )
    {
        super ( context );
    }

    public AbstractWriteHandlerHive ( final BundleContext context, final Executor executor )
    {
        super ( context, executor );
    }

    @Override
    protected NotifyFuture<WriteResult> processWrite ( final SessionCommon session, final String itemId, final Variant value, final org.eclipse.scada.core.server.OperationParameters effectiveOperationParameters )
    {
        logger.debug ( "Processing write - granted - itemId: {}, value: {}", itemId, value );

        final WriteHandler writeHandler = getWriteHandler ( session, itemId );

        if ( writeHandler == null )
        {
            return new InstantErrorFuture<WriteResult> ( new InvalidItemException ( itemId ).fillInStackTrace () );
        }

        // go
        final NotifyFuture<WriteResult> future = writeHandler.startWriteValue ( value, effectiveOperationParameters );
        try
        {
            session.addFuture ( future );
            return future;
        }
        catch ( final InvalidSessionException e )
        {
            return new InstantErrorFuture<WriteResult> ( e );
        }
    }

    protected WriteHandler getWriteHandler ( final SessionCommon session, final String itemId )
    {
        final DataItem item = retrieveItem ( itemId );
        if ( item != null )
        {
            return new WriteHandler () {

                @Override
                public NotifyFuture<WriteResult> startWriteValue ( final Variant value, final OperationParameters effectiveOperationParameters )
                {
                    return item.startWriteValue ( value, effectiveOperationParameters );
                }
            };
        }
        return null;
    }

}
