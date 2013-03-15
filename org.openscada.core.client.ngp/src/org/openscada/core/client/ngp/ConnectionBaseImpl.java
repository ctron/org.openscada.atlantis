/*
 * This file is part of the openSCADA project
 * 
 * Copyright (C) 2011-2012 TH4 SYSTEMS GmbH (http://th4-systems.com)
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

package org.openscada.core.client.ngp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

import org.openscada.core.ConnectionInformation;
import org.openscada.core.client.ConnectionState;
import org.openscada.core.data.CallbackRequest;
import org.openscada.core.data.CallbackResponse;
import org.openscada.core.data.ErrorInformation;
import org.openscada.core.data.Request;
import org.openscada.core.data.RequestMessage;
import org.openscada.core.data.Response;
import org.openscada.core.data.ResponseMessage;
import org.openscada.core.data.message.CreateSession;
import org.openscada.core.data.message.RequestCallbacks;
import org.openscada.core.data.message.RespondCallbacks;
import org.openscada.core.data.message.SessionAccepted;
import org.openscada.core.data.message.SessionPrivilegesChanged;
import org.openscada.core.data.message.SessionRejected;
import org.openscada.core.ngp.Features;
import org.openscada.core.ngp.MessageSender;
import org.openscada.core.ngp.ResponseManager;
import org.openscada.protocol.ngp.common.ProtocolConfigurationFactory;
import org.openscada.sec.callback.Callback;
import org.openscada.sec.callback.CallbackFactory;
import org.openscada.sec.callback.CallbackHandler;
import org.openscada.sec.callback.DefaultCallbackFactory;
import org.openscada.utils.ExceptionHelper;
import org.openscada.utils.concurrent.FutureListener;
import org.openscada.utils.concurrent.NotifyFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConnectionBaseImpl extends ClientBaseConnection
{

    private final static Logger logger = LoggerFactory.getLogger ( ConnectionBaseImpl.class );

    private final ResponseManager responseManager;

    private final MessageSender messageSender = new MessageSender () {

        @Override
        public void sendMessage ( final Object message )
        {
            ConnectionBaseImpl.this.sendMessage ( message );
        }
    };

    private final CallbackHandlerManager callbackHandlerManager;

    private CallbackFactory callbackFactory;

    private final OpenCallbacksManager callbackManager;

    public ConnectionBaseImpl ( final ProtocolConfigurationFactory protocolConfigurationFactory, final ConnectionInformation connectionInformation ) throws Exception
    {
        super ( protocolConfigurationFactory, connectionInformation );
        this.responseManager = new ResponseManager ( this.statistics, this.messageSender, this.executor );
        this.callbackHandlerManager = new CallbackHandlerManager ( this.statistics );
        this.callbackManager = new OpenCallbacksManager ( this, this.statistics, this.executor );
        this.callbackFactory = new DefaultCallbackFactory ();
    }

    @Override
    public void setCallbackFactory ( final CallbackFactory callbackFactory )
    {
        this.callbackFactory = callbackFactory;
    }

    @Override
    protected void onConnectionConnected ()
    {
        // don't call super, would switch to BOUND immediately

        // connect request manager
        this.responseManager.connected ();

        // send create session request
        final Map<String, String> properties = makeProperties ();

        logger.info ( "Requesting new session: {}", properties ); //$NON-NLS-1$

        final Long callbackHandlerId = registerCallbackHandler ( nextRequest (), this.connectCallbackHandler );
        sendMessage ( new CreateSession ( properties, callbackHandlerId ) );
    }

    protected Map<String, String> makeProperties ()
    {
        final Map<String, String> result = new HashMap<String, String> ( this.connectionInformation.getProperties () );

        result.put ( Features.FEATURE_SESSION_PRIVILEGES, "true" ); //$NON-NLS-1$
        result.put ( Features.FEATURE_CALLBACKS, "true" ); //$NON-NLS-1$

        return result;
    }

    @Override
    protected void onConnectionClosed ()
    {
        super.onConnectionClosed ();
        synchronized ( this )
        {
            this.responseManager.disconnected ();
            this.callbackManager.disconnected ();
        }
    }

    @Override
    protected synchronized void handleMessage ( final Object message )
    {
        if ( message instanceof SessionAccepted )
        {
            handleSessionAccepted ( (SessionAccepted)message );
        }
        else if ( message instanceof SessionRejected )
        {
            // failure
            performDisconnected ( new IllegalStateException ( String.format ( "Failed to create session. Reply: %s", ( (SessionRejected)message ).getErrorReason () ) ).fillInStackTrace () );
        }
        else if ( message instanceof SessionPrivilegesChanged )
        {
            handlePrivilegeChange ( (SessionPrivilegesChanged)message );
        }
        else if ( message instanceof RequestCallbacks )
        {
            handleRequestCallbacks ( (RequestCallbacks)message );
        }
        else if ( message instanceof ResponseMessage )
        {
            handleResponse ( (ResponseMessage)message );
        }
    }

    private void handleResponse ( final ResponseMessage message )
    {
        this.callbackHandlerManager.unregisterHandler ( message.getResponse ().getRequest ().getRequestId () );
        this.responseManager.handleResponse ( message );
    }

    /**
     * @since 1.1
     */
    private void handleRequestCallbacks ( final RequestCallbacks message )
    {
        final CallbackHandler callbackHandler = this.callbackHandlerManager.getHandler ( message.getCallbackHandlerId () );
        if ( callbackHandler == null || this.callbackFactory == null )
        {
            // early abort
            sendMessage ( new RespondCallbacks ( new Response ( message.getRequest () ), allCallbacksCanceled ( message.getCallbacks ().size () ), null ) );
            return;
        }

        // make array
        final Callback[] callbacks = new Callback[message.getCallbacks ().size ()];

        // create callbacks from request
        int i = 0;
        for ( final CallbackRequest cr : message.getCallbacks () )
        {
            callbacks[i] = this.callbackFactory.createCallback ( cr.getType (), cr.getAttributes () );
            logger.debug ( "Created callback #{}: {}", i, callbacks[i] );
            i++;
        }

        // start processing
        final NotifyFuture<Callback[]> future = this.callbackManager.processCallbacks ( callbackHandler, callbacks, message.getTimeoutMillis () );
        future.addListener ( new FutureListener<Callback[]> () {

            @Override
            public void complete ( final Future<Callback[]> future )
            {
                processCallbackFuture ( message.getRequest (), future, callbacks );
            }
        } );
    }

    /**
     * @since 1.1
     */
    protected void processCallbackFuture ( final Request request, final Future<Callback[]> future, final Callback[] callbacks )
    {
        logger.debug ( "Processing callback result - request: {}, future: {}", request, future );

        final List<CallbackResponse> result = new LinkedList<CallbackResponse> ();
        ErrorInformation errorInformation = null;

        try
        {
            future.get (); // this is just a get call to see if we have an exception
            for ( final Callback cb : callbacks )
            {
                final boolean canceled = cb.isCanceled ();
                final Map<String, String> attributes = !canceled ? cb.buildResponseAttributes () : Collections.<String, String> emptyMap ();
                logger.debug ( "Callback result - canceled: {}, attributes: {}", canceled, attributes );
                result.add ( new CallbackResponse ( canceled, attributes ) );
            }
        }
        catch ( final Exception e )
        {
            logger.warn ( "Failed to build result map", e );
            errorInformation = new ErrorInformation ( null, ExceptionHelper.getMessage ( e ), ExceptionHelper.formatted ( e ) );
            result.clear ();
        }

        sendMessage ( new RespondCallbacks ( new Response ( request ), result, errorInformation ) );
    }

    /**
     * Return a result with all callbacks canceled
     * 
     * @param count
     *            the number of callbacks in the request
     * @return the message result for canceled callbacks
     */
    private List<CallbackResponse> allCallbacksCanceled ( final int count )
    {
        final List<CallbackResponse> response = new ArrayList<CallbackResponse> ( count );
        for ( int i = 0; i < count; i++ )
        {
            response.add ( new CallbackResponse ( true, Collections.<String, String> emptyMap () ) );
        }
        return response;
    }

    private void handleSessionAccepted ( final SessionAccepted message )
    {
        final Map<String, String> properties = message.getProperties ();

        setSessionProperties ( properties );
        switchState ( ConnectionState.BOUND, null );
    }

    // callbacks

    /**
     * @since 1.1
     */
    protected synchronized Long registerCallbackHandler ( final Request request, final CallbackHandler callbackHandler )
    {
        if ( callbackHandler == null )
        {
            return null;
        }
        else
        {
            this.callbackHandlerManager.registerHandler ( request.getRequestId (), callbackHandler );
            return request.getRequestId ();
        }
    }

    // requests

    private void handlePrivilegeChange ( final SessionPrivilegesChanged message )
    {
        firePrivilegeChange ( message.getGranted () );
    }

    protected synchronized Request nextRequest ()
    {
        return this.responseManager.nextRequest ();
    }

    protected synchronized NotifyFuture<ResponseMessage> sendRequestMessage ( final RequestMessage requestMessage )
    {
        return this.responseManager.sendRequestMessage ( requestMessage );
    }

}
