/*
 * This file is part of the OpenSCADA project
 * 
 * Copyright (C) 2006-2012 TH4 SYSTEMS GmbH (http://th4-systems.com)
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

import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.openscada.core.ConnectionInformation;
import org.openscada.core.UnableToCreateSessionException;
import org.openscada.core.server.Service;
import org.openscada.core.server.Session;
import org.openscada.core.server.common.session.AbstractSessionImpl;
import org.openscada.sec.AuthenticationException;
import org.openscada.sec.AuthorizationResult;
import org.openscada.sec.PermissionDeniedException;
import org.openscada.sec.UserInformation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ServiceCommon<S extends Session, SI extends AbstractSessionImpl> implements Service<S>
{

    private final static Logger logger = LoggerFactory.getLogger ( ServiceCommon.class );

    protected static final AuthorizationResult DEFAULT_RESULT = AuthorizationResult.create ( org.openscada.sec.StatusCodes.AUTHORIZATION_FAILED, Messages.getString ( "ServiceCommon.DefaultMessage" ) ); //$NON-NLS-1$

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
     * @param properties
     *            the session properties used for authentication
     * @param sessionResultProperties
     *            the session properties that will be returned to the client.
     *            The method may add or remove properties as it likes.
     * @return the user information object or <code>null</code> if it is an
     *         anonymous session
     * @throws AuthenticationException
     *             if the user was rejected
     */
    protected UserInformation authenticate ( final Properties properties, final Map<String, String> sessionResultProperties ) throws AuthenticationException
    {
        final String username = properties.getProperty ( ConnectionInformation.PROP_USER );
        final String password = properties.getProperty ( ConnectionInformation.PROP_PASSWORD );

        final String plainPassword = System.getProperty ( "org.openscada.core.server.common.ServiceCommon.password" );

        if ( plainPassword == null || plainPassword.isEmpty () )
        {
            if ( username != null )
            {
                return new UserInformation ( username, password, new String[0] );
            }
            else
            {
                return null;
            }
        }
        else
        {
            if ( username == null || password == null || !plainPassword.equals ( password ) )
            {
                logger.debug ( "Password requested using system properties. But not or wrong provided." );
                throw new AuthenticationException ( org.openscada.sec.StatusCodes.INVALID_USER_OR_PASSWORD );
            }
            else
            {
                return new UserInformation ( username, password, new String[0] );
            }
        }
    }

    protected Set<String> extractPrivileges ( final Properties properties )
    {
        final Set<String> result = new HashSet<String> ();

        for ( final Map.Entry<Object, Object> entry : properties.entrySet () )
        {
            if ( entry.getKey () instanceof String && entry.getValue () instanceof String )
            {
                final String key = (String)entry.getKey ();
                if ( key.startsWith ( "session.privilege." ) ) //$NON-NLS-1$
                {
                    final String priv = key.substring ( "session.privilege.".length () ); //$NON-NLS-1$
                    result.add ( priv );
                }
            }
        }

        return result;
    }

    /**
     * Wraps the call to {@link #authenticate(Properties)} so that the correct
     * exceptions are thrown for a {@link #createSession(Properties)} call.
     * 
     * @param properties
     *            the user session properties
     * @return the user information returned by
     *         {@link #authenticate(Properties)}
     * @throws UnableToCreateSessionException
     *             if a {@link AuthenticationException} was caught by the call
     *             to {@link #authenticate(Properties)}.
     * @see #authenticate(Properties)
     */
    protected UserInformation createUserInformation ( final Properties properties, final Map<String, String> sessionResultProperties ) throws UnableToCreateSessionException
    {
        try
        {
            // check who the user is
            final UserInformation result = authenticate ( properties, sessionResultProperties );

            logger.debug ( "Authenticated as {}", result ); //$NON-NLS-1$

            // checking if the user is allowed to log on
            final AuthorizationResult authResult = authorize ( "SESSION", extractUserName ( result ), "CONNECT", result, null );
            if ( !authResult.isGranted () )
            {
                throw new UnableToCreateSessionException ( String.format ( "The user is not allowed to log on: %s", authResult.toString () ) );
            }

            if ( result != null && result.getRoles () != null )
            {
                for ( final String role : result.getRoles () )
                {
                    sessionResultProperties.put ( "userInformation.roles." + role, "true" ); //$NON-NLS-1$ //$NON-NLS-2$
                }
            }

            return result;
        }
        catch ( final AuthenticationException e )
        {
            throw new UnableToCreateSessionException ( e );
        }
    }

    /**
     * Get the name of the user, if the user is known
     * 
     * @param userInformation
     *            the user information from which to extract the name
     * @return the user name or <code>null</code> if the name is unknown
     */
    private String extractUserName ( final UserInformation userInformation )
    {
        if ( userInformation == null )
        {
            return null;
        }
        return userInformation.getName ();
    }

    protected AuthorizationResult authorize ( final String objectType, final String objectId, final String action, final UserInformation userInformation, final Map<String, Object> context )
    {
        return authorize ( objectType, objectId, action, userInformation, context, DEFAULT_RESULT );
    }

    /**
     * Authorize an operation
     * <p>
     * The default implementation grants everything. Override to change
     * according to your needs.
     * </p>
     * 
     * @param objectType
     *            the type of the object the operation takes place
     * @param objectId
     *            the id of the object the operation takes place
     * @param userInformation
     *            the user information
     * @param context
     *            the context information
     * @param defaultResult
     *            the default result that should be returned if no one votes,
     *            must not be <code>null</code>
     * @return the authorization result, never returns <code>null</code>
     */
    protected AuthorizationResult authorize ( final String objectType, final String objectId, final String action, final UserInformation userInformation, final Map<String, Object> context, final AuthorizationResult defaultResult )
    {
        logger.debug ( "Requesting authorization - objectType: {}, objectId: {}, action: {}, userInformation: {}, context: {}, defaultResult: {} ... defaulting to GRANTED", new Object[] { objectType, objectId, action, userInformation, context, defaultResult } ); //$NON-NLS-1$
        return AuthorizationResult.GRANTED;
    }

    protected UserInformation makeEffectiveUserInformation ( final AbstractSessionImpl session, final UserInformation userInformation ) throws PermissionDeniedException
    {
        UserInformation sessionInformation = session.getUserInformation ();
        if ( sessionInformation == null )
        {
            logger.debug ( "Session has no user information. Using anonymous" );
            sessionInformation = UserInformation.ANONYMOUS;
        }

        if ( userInformation == null )
        {
            logger.debug ( "No user information provided. Using session information ({}).", sessionInformation );
            return sessionInformation;
        }

        final String proxyUser = userInformation.getName ();
        if ( proxyUser == null )
        {
            logger.info ( "Proxy user is null" );
            return sessionInformation;
        }

        // check if user differs
        if ( !proxyUser.equals ( sessionInformation.getName () ) )
        {
            logger.debug ( "Trying to set proxy user: {}", proxyUser );

            // try to set proxy user
            final AuthorizationResult result = authorize ( "SESSION", proxyUser, "PROXY_USER", session.getUserInformation (), null );
            if ( !result.isGranted () )
            {
                logger.info ( "Proxy user is not allowed" );
                // not allowed to use proxy user
                throw new PermissionDeniedException ( result.getErrorCode (), result.getMessage () );
            }

            return new UserInformation ( proxyUser, userInformation.getPassword (), sessionInformation.getRoles () );
        }
        else
        {
            logger.debug ( "Session user and proxy user match ... using session user" );
            // session is already is proxy user
            return sessionInformation;
        }
    }

}
