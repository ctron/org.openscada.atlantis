/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2009 inavare GmbH (http://inavare.com)
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

package org.openscada.core.client;

import org.apache.log4j.Logger;

/**
 * The connect wait controller makes the {@link Connection#connect()} call a synchronous operation 
 * @author Jens Reimann
 *
 */
public class ConnectWaitController implements ConnectionStateListener
{
    private static Logger logger = Logger.getLogger ( ConnectWaitController.class );

    private final Connection connection;

    private ConnectionState state = null;

    private Throwable error = null;

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
