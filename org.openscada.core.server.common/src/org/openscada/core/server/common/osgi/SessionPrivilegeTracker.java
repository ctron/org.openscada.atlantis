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

package org.openscada.core.server.common.osgi;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.Executor;

import org.openscada.sec.AuthorizationResult;
import org.openscada.sec.UserInformation;
import org.openscada.sec.osgi.AuthorizationRequest;
import org.openscada.sec.osgi.AuthorizationTracker;
import org.openscada.sec.osgi.AuthorizationTracker.Listener;
import org.openscada.sec.osgi.AuthorizationTracker.Monitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SessionPrivilegeTracker
{
    private final static Logger logger = LoggerFactory.getLogger ( SessionPrivilegeTracker.class );

    private final Set<Monitor> monitors = new LinkedHashSet<Monitor> ();

    private final Set<String> granted = new HashSet<String> ();

    private final Executor executor;

    private volatile PrivilegeListener listener;

    public interface PrivilegeListener
    {
        public void privilegesChanged ( Set<String> granted );
    }

    private class ListenerImpl implements Listener
    {
        private final String privilege;

        public ListenerImpl ( final String privilege )
        {
            this.privilege = privilege;
        }

        @Override
        public void resultChanged ( final AuthorizationResult result )
        {
            privilegeChange ( this.privilege, result );
        }
    }

    public SessionPrivilegeTracker ( final Executor executor, final PrivilegeListener listener, final AuthorizationTracker tracker, final Set<String> privileges, final UserInformation userInformation )
    {
        this.executor = executor;
        this.listener = listener;

        logger.debug ( "Start tracking privileges for user: {}", userInformation );

        synchronized ( this )
        {
            for ( final String privilege : privileges )
            {
                logger.debug ( "Start tracking: {}", privilege );
                final Monitor monitor = tracker.createMonitor ( new ListenerImpl ( privilege ), new AuthorizationRequest ( "SESSION", privilege, "PRIV", userInformation, null ) );
                this.monitors.add ( monitor );
            }
        }
    }

    public synchronized void privilegeChange ( final String privilege, final AuthorizationResult result )
    {
        logger.debug ( "Privilege change - privilege: {}, result: {}", privilege, result );
        if ( result.isGranted () )
        {
            this.granted.add ( privilege );
        }
        else
        {
            this.granted.remove ( privilege );
        }
        fireChange ( Collections.unmodifiableSet ( this.granted ) );
    }

    private void fireChange ( final Set<String> granted )
    {
        if ( this.listener == null )
        {
            return;
        }

        this.executor.execute ( new Runnable () {

            @Override
            public void run ()
            {
                final PrivilegeListener listener = SessionPrivilegeTracker.this.listener;
                if ( listener != null )
                {
                    listener.privilegesChanged ( granted );
                }
            }
        } );
    }

    public void dispose ()
    {
        synchronized ( this )
        {
            this.listener = null;
        }

        for ( final Monitor monitor : this.monitors )
        {
            monitor.dispose ();
        }
        this.monitors.clear ();
    }
}
