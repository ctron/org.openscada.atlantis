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

package org.openscada.da.server.common.chain;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;

import org.openscada.core.Variant;
import org.openscada.core.server.common.session.UserSession;
import org.openscada.da.core.DataItemInformation;
import org.openscada.da.core.WriteAttributeResult;
import org.openscada.da.core.WriteAttributeResults;
import org.openscada.da.core.WriteResult;
import org.openscada.da.server.common.DataItemInformationBase;
import org.openscada.utils.concurrent.FutureTask;
import org.openscada.utils.concurrent.NotifyFuture;

public class AttributeWriteHandlerItem extends DataItemInputChained
{

    private volatile AttributeWriteHandler writeHandler;

    public AttributeWriteHandlerItem ( final DataItemInformation di, final AttributeWriteHandler writeHandler, final Executor executor )
    {
        super ( di, executor );
        this.writeHandler = writeHandler;
    }

    public AttributeWriteHandlerItem ( final String itemId, final AttributeWriteHandler writeHandler, final Executor executor )
    {
        super ( new DataItemInformationBase ( itemId ), executor );
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
    public void setWriteHandler ( final AttributeWriteHandler writeHandler )
    {
        this.writeHandler = writeHandler;
    }

    @Override
    protected WriteAttributeResults handleUnhandledAttributes ( final WriteAttributeResults writeAttributeResults, final Map<String, Variant> attributes )
    {
        final AttributeWriteHandler handler = this.writeHandler;

        WriteAttributeResults result = null;
        try
        {
            result = handler.handleWrite ( attributes );
            // remove handled attributes
            for ( final String attr : result.keySet () )
            {
                attributes.remove ( attr );
            }
            return super.handleUnhandledAttributes ( result, attributes );
        }
        catch ( final Exception e )
        {
            if ( result == null )
            {
                result = new WriteAttributeResults ();
            }

            for ( final String attr : attributes.keySet () )
            {
                result.put ( attr, new WriteAttributeResult ( e ) );
            }
            return result;
        }
    }

    @Override
    public NotifyFuture<WriteResult> startWriteValue ( final UserSession session, final Variant value )
    {
        final FutureTask<WriteResult> task = new FutureTask<WriteResult> ( new Callable<WriteResult> () {

            public WriteResult call () throws Exception
            {
                return processWriteValue ( value );
            }
        } );

        this.executor.execute ( task );

        return task;
    }

    protected WriteResult processWriteValue ( final Variant value )
    {
        final AttributeWriteHandler handler = this.writeHandler;
        if ( handler == null )
        {
            return new WriteResult ( new IllegalStateException ( "No write handler set" ).fillInStackTrace () );
        }

        try
        {
            handler.handleWrite ( value );
        }
        catch ( final Exception e )
        {
            return new WriteResult ( e );
        }

        return new WriteResult ();
    }

}
