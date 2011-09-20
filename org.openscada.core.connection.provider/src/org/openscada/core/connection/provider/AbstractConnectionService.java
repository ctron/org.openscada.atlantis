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

package org.openscada.core.connection.provider;

import org.openscada.core.client.AutoReconnectController;
import org.openscada.core.client.Connection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractConnectionService implements org.openscada.core.connection.provider.ConnectionService
{
    private static final Logger logger = LoggerFactory.getLogger ( AbstractConnectionService.class );

    private final Connection connection;

    private final AutoReconnectController controller;

    public AbstractConnectionService ( final Connection connection, final Integer autoReconnectController )
    {
        super ();
        this.connection = connection;

        if ( autoReconnectController != null )
        {
            this.controller = new AutoReconnectController ( connection, autoReconnectController );
        }
        else
        {
            this.controller = null;
        }
    }

    @Override
    public void dispose ()
    {
        logger.info ( "Disposing: {}", this.connection );
        disconnect ();

        if ( this.controller != null )
        {
            this.controller.dispose ();
        }
    }

    @Override
    public AutoReconnectController getAutoReconnectController ()
    {
        return this.controller;
    }

    @Override
    public Connection getConnection ()
    {
        return this.connection;
    }

    @Override
    public void connect ()
    {
        if ( this.controller != null )
        {
            this.controller.connect ();
        }
        else
        {
            this.connection.connect ();
        }
    }

    @Override
    public void disconnect ()
    {
        if ( this.controller != null )
        {
            this.controller.disconnect ();
        }
        else
        {
            this.connection.disconnect ();
        }
    }

}