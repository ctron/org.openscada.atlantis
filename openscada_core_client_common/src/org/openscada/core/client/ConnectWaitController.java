/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2008 inavare GmbH (http://inavare.com)
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

public class ConnectWaitController implements ConnectionStateListener
{
    private static Logger logger = Logger.getLogger ( ConnectWaitController.class );

    private Connection _connection;

    private ConnectionState _state = null;

    private Throwable _error = null;

    public ConnectWaitController ( Connection connection )
    {
        super ();
        _connection = connection;
    }

    public synchronized void connect () throws Exception
    {
        connect ( 0 );
    }

    public synchronized void connect ( int timeout ) throws Exception
    {
        try
        {
            _connection.addConnectionStateListener ( this );
            _connection.connect ();
            switch ( _state )
            {
            case BOUND:
                return;
            case CLOSED:
                if ( _error == null )
                {
                    return;
                }
                else
                {
                    throw new Exception ( _error );
                }
            }

            wait ( timeout );
            if ( _error != null )
            {
                throw new Exception ( _error );
            }
        }
        finally
        {
            _connection.removeConnectionStateListener ( this );
        }
    }

    public synchronized void stateChange ( Connection connection, ConnectionState state, Throwable error )
    {
        logger.info ( String.format ( "New connection state: %s", state ) );
        _state = state;
        _error = error;

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
