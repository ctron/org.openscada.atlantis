/*
 * This file is part of the openSCADA project
 * Copyright (C) 2011-2012 TH4 SYSTEMS GmbH (http://th4-systems.com)
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

package org.openscada.core.client.ngp;

import java.util.HashMap;
import java.util.Map;

import org.openscada.core.ConnectionInformation;
import org.openscada.core.OperationException;
import org.openscada.core.client.ConnectionState;
import org.openscada.core.data.Request;
import org.openscada.core.data.RequestMessage;
import org.openscada.core.data.Response;
import org.openscada.core.data.ResponseMessage;
import org.openscada.core.data.message.CreateSession;
import org.openscada.core.data.message.SessionAccepted;
import org.openscada.core.data.message.SessionRejected;
import org.openscada.protocol.ngp.common.ProtocolConfigurationFactory;
import org.openscada.utils.concurrent.ExecutorFuture;
import org.openscada.utils.concurrent.InstantErrorFuture;
import org.openscada.utils.concurrent.NotifyFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConnectionBaseImpl extends ClientBaseConnection
{

    private final static Logger logger = LoggerFactory.getLogger ( ConnectionBaseImpl.class );

    private static final Object STATS_OPEN_REQUESTS = new Object ();

    private long sequenceNumber = 0;

    public ConnectionBaseImpl ( final ProtocolConfigurationFactory protocolConfigurationFactory, final ConnectionInformation connectionInformation ) throws Exception
    {
        super ( protocolConfigurationFactory, connectionInformation );
        this.statistics.setLabel ( STATS_OPEN_REQUESTS, "Open requests" );
    }

    @Override
    protected void onConnectionConnected ()
    {
        // send create session request
        logger.info ( "Requesting new session" );

        sendMessage ( new CreateSession ( this.connectionInformation.getProperties () ) );
    }

    @Override
    protected void onConnectionClosed ()
    {
        super.onConnectionClosed ();
        cancelRequests ();
    }

    @Override
    protected synchronized void handleMessage ( final Object message )
    {
        if ( message instanceof SessionAccepted )
        {
            setSessionProperties ( ( (SessionAccepted)message ).getProperties () );
            switchState ( ConnectionState.BOUND, null );
        }
        else if ( message instanceof SessionRejected )
        {
            // failure
            performDisconnected ( new IllegalStateException ( String.format ( "Failed to create session. Reply: %s", ( (SessionRejected)message ).getErrorReason () ) ).fillInStackTrace () );
        }
        else if ( message instanceof ResponseMessage )
        {
            handleResponse ( (ResponseMessage)message );
        }
    }

    // requests

    protected void handleResponse ( final ResponseMessage message )
    {
        final ExecutorFuture<ResponseMessage> request = this.requestMap.remove ( message.getResponse ().getRequest ().getRequestId () );

        if ( request != null )
        {
            this.statistics.setCurrentValue ( STATS_OPEN_REQUESTS, this.requestMap.size () ); // update info
            request.asyncSetResult ( message );
        }
    }

    protected synchronized long nextRequestNumber ()
    {
        return ++this.sequenceNumber;
    }

    protected synchronized Request nextRequest ()
    {
        return new Request ( nextRequestNumber () );
    }

    protected Response getResponseFromMessage ( final Object message )
    {
        if ( message instanceof ResponseMessage )
        {
            return ( (ResponseMessage)message ).getResponse ();
        }
        else
        {
            return null;
        }
    }

    private final Map<Long, ExecutorFuture<ResponseMessage>> requestMap = new HashMap<Long, ExecutorFuture<ResponseMessage>> ();

    protected synchronized void cancelRequests ()
    {
        // all operations got cancelled
        for ( final ExecutorFuture<ResponseMessage> request : this.requestMap.values () )
        {
            request.asyncSetError ( new OperationException ( "Operation was cancelled" ) );
        }
        this.requestMap.clear ();
    }

    protected synchronized NotifyFuture<ResponseMessage> sendRequestMessage ( final RequestMessage requestMessage )
    {
        final Request request = requestMessage.getRequest ();
        if ( request == null )
        {
            return null;
        }

        final ConnectionState state = getState ();

        if ( state != ConnectionState.BOUND )
        {
            return new InstantErrorFuture<ResponseMessage> ( new IllegalStateException ( String.format ( "Connection is not BOUND: %s", state ) ) );
        }

        sendMessage ( requestMessage );

        final long requestId = request.getRequestId ();
        final ExecutorFuture<ResponseMessage> result = new ExecutorFuture<ResponseMessage> ( this.executor );
        this.requestMap.put ( requestId, result );
        this.statistics.setCurrentValue ( STATS_OPEN_REQUESTS, this.requestMap.size () ); // update info

        return result;
    }

}
