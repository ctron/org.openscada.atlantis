/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2010 TH4 SYSTEMS GmbH (http://inavare.com)
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

package org.openscada.net.base;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.openscada.net.base.data.LongValue;
import org.openscada.net.base.data.Message;
import org.openscada.net.mina.Messenger;
import org.openscada.utils.exec.LongRunningListener;
import org.openscada.utils.exec.LongRunningState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LongRunningController implements MessageListener
{

    private final static Logger logger = LoggerFactory.getLogger ( LongRunningController.class );

    private final Set<Integer> _commandCodes = new HashSet<Integer> ();

    private Messenger _connectionHandler = null;

    private final Map<Long, LongRunningOperation> opMap = new HashMap<Long, LongRunningOperation> ();

    public LongRunningController ( final Messenger connectionHandler, final int commandCode )
    {
        this._connectionHandler = connectionHandler;
        this._commandCodes.add ( commandCode );
    }

    public LongRunningController ( final Messenger connectionHandler, final Set<Integer> commandCodes )
    {
        this._connectionHandler = connectionHandler;
        this._commandCodes.addAll ( commandCodes );
    }

    public LongRunningController ( final Messenger connectionHandler, final Integer... commandCodes )
    {
        this._connectionHandler = connectionHandler;
        this._commandCodes.addAll ( Arrays.asList ( commandCodes ) );
    }

    public void register ()
    {
        for ( final Integer commandCode : this._commandCodes )
        {
            this._connectionHandler.setHandler ( commandCode, this );
        }
    }

    public void unregister ()
    {
        for ( final Integer commandCode : this._commandCodes )
        {
            this._connectionHandler.unsetHandler ( commandCode );
        }
    }

    synchronized public LongRunningOperation start ( final Message message, final LongRunningListener listener )
    {
        if ( message == null )
        {
            return null;
        }

        final LongRunningOperation op = new LongRunningOperation ( this, listener );

        this._connectionHandler.sendMessage ( message, new MessageStateListener () {

            public void messageReply ( final Message message )
            {
                if ( message.getValues ().containsKey ( "id" ) )
                {
                    if ( message.getValues ().get ( "id" ) instanceof LongValue )
                    {
                        final long id = ( (LongValue)message.getValues ().get ( "id" ) ).getValue ();
                        op.granted ( id );
                        assignOperation ( id, op );
                        return;
                    }
                }
                else if ( message.getValues ().containsKey ( Message.FIELD_ERROR_INFO ) )
                {
                    final String errorInfo = message.getValues ().get ( Message.FIELD_ERROR_INFO ).toString ();
                    op.fail ( new InvalidMessageReplyException ( errorInfo ).fillInStackTrace () );
                    return;
                }
                // else
                op.fail ( new InvalidMessageReplyException ( "Message did not contain 'id' field" ).fillInStackTrace () );
            }

            public void messageTimedOut ()
            {
                op.fail ( new MessageTimeoutException ().fillInStackTrace () );
            }
        } );

        if ( listener != null )
        {
            listener.stateChanged ( op, LongRunningState.REQUESTED, null );
        }

        return op;
    }

    private synchronized void assignOperation ( final long id, final LongRunningOperation op )
    {
        this.opMap.put ( id, op );
    }

    public void messageReceived ( final Message message )
    {
        long id = 0;

        if ( message.getValues ().containsKey ( "id" ) )
        {
            if ( message.getValues ().get ( "id" ) instanceof LongValue )
            {
                id = ( (LongValue)message.getValues ().get ( "id" ) ).getValue ();
            }
        }

        logger.debug ( "Received long-op reply with id {}", id );

        if ( id != 0 )
        {
            LongRunningOperation op = null;
            synchronized ( this.opMap )
            {
                op = this.opMap.get ( id );
                this.opMap.remove ( id );
            }

            if ( op != null )
            {
                op.result ( message );
            }
            else
            {
                logger.warn ( "Received long-op message for unregistered operation" );
            }
        }
    }
}
