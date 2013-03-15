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

import org.openscada.sec.AuthenticationException;
import org.openscada.sec.AuthenticationImplementation;
import org.openscada.sec.UserInformation;
import org.openscada.sec.callback.Callback;
import org.openscada.sec.callback.CallbackHandler;
import org.openscada.sec.callback.Callbacks;
import org.openscada.sec.callback.PasswordCallback;
import org.openscada.utils.concurrent.InstantFuture;
import org.openscada.utils.concurrent.NotifyFuture;
import org.openscada.utils.concurrent.TransformResultFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @since 1.1
 */
public class DefaultAuthentication implements AuthenticationImplementation
{

    private final static Logger logger = LoggerFactory.getLogger ( DefaultAuthentication.class );

    /**
     * Authenticate a user
     * <p>
     * This method simply implements an <em>any</em> authentication which allows
     * access to session with or without user names. No password is checked.
     * </p>
     * <p>
     * This method should be overridden if a different authentication scheme is
     * required.
     * </p>
     * 
     * @param username
     *            the username
     * @param password
     *            the password
     * @param sessionResultProperties
     *            the session properties that will be returned to the client.
     *            The method may add or remove properties as it likes.
     * @return the user information object or <code>null</code> if it is an
     *         anonymous session
     * @throws AuthenticationException
     *             if the user was rejected
     */
    @Override
    public NotifyFuture<UserInformation> authenticate ( final CallbackHandler callbackHandler )
    {
        final String plainPassword = System.getProperty ( "org.openscada.core.server.common.ServiceCommon.password" ); //$NON-NLS-1$

        if ( plainPassword == null || plainPassword.isEmpty () )
        {
            // no need to request password
            return new InstantFuture<UserInformation> ( UserInformation.ANONYMOUS );
        }

        final NotifyFuture<Callback[]> future = Callbacks.callback ( callbackHandler, new Callback[] { new PasswordCallback ( "Password", 200 ) } );

        return new TransformResultFuture<Callback[], UserInformation> ( future ) {
            @Override
            protected UserInformation transform ( final Callback[] callbacks ) throws Exception
            {
                // pass on password from our check, so that it cannot change inbetween
                return processAuthenticate ( callbacks, plainPassword );
            }
        };

    }

    protected UserInformation processAuthenticate ( final Callback[] callbacks, final String plainPassword ) throws AuthenticationException
    {
        final Callback cb = callbacks[0];

        String password = null;
        if ( cb instanceof PasswordCallback )
        {
            password = ( (PasswordCallback)cb ).getPassword ();
        }

        if ( password == null || !plainPassword.equals ( password ) )
        {
            logger.debug ( "Password requested using system properties. But none or wrong provided." );
            throw new AuthenticationException ( org.openscada.sec.StatusCodes.INVALID_USER_OR_PASSWORD );
        }
        else
        {
            return UserInformation.ANONYMOUS;
        }

    }

}
