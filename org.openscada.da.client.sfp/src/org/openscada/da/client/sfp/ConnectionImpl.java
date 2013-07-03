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

package org.openscada.da.client.sfp;

import java.nio.charset.Charset;
import java.util.EnumSet;

import org.openscada.core.ConnectionInformation;
import org.openscada.core.client.ConnectionState;
import org.openscada.core.client.common.ClientBaseConnection;
import org.openscada.protocol.sfp.Sessions;
import org.openscada.protocol.sfp.messages.Hello;
import org.openscada.protocol.sfp.messages.Welcome;

public class ConnectionImpl extends ClientBaseConnection
{

    public ConnectionImpl ( final ConnectionInformation connectionInformation ) throws Exception
    {
        super ( new HandlerFactory (), new FilterChainBuilder (), connectionInformation );
    }

    @Override
    protected void onConnectionConnected ()
    {
        sendHello ();
    }

    private void sendHello ()
    {
        final Hello message = new Hello ( (short)1, EnumSet.noneOf ( Hello.Features.class ) );
        sendMessage ( message );
    }

    @Override
    protected void handleMessage ( final Object message )
    {
        if ( message instanceof Welcome )
        {
            processWelcome ( (Welcome)message );
        }
    }

    private void processWelcome ( final Welcome message )
    {
        final String charsetName = message.getProperties ().get ( "charset" );
        if ( charsetName != null )
        {
            final Charset charset = Charset.forName ( charsetName );
            Sessions.setCharset ( getSession (), charset );
        }
        switchState ( ConnectionState.BOUND, null );
    }

}
