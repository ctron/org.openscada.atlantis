/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2010 inavare GmbH (http://inavare.com)
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

package org.openscada.core.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The connect wait controller makes the {@link Connection#connect()} call a synchronous operation 
 * @author Jens Reimann
 *
 */
public class ConnectWaitController implements ConnectionStateListener
{
    private final static Logger logger = LoggerFactory.getLogger ( ConnectWaitController.class );

    private final Connection connection;

    private ConnectionState state;

    private Throwable error;

    public ConnectWaitController ( final Connection connection )
    {
        super ();
        this.connection = connection;
    }

    public synchronized void connect () throws Exception
    {
        connect ( 0 );
    }

    public synchronized void connect ( final int timeout ) throws Exception
    {
        try
        {
            this.state = this.connection.getState ();
            this.connection.addConnectionStateListener ( this );
            this.connection.connect ();
            switch ( this.state )
            {
            case BOUND:
                return;
            case CLOSED:
                if ( this.error == null )
                {
                    return;
                }
                else
                {
                    throw new Exception ( this.error );
                }
            }

            wait ( timeout );
            if ( this.error != null )
            {
                throw new Exception ( this.error );
            }
        }
        finally
        {
            this.connection.removeConnectionStateListener ( this );
        }
    }

    public synchronized void stateChange ( final Connection connection, final ConnectionState state, final Throwable error )
    {
        logger.info ( String.format ( "New connection state: %s", state ) );
        this.state = state;
        this.error = error;

        switch ( state )
        {
        case BOUND:
            notifyAll ();
            break;
        case CLOSED:
            notifyAll ();
            break;
        }
    }
}
