/*
 * This file is part of the openSCADA project
 * 
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

package org.openscada.sec.osgi.manager;

import org.eclipse.scada.sec.AuthorizationReply;
import org.eclipse.scada.sec.AuthorizationRequest;
import org.eclipse.scada.sec.authz.AuthorizationContext;
import org.eclipse.scada.sec.osgi.AuthorizationTracker.Listener;
import org.eclipse.scada.sec.osgi.AuthorizationTracker.Monitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MonitorImpl implements Monitor
{

    private final static Logger logger = LoggerFactory.getLogger ( MonitorImpl.class );

    private final AuthorizationManagerImpl authorizationManagerImpl;

    private volatile Listener listener;

    private AuthorizationReply lastResult;

    private final AuthorizationContext context;

    public MonitorImpl ( final AuthorizationManagerImpl authorizationManagerImpl, final Listener listener, final AuthorizationRequest request )
    {
        this.authorizationManagerImpl = authorizationManagerImpl;
        this.listener = listener;

        this.context = new AuthorizationContext ();
        this.context.setRequest ( request );
    }

    @Override
    public void dispose ()
    {
        logger.debug ( "Dispose monitor" );
        this.listener = null;
        this.authorizationManagerImpl.disposeMonitor ( this );
    }

    public void setResult ( final AuthorizationReply result )
    {
        final Listener listener = this.listener;
        if ( listener != null )
        {

            if ( this.lastResult != null && this.lastResult.equals ( result ) )
            {
                return;
            }

            this.lastResult = result;

            logger.debug ( "Updating result - {}", result );

            try
            {
                listener.resultChanged ( result );
            }
            catch ( final Exception e )
            {
                logger.warn ( "Failed to notify listener", e );
            }
        }
    }

    public AuthorizationContext getContext ()
    {
        return this.context;
    }
}