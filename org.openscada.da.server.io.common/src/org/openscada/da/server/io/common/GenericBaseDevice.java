/*
 * This file is part of the openSCADA project
 * 
 * Copyright (C) 2006-2010 TH4 SYSTEMS GmbH (http://th4-systems.com)
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

package org.openscada.da.server.io.common;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GenericBaseDevice extends IoHandlerAdapter
{

    private static final Logger logger = LoggerFactory.getLogger ( GenericBaseDevice.class );

    protected final Set<ConnectionListener> connectionListeners = new CopyOnWriteArraySet<ConnectionListener> ();

    public GenericBaseDevice ()
    {
        super ();
    }

    public void addConnectionListener ( final ConnectionListener listener )
    {
        this.connectionListeners.add ( listener );
    }

    public void removeConnectionListener ( final ConnectionListener listener )
    {
        this.connectionListeners.remove ( listener );
    }

    protected void fireConnectionFailed ( final Throwable e )
    {
        logger.info ( "Connect failed", e );

        for ( final ConnectionListener listener : this.connectionListeners )
        {
            listener.connectFailed ( e );
        }
    }

    protected void fireDisconnected ( final Throwable e )
    {
        logger.info ( "Disconnected", e );

        for ( final ConnectionListener listener : this.connectionListeners )
        {
            listener.connectFailed ( e );
        }
    }

    protected void fireConnected ()
    {
        for ( final ConnectionListener listener : this.connectionListeners )
        {
            listener.opened ();
        }
    }

}