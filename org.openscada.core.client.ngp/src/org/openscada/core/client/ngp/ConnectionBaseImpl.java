/*
 * This file is part of the openSCADA project
 * Copyright (C) 2011-2012 TH4 SYSTEMS GmbH (http://th4-systems.com)
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

package org.openscada.core.client.ngp;

import org.openscada.core.ConnectionInformation;
import org.openscada.core.client.ConnectionState;
import org.openscada.core.data.message.CreateSession;
import org.openscada.core.data.message.SessionAccepted;
import org.openscada.core.data.message.SessionRejected;
import org.openscada.protocol.ngp.client.ClientBaseConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConnectionBaseImpl extends ClientBaseConnection
{

    private final static Logger logger = LoggerFactory.getLogger ( ConnectionBaseImpl.class );

    private long sequenceNumber = 0;

    public ConnectionBaseImpl ( final ConnectionInformation connectionInformation ) throws Exception
    {
        super ( connectionInformation );
    }

    @Override
    protected void onConnectionConnected ()
    {
        // send create session request
        logger.info ( "Requesting new session" );

        sendMessage ( new CreateSession ( this.connectionInformation.getProperties () ) );
    }

    @Override
    protected synchronized void handleMessage ( final Object message )
    {
        if ( message instanceof SessionAccepted )
        {
            setSessionProperties ( ( (SessionAccepted)message ).getProperties () );
            switchState ( ConnectionState.BOUND, null );
        }
        else if ( message instanceof SessionRejected )
        {
            // failure
            performDisconnected ( new IllegalStateException ( String.format ( "Failed to create session. Reply: %s", ( (SessionRejected)message ).getErrorReason () ) ).fillInStackTrace () );
        }
    }

    protected synchronized long nextRequestNumber ()
    {
        return ++this.sequenceNumber;
    }

}
