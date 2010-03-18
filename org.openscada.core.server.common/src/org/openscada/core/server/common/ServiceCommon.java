/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2010 inavare GmbH (http://inavare.com)
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package org.openscada.core.server.common;

import java.util.Properties;

import org.openscada.core.ConnectionInformation;
import org.openscada.core.UnableToCreateSessionException;
import org.openscada.core.server.Service;
import org.openscada.sec.AuthenticationException;
import org.openscada.sec.UserInformation;

public abstract class ServiceCommon implements Service
{

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
     * @return the user information object or <code>null</code> if it is an
     * anonymous session
     * @throws AuthenticationException if the user was rejected 
     */
    protected UserInformation authenticate ( final Properties properties ) throws AuthenticationException
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
    protected UserInformation createUserInformation ( final Properties properties ) throws UnableToCreateSessionException
    {
        try
        {
            return authenticate ( properties );
        }
        catch ( final AuthenticationException e )
        {
            throw new UnableToCreateSessionException ( e );
        }
    }

}
