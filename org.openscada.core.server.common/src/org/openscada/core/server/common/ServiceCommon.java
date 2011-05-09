/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2011 TH4 SYSTEMS GmbH (http://th4-systems.com)
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
import java.util.Properties;

import org.openscada.core.ConnectionInformation;
import org.openscada.core.UnableToCreateSessionException;
import org.openscada.core.server.Service;
import org.openscada.sec.AuthenticationException;
import org.openscada.sec.AuthorizationResult;
import org.openscada.sec.UserInformation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ServiceCommon implements Service
{

    private final static Logger logger = LoggerFactory.getLogger ( ServiceCommon.class );

    protected static final AuthorizationResult DEFAULT_RESULT = AuthorizationResult.create ( org.openscada.sec.StatusCodes.AUTHORIZATION_FAILED, "No authentication provider voted. Rejecting request!" );

    /**
     * Authenticate a user
     * <p>
     * This method simply implements an <em>any</em> authentication which allows
     * access to session with or without user names. No password is checked.
     * </p>
     * <p>
     * This method should be overridden if a different authentication scheme
     * is required.
     * </p>
     * @param properties the session properties used for authentication
     * @param sessionResultProperties the session properties that will be returned to the client.
     * The method may add or remove properties as it likes.
     * @return the user information object or <code>null</code> if it is an
     * anonymous session
     * @throws AuthenticationException if the user was rejected 
     */
    protected UserInformation authenticate ( final Properties properties, final Map<String, String> sessionResultProperties ) throws AuthenticationException
    {
        final String username = properties.getProperty ( ConnectionInformation.PROP_USER );
        if ( username != null )
        {
            return new UserInformation ( username, new String[0] );
        }
        else
        {
            return null;
        }
    }

    /**
     * Wraps the call to {@link #authenticate(Properties)} so that the correct exceptions
     * are thrown for a {@link #createSession(Properties)} call.
     * @param properties the user session properties
     * @return the user information returned by {@link #authenticate(Properties)}
     * @throws UnableToCreateSessionException if a {@link AuthenticationException} was
     * caught by the call to {@link #authenticate(Properties)}.
     * @see #authenticate(Properties)
     */
    protected UserInformation createUserInformation ( final Properties properties, final Map<String, String> sessionResultProperties ) throws UnableToCreateSessionException
    {
        try
        {
            final UserInformation result = authenticate ( properties, sessionResultProperties );

            logger.debug ( "Authenticated as {}", result );

            authorizeSessionPriviliges ( properties, result, sessionResultProperties );

            if ( result != null && result.getRoles () != null )
            {
                for ( final String role : result.getRoles () )
                {
                    sessionResultProperties.put ( "userInformation.roles." + role, "true" );
                }
            }

            return result;
        }
        catch ( final AuthenticationException e )
        {
            throw new UnableToCreateSessionException ( e );
        }
    }

    protected AuthorizationResult authorize ( final String objectType, final String objectId, final String action, final UserInformation userInformation, final Map<String, Object> context )
    {
        return authorize ( objectType, objectId, action, userInformation, context, DEFAULT_RESULT );
    }

    /**
     * Authorize an operation
     * <p>
     * The default implementation grants everything. Override to change according to your needs.
     * </p>
     * @param objectType the type of the object the operation takes place
     * @param objectId the id of the object the operation takes place
     * @param userInformation the user information
     * @param context the context information
     * @param defaultResult the default result that should be returned if no one votes, must not be <code>null</code>
     * @return the authorization result, never returns <code>null</code>
     */
    protected AuthorizationResult authorize ( final String objectType, final String objectId, final String action, final UserInformation userInformation, final Map<String, Object> context, final AuthorizationResult defaultResult )
    {
        logger.debug ( "Requesting authorization - objectType: {}, objectId: {}, action: {}, userInformation: {}, context: {}, defaultResult: {} ... defaulting to GRANTED", new Object[] { objectType, objectId, action, userInformation, context, defaultResult } );
        return AuthorizationResult.GRANTED;
    }

    protected void authorizeSessionPriviliges ( final Properties properties, final UserInformation user, final Map<String, String> sessionResultProperties )
    {
        for ( final Map.Entry<Object, Object> entry : properties.entrySet () )
        {
            if ( entry.getKey () instanceof String && entry.getValue () instanceof String )
            {
                final String key = (String)entry.getKey ();
                final String value = (String)entry.getValue ();
                if ( key.startsWith ( "session.privilege." ) )
                {
                    final String priv = key.substring ( "session.privilege.".length () );
                    if ( authorizeSessionPrivilege ( user, priv, value ) )
                    {
                        sessionResultProperties.put ( key, "true" );
                    }
                }
            }
        }
    }

    protected boolean authorizeSessionPrivilege ( final UserInformation user, final String key, final String value )
    {
        final AuthorizationResult result = authorize ( "SESSION", key, "PRIV", user, null );
        return result.isGranted ();
    }

}
