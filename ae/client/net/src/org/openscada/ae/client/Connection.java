/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006 inavare GmbH (http://inavare.com)
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

package org.openscada.ae.client;

import java.util.Properties;

import org.apache.log4j.Logger;
import org.openscada.ae.net.CreateSessionMessage;
import org.openscada.core.client.net.ConnectionBase;
import org.openscada.core.client.net.ConnectionInfo;
import org.openscada.core.client.net.DisconnectReason;
import org.openscada.core.client.net.OperationTimedOutException;
import org.openscada.net.base.MessageStateListener;
import org.openscada.net.base.data.Message;

public class Connection extends ConnectionBase
{

    public static final String VERSION = "0.1.0";

    private static Logger _log = Logger.getLogger ( Connection.class );

    public Connection ( ConnectionInfo connectionInfo )
    {
        super ( connectionInfo );
    }

    @Override
    protected void init ()
    {
        super.init ();
    }

    private void requestSession ()
    {
        if ( _client == null )
            return;

        Properties props = new Properties();
        props.setProperty ( "client-version", VERSION );

        CreateSessionMessage message = new CreateSessionMessage ();
        message.setProperties ( props );
        _client.getConnection().sendMessage ( message.toMessage (), new MessageStateListener(){

            public void messageReply ( Message message )
            {
                processSessionReply ( message );
            }

            public void messageTimedOut ()
            {
                disconnect (  new OperationTimedOutException().fillInStackTrace () );
            }}, 10 * 1000 );
    }

    private void processSessionReply ( Message message )
    {
        _log.debug ( "Got session reply!" );

        if ( message.getValues ().containsKey ( Message.FIELD_ERROR_INFO ) )
        {
            String errorInfo = message.getValues ().get ( Message.FIELD_ERROR_INFO ).toString ();
            disconnect ( new DisconnectReason ( "Failed to create session: " + errorInfo ) );
        }
        else if ( message.getCommandCode () != Message.CC_ACK )
        {
            disconnect ( new DisconnectReason ( "Received an invalid reply when requesting session" ) );
        }
        else
        {
            setState ( State.BOUND, null );

        }
    }

    @Override
    protected void onConnectionBound ()
    {
    }

    @Override
    protected void onConnectionClosed ()
    {
    }

    @Override
    protected void onConnectionEstablished ()
    {
        requestSession ();
    }
    
   
}
