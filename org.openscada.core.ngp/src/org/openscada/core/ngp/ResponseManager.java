/*
 * This file is part of the openSCADA project
 * 
 * Copyright (C) 2013 Jens Reimann (ctron@dentrassi.de)
 *
 * openSCADA is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 3
 * only, as published by the Free Software Foundation.
 *
 * openSCADA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License version 3 for more details
 * (a copy is included in the LICENSE file that accompanied this code).
 *
 * You should have received a copy of the GNU Lesser General Public License
 * version 3 along with openSCADA. If not, see
 * <http://opensource.org/licenses/lgpl-3.0.html> for a copy of the LGPLv3 License.
 */

package org.openscada.core.ngp;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicLong;

import org.openscada.core.OperationException;
import org.openscada.core.data.Request;
import org.openscada.core.data.RequestMessage;
import org.openscada.core.data.ResponseMessage;
import org.openscada.core.info.StatisticsImpl;
import org.openscada.utils.concurrent.ExecutorFuture;
import org.openscada.utils.concurrent.InstantErrorFuture;
import org.openscada.utils.concurrent.NotifyFuture;

public class ResponseManager
{

    private static final Object STATS_OPEN_REQUESTS = new Object ();

    private final Map<Long, ExecutorFuture<ResponseMessage>> requestMap = new HashMap<Long, ExecutorFuture<ResponseMessage>> ();

    private final StatisticsImpl statistics;

    private boolean connected;

    private final MessageSender messageSender;

    private final Executor executor;

    private final AtomicLong sequenceNumber = new AtomicLong ();

    public ResponseManager ( final StatisticsImpl statistics, final MessageSender messageSender, final Executor executor )
    {
        this.statistics = statistics;
        this.messageSender = messageSender;
        this.executor = executor;

        this.statistics.setLabel ( STATS_OPEN_REQUESTS, "Open requests" ); //$NON-NLS-1$
    }

    public void handleResponse ( final ResponseMessage message )
    {
        final ExecutorFuture<ResponseMessage> request = this.requestMap.remove ( message.getResponse ().getRequest ().getRequestId () );

        if ( request != null )
        {
            this.statistics.setCurrentValue ( STATS_OPEN_REQUESTS, this.requestMap.size () ); // update info
            request.asyncSetResult ( message );
        }
    }

    public void connected ()
    {
        this.connected = true;
    }

    public void disconnected ()
    {
        this.connected = false;
        // all operations got cancelled
        for ( final ExecutorFuture<ResponseMessage> request : this.requestMap.values () )
        {
            request.asyncSetError ( new OperationException ( "Operation was cancelled" ) );
        }
        this.requestMap.clear ();
    }

    public NotifyFuture<ResponseMessage> sendRequestMessage ( final RequestMessage requestMessage )
    {
        final Request request = requestMessage.getRequest ();
        if ( request == null )
        {
            return null;
        }

        if ( !this.connected )
        {
            return new InstantErrorFuture<ResponseMessage> ( new IllegalStateException ( "Connection is not conected" ) );
        }

        this.messageSender.sendMessage ( requestMessage );

        final long requestId = request.getRequestId ();
        final ExecutorFuture<ResponseMessage> result = new ExecutorFuture<ResponseMessage> ( this.executor );
        this.requestMap.put ( requestId, result );
        this.statistics.setCurrentValue ( STATS_OPEN_REQUESTS, this.requestMap.size () ); // update info

        return result;
    }

    public Request nextRequest ()
    {
        return new Request ( this.sequenceNumber.incrementAndGet () );
    }
}
