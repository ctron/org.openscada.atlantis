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

package org.openscada.ae.submitter.net;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.openscada.ae.core.Event;
import org.openscada.ae.core.Submission;
import org.openscada.ae.net.SubmitEventMessage;
import org.openscada.core.ConnectionInformation;
import org.openscada.core.client.ConnectionState;
import org.openscada.core.client.net.ConnectionBase;
import org.openscada.core.client.net.ConnectionInfo;
import org.openscada.net.base.MessageStateListener;
import org.openscada.net.base.data.Message;

public class Connection extends ConnectionBase implements Submission
{
    public static final String PROP_RECONNECT_DELAY = "reconnect-delay";

    public static final String PROP_AUTO_RECONNECT = "auto-reconnect";

    public Connection ( final ConnectionInfo connectionInfo )
    {
        super ( connectionInfo );
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
        // we don't need no binding for submitting an event
        setState ( ConnectionState.BOUND, null );
    }

    public void submitEvent ( final Properties properties, final Event event ) throws Exception
    {
        final SubmitEventMessage message = new SubmitEventMessage ();
        message.setEvent ( event );
        message.setProperties ( properties );

        final SubmissionResult result = new SubmissionResult ();

        synchronized ( result )
        {
            sendMessage ( message.toMessage (), new MessageStateListener () {

                public void messageReply ( final Message message )
                {
                    if ( message.getCommandCode () == Message.CC_ACK )
                    {
                        result.complete ();
                    }
                    else
                    {
                        result.fail ( new Exception ( "received invalid response" ) );
                    }
                }

                public void messageTimedOut ()
                {
                    result.fail ( (Exception)new Exception ( "Message timed out" ).fillInStackTrace () );

                }
            }, Integer.getInteger ( "openscada.ae.message.timeout", 10 * 1000 ) );

            result.wait ();
        }

        if ( !result.isSuccess () )
        {
            throw result.getError ();
        }
    }

    public ConnectionInformation getConnectionInformation ()
    {
        final ConnectionInformation info = new ConnectionInformation ();
        info.setInterface ( "aes" );
        info.setDriver ( "net" );
        info.setTarget ( this.connectionInfo.getHostName () );
        info.setSecondaryTarget ( this.connectionInfo.getPort () );

        final Map<String, String> properties = new HashMap<String, String> ();
        if ( this.connectionInfo.getReconnectDelay () > 0 )
        {
            properties.put ( PROP_AUTO_RECONNECT, "true" );
            properties.put ( PROP_RECONNECT_DELAY, String.format ( "%s", this.connectionInfo.getReconnectDelay () ) );
        }

        info.setProperties ( properties );

        return info;
    }
}
