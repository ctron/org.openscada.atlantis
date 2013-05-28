/*
 * This file is part of the OpenSCADA project
 * 
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

package org.openscada.core.server.common;

import java.util.Map;
import java.util.concurrent.Future;

import org.openscada.core.data.OperationParameters;
import org.openscada.core.server.common.session.AbstractSessionImpl;
import org.openscada.sec.AuthorizationReply;
import org.openscada.sec.AuthorizationRequest;
import org.openscada.sec.AuthorizationResult;
import org.openscada.sec.PermissionDeniedException;
import org.openscada.sec.UserInformation;
import org.openscada.sec.callback.CallbackHandler;
import org.openscada.utils.concurrent.AbstractFuture;
import org.openscada.utils.concurrent.FutureListener;
import org.openscada.utils.concurrent.NotifyFuture;

/**
 * @since 1.1
 */
public abstract class AuthorizedOperation<T, SI extends AbstractSessionImpl> extends AbstractFuture<T>
{

    private final String objectType;

    private final String objectId;

    private final String action;

    private final Map<String, Object> context;

    private final AuthorizationProvider<SI> authorizationProvider;

    private final CallbackHandler handler;

    private final AuthorizationResult defaultResult;

    private final OperationParameters operationParameters;

    public AuthorizedOperation ( final AuthorizationProvider<SI> authorizationProvider, final SI session, final String objectType, final String objectId, final String action, final Map<String, Object> context, final OperationParameters operationParameters, final CallbackHandler handler, final AuthorizationResult defaultResult )
    {
        this.authorizationProvider = authorizationProvider;
        this.objectType = objectType;
        this.objectId = objectId;
        this.action = action;
        this.context = context;
        this.handler = handler;
        this.defaultResult = defaultResult;
        this.operationParameters = operationParameters;

        final NotifyFuture<UserInformation> future = this.authorizationProvider.impersonate ( session, operationParameters == null ? null : operationParameters.getUserInformation ().getName (), handler );
        future.addListener ( new FutureListener<UserInformation> () {

            @Override
            public void complete ( final Future<UserInformation> future )
            {
                handleCompleteImpersonate ( future );
            }
        } );
    }

    protected void handleCompleteImpersonate ( final Future<UserInformation> future )
    {
        try
        {
            final UserInformation userInformation = future.get ();

            final NotifyFuture<AuthorizationReply> f = this.authorizationProvider.authorize ( makeRequest ( userInformation ), this.handler, this.defaultResult );

            f.addListener ( new FutureListener<AuthorizationReply> () {

                @Override
                public void complete ( final Future<AuthorizationReply> future )
                {
                    handleCompleteAuthorize ( future );
                }
            } );
        }
        catch ( final Exception e )
        {
            setError ( e );
        }
    }

    protected void handleCompleteAuthorize ( final Future<AuthorizationReply> future )
    {
        try
        {
            final AuthorizationReply result = future.get ();

            final PermissionDeniedException ex = result.getResult ().asException ();
            if ( ex != null )
            {
                // we got rejected
                setError ( ex );
                return;
            }

            final NotifyFuture<T> f = granted ( makeParameters ( result ) );
            f.addListener ( new FutureListener<T> () {

                @Override
                public void complete ( final Future<T> future )
                {
                    try
                    {
                        setResult ( future.get () );
                    }
                    catch ( final Exception e )
                    {
                        setError ( e );
                    }
                }
            } );
        }
        catch ( final Exception e )
        {
            setError ( e );
        }
    }

    private org.openscada.core.server.OperationParameters makeParameters ( final AuthorizationReply result )
    {
        return new org.openscada.core.server.OperationParameters ( result.getUserInformation (), this.operationParameters == null ? null : this.operationParameters.getProperties (), this.handler );
    }

    private AuthorizationRequest makeRequest ( final UserInformation effective )
    {
        return new AuthorizationRequest ( this.objectType, this.objectId, this.action, effective, this.context );
    }

    protected abstract NotifyFuture<T> granted ( org.openscada.core.server.OperationParameters effectiveOperationParameters );

}
