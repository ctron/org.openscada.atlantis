/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2011-2012 TH4 SYSTEMS GmbH (http://th4-systems.com)
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

package org.openscada.core.server.common.osgi;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.Executor;

import org.openscada.ae.sec.AuthorizationHelper;
import org.openscada.core.ConnectionInformation;
import org.openscada.core.InvalidSessionException;
import org.openscada.core.UnableToCreateSessionException;
import org.openscada.core.server.Session;
import org.openscada.core.server.common.ServiceCommon;
import org.openscada.core.server.common.session.AbstractSessionImpl;
import org.openscada.core.server.common.session.AbstractSessionImpl.DisposeListener;
import org.openscada.core.server.common.session.PrivilegeListenerImpl;
import org.openscada.sec.AuthenticationException;
import org.openscada.sec.AuthorizationResult;
import org.openscada.sec.UserInformation;
import org.openscada.sec.osgi.AuthenticationHelper;
import org.openscada.sec.osgi.AuthorizationTracker;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;

public abstract class AbstractServiceImpl<S extends Session, SI extends AbstractSessionImpl> extends ServiceCommon<S, SI>
{

    private final AuthenticationHelper authenticationManager;

    private final AuthorizationHelper authorizationHelper;

    protected final Set<SI> sessions = new CopyOnWriteArraySet<SI> ();

    private final AuthorizationTracker authorizationTracker;

    private final Executor executor;

    public AbstractServiceImpl ( final BundleContext context, final Executor executor ) throws InvalidSyntaxException
    {
        this.executor = executor;
        this.authenticationManager = new AuthenticationHelper ( context );
        this.authorizationHelper = new AuthorizationHelper ( context );
        this.authorizationTracker = new AuthorizationTracker ( context, executor );
    }

    @Override
    protected UserInformation authenticate ( final Properties properties, final Map<String, String> sessionResultProperties ) throws AuthenticationException
    {
        return this.authenticationManager.authenticate ( properties.getProperty ( ConnectionInformation.PROP_USER ), properties.getProperty ( ConnectionInformation.PROP_PASSWORD ) );
    }

    @Override
    protected AuthorizationResult authorize ( final String objectType, final String objectId, final String action, final UserInformation userInformation, final Map<String, Object> context, final AuthorizationResult defaultResult )
    {
        return this.authorizationHelper.authorize ( objectType, objectId, action, userInformation, context, defaultResult );
    }

    @Override
    public void start () throws Exception
    {
        this.authenticationManager.open ();
        this.authorizationHelper.open ();
        this.authorizationTracker.open ();
    }

    @Override
    public void stop () throws Exception
    {
        this.authenticationManager.close ();
        this.authorizationHelper.close ();
        this.authorizationTracker.close ();

        // close sessions
        for ( final SI session : this.sessions )
        {
            session.dispose ();
        }
    }

    @SuppressWarnings ( "unchecked" )
    @Override
    public void closeSession ( final S session ) throws InvalidSessionException
    {
        SI sessionImpl = null;
        synchronized ( this )
        {
            if ( this.sessions.remove ( session ) )
            {
                sessionImpl = (SI)session;
            }
        }

        if ( sessionImpl != null )
        {
            // now dispose
            sessionImpl.dispose ();

            handleSessionClosed ( sessionImpl );
        }
    }

    protected void handleSessionClosed ( final SI session )
    {
    }

    @SuppressWarnings ( "unchecked" )
    @Override
    public synchronized S createSession ( final Properties properties ) throws UnableToCreateSessionException
    {
        final Map<String, String> sessionProperties = new HashMap<String, String> ();
        final UserInformation user = createUserInformation ( properties, sessionProperties );

        final SI session = createSessionInstance ( user, sessionProperties );

        final Set<String> privileges = extractPrivileges ( properties );
        final SessionPrivilegeTracker privTracker = new SessionPrivilegeTracker ( this.executor, new PrivilegeListenerImpl ( session ), this.authorizationTracker, privileges, user );

        session.addDisposeListener ( new DisposeListener () {

            @Override
            public void disposed ()
            {
                privTracker.dispose ();
            }
        } );

        this.sessions.add ( session );

        handleSessionCreated ( session );

        return (S)session;
    }

    protected void handleSessionCreated ( final SI session )
    {
    }

    protected abstract SI createSessionInstance ( UserInformation user, Map<String, String> sessionProperties );

    protected synchronized SI validateSession ( final S session, final Class<SI> sessionImplClazz ) throws InvalidSessionException
    {
        if ( !this.sessions.contains ( session ) )
        {
            throw new InvalidSessionException ();
        }

        try
        {
            return sessionImplClazz.cast ( session );
        }
        catch ( final ClassCastException e )
        {
            throw new InvalidSessionException ();
        }
    }

}
