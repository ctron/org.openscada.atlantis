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

package org.openscada.core.server.ngp;

import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.mina.core.session.IoSession;
import org.openscada.core.InvalidSessionException;
import org.openscada.core.UnableToCreateSessionException;
import org.openscada.core.data.message.CreateSession;
import org.openscada.core.data.message.SessionAccepted;
import org.openscada.core.data.message.SessionPrivilegesChanged;
import org.openscada.core.data.message.SessionRejected;
import org.openscada.core.ngp.Features;
import org.openscada.core.server.Service;
import org.openscada.core.server.Session;
import org.openscada.core.server.Session.SessionListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ServiceServerConnection<T extends Session, S extends Service<T>> extends ServerConnection
{
    private final static Logger logger = LoggerFactory.getLogger ( ServiceServerConnection.class );

    protected final S service;

    protected T session;

    private boolean enablePrivs;

    public ServiceServerConnection ( final IoSession session, final S service )
    {
        super ( session );
        this.service = service;
    }

    @Override
    public void messageReceived ( final Object message ) throws Exception
    {
        logger.trace ( "Received message : {}", message );

        if ( message instanceof CreateSession )
        {
            handleCreateSession ( (CreateSession)message );
        }
    }

    protected synchronized void handleCreateSession ( final CreateSession message )
    {
        try
        {
            this.enablePrivs = message.getProperties ().containsKey ( Features.FEATURE_SESSION_PRIVILEGES );
            logger.debug ( "Enable privileges: {}", this.enablePrivs );

            performCreateSession ( message.getProperties () );
            sendMessage ( makeSuccessMessage ( this.session.getProperties () ) );
            this.session.addSessionListener ( new SessionListener () {

                @Override
                public void privilegeChange ()
                {
                    handlePrivilegeChange ();
                }
            } );
        }
        catch ( final UnableToCreateSessionException e )
        {
            sendMessage ( makeRejectMessage ( e ) );
            // FIXME: allow re-try
            requestClose ( false );
        }
        catch ( final Exception e )
        {
            sendMessage ( makeRejectMessage ( e ) );
            requestClose ( false );
        }
    }

    protected SessionPrivilegesChanged makePrivilegeChangeMessage ( final Set<String> privileges )
    {
        return new SessionPrivilegesChanged ( privileges );
    }

    private SessionAccepted makeSuccessMessage ( final Map<String, String> properties )
    {
        return new SessionAccepted ( properties );
    }

    private SessionRejected makeRejectMessage ( final Exception e )
    {
        return new SessionRejected ( e.getMessage () );
    }

    private void performCreateSession ( final Map<String, String> properties ) throws UnableToCreateSessionException
    {
        if ( this.session != null )
        {
            throw new IllegalStateException ( "A session was already created" );
        }

        final Properties p = new Properties ();
        p.putAll ( properties );
        this.session = createSession ( p );
    }

    protected T createSession ( final Properties properties ) throws UnableToCreateSessionException
    {
        return this.service.createSession ( properties );
    }

    @Override
    public void dispose ()
    {
        T session;

        synchronized ( this )
        {
            session = this.session;
            this.session = null;
        }

        if ( session != null )
        {
            try
            {
                this.service.closeSession ( session );
            }
            catch ( final InvalidSessionException e )
            {
                logger.warn ( "Failed to close session", e );
            }
        }
        super.dispose ();
    }

    private void handlePrivilegeChange ()
    {
        if ( this.enablePrivs )
        {
            sendMessage ( makePrivilegeChangeMessage ( ServiceServerConnection.this.session.getPrivileges () ) );
        }
    }
}