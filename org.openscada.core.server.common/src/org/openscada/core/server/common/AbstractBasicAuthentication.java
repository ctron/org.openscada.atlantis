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

import org.eclipse.scada.sec.AuthenticationException;
import org.eclipse.scada.sec.AuthenticationImplementation;
import org.eclipse.scada.sec.StatusCodes;
import org.eclipse.scada.sec.UserInformation;
import org.eclipse.scada.sec.callback.Callback;
import org.eclipse.scada.sec.callback.CallbackHandler;
import org.eclipse.scada.sec.callback.Callbacks;
import org.eclipse.scada.sec.callback.PasswordCallback;
import org.eclipse.scada.sec.utils.password.PasswordType;
import org.eclipse.scada.utils.concurrent.InstantFuture;
import org.eclipse.scada.utils.concurrent.NotifyFuture;
import org.eclipse.scada.utils.concurrent.TransformResultFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @since 1.1
 */
public abstract class AbstractBasicAuthentication implements AuthenticationImplementation
{

    private static final Logger logger = LoggerFactory.getLogger ( DefaultAuthentication.class );

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
        final String plainPassword = getPlainPassword ();

        if ( plainPassword == null || plainPassword.isEmpty () )
        {
            // no need to request password
            return new InstantFuture<UserInformation> ( UserInformation.ANONYMOUS );
        }

        final NotifyFuture<Callback[]> future = Callbacks.callback ( callbackHandler, new Callback[] { new PasswordCallback ( "Password", 200, PasswordType.PLAIN.getSupportedInputEncodings () ) } );

        return new TransformResultFuture<Callback[], UserInformation> ( future ) {
            @Override
            protected UserInformation transform ( final Callback[] callbacks ) throws Exception
            {
                // pass on password from our check, so that it cannot change inbetween
                return processAuthenticate ( callbacks, plainPassword );
            }
        };
    }

    protected abstract String getPlainPassword ();

    @Override
    public UserInformation getUser ( final String user )
    {
        // we only know anonymous
        return UserInformation.ANONYMOUS;
    }

    protected UserInformation processAuthenticate ( final Callback[] callbacks, final String plainPassword ) throws AuthenticationException
    {
        final Callback cb = callbacks[0];

        if ( ! ( cb instanceof PasswordCallback ) )
        {
            logger.debug ( "Password requested using system properties. But none was provided." );
            throw new AuthenticationException ( org.eclipse.scada.sec.StatusCodes.INVALID_USER_OR_PASSWORD, "Invalid username or wrong password" );
        }

        try
        {
            if ( !PasswordType.PLAIN.createValdiator ().validatePassword ( ( (PasswordCallback)cb ).getPasswords (), plainPassword ) )
            {
                logger.debug ( "Password requested using system properties. But none or wrong provided." );
                throw new AuthenticationException ( org.eclipse.scada.sec.StatusCodes.INVALID_USER_OR_PASSWORD, "Invalid username or wrong password" );
            }
        }
        catch ( final Exception e )
        {
            logger.warn ( "Failed to authenticate", e );
            throw new AuthenticationException ( StatusCodes.AUTHENTICATION_FAILED, e );
        }

        // succeeded
        return UserInformation.ANONYMOUS;
    }

}