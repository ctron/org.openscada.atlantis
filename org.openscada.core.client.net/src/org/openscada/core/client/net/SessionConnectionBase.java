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

package org.openscada.core.client.net;

import java.util.Properties;

import org.apache.log4j.Logger;
import org.openscada.core.ConnectionInformation;
import org.openscada.core.net.MessageHelper;
import org.openscada.net.base.MessageStateListener;
import org.openscada.net.base.data.Message;

public abstract class SessionConnectionBase extends ConnectionBase
{
    public static final String SESSION_CLIENT_VERSION = "client-version";

    private static Logger logger = Logger.getLogger ( SessionConnectionBase.class );

    private final ConnectionInformation connectionInformation;

    public SessionConnectionBase ( final ConnectionInformation connectionInformation )
    {
        super ( connectionInformation );
        this.connectionInformation = connectionInformation;
    }

    public abstract String getRequiredVersion ();

    @Override
    protected void onConnectionEstablished ()
    {
        requestSession ();
    }

    protected void requestSession ()
    {
        final Properties props = new Properties ();
        props.putAll ( this.connectionInformation.getProperties () );

        props.setProperty ( SESSION_CLIENT_VERSION, getRequiredVersion () );

        final String username = getConnectionInformation ().getProperties ().get ( ConnectionInformation.PROP_USER );
        final String password = getConnectionInformation ().getProperties ().get ( ConnectionInformation.PROP_PASSWORD );

        if ( username != null && password != null )
        {
            props.put ( ConnectionInformation.PROP_USER, username );
            props.put ( ConnectionInformation.PROP_PASSWORD, password );
        }
        else if ( username != null )
        {
            props.put ( ConnectionInformation.PROP_USER, username );
        }

        this.messenger.sendMessage ( MessageHelper.createSession ( props ), new MessageStateListener () {

            public void messageReply ( final Message message )
            {
                processSessionReply ( message );
            }

            public void messageTimedOut ()
            {
                disconnected ( new OperationTimedOutException ().fillInStackTrace () );
            }
        }, getMessageTimeout () );

    }

    protected void processSessionReply ( final Message message )
    {
        logger.debug ( "Got session reply!" );

        if ( message.getValues ().containsKey ( Message.FIELD_ERROR_INFO ) )
        {
            final String errorInfo = message.getValues ().get ( Message.FIELD_ERROR_INFO ).toString ();
            disconnected ( new DisconnectReason ( "Failed to create session: " + errorInfo ) );
        }
        else if ( message.getCommandCode () != Message.CC_ACK )
        {
            disconnected ( new DisconnectReason ( "Received an invalid reply when requesting session" ) );
        }
        else
        {
            final Properties properties = new Properties ();
            MessageHelper.getProperties ( properties, message.getValues ().get ( "properties" ) );
            setBound ( properties );
        }
    }
}
