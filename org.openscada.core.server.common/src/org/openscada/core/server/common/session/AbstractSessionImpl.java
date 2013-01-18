/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2012 TH4 SYSTEMS GmbH (http://th4-systems.com)
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

package org.openscada.core.server.common.session;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.openscada.core.server.Session;
import org.openscada.sec.UserInformation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractSessionImpl implements Session
{
    private final static Logger logger = LoggerFactory.getLogger ( AbstractSessionImpl.class );

    private final UserInformation userInformation;

    private final Map<String, String> properties;

    private volatile Set<String> privileges = new HashSet<String> ();

    private final Set<SessionListener> listeners = new CopyOnWriteArraySet<Session.SessionListener> ();

    private final Set<DisposeListener> disposeListeners = new LinkedHashSet<DisposeListener> ();

    private boolean disposed;

    public interface DisposeListener
    {
        public void disposed ();
    }

    public AbstractSessionImpl ( final UserInformation userInformation, final Map<String, String> properties )
    {
        this.userInformation = userInformation;
        this.properties = new HashMap<String, String> ( properties );
    }

    @Override
    public Map<String, String> getProperties ()
    {
        return Collections.unmodifiableMap ( this.properties );
    }

    public UserInformation getUserInformation ()
    {
        return this.userInformation;
    }

    public void dispose ()
    {
        if ( this.disposed )
        {
            return;
        }

        this.disposed = true;

        for ( final DisposeListener listener : this.disposeListeners )
        {
            try
            {
                listener.disposed ();
            }
            catch ( final Exception e )
            {
                logger.warn ( "Failed to handle dispose", e );
            }
        }
    }

    public void addDisposeListener ( final DisposeListener disposeListener )
    {
        this.disposeListeners.add ( disposeListener );
    }

    public void removeDisposeListener ( final DisposeListener disposeListener )
    {
        this.disposeListeners.remove ( disposeListener );
    }

    @Override
    public void addSessionListener ( final SessionListener listener )
    {
        if ( this.listeners.add ( listener ) )
        {
            listener.privilegeChange ();
        }
    }

    @Override
    public void removeSessionListener ( final SessionListener listener )
    {
        this.listeners.remove ( listener );
    }

    protected void firePrivilegeChange ()
    {
        for ( final SessionListener listener : this.listeners )
        {
            listener.privilegeChange ();
        }
    }

    public void setPrivileges ( final Set<String> privileges )
    {
        this.privileges = privileges;
        firePrivilegeChange ();
    }

    @Override
    public Set<String> getPrivileges ()
    {
        return this.privileges;
    }
}
