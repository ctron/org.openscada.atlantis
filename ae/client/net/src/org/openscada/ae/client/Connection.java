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
import java.util.Set;

import org.apache.log4j.Logger;
import org.openscada.ae.client.operations.ListOperationController;
import org.openscada.ae.core.QueryDescription;
import org.openscada.ae.net.CreateSessionMessage;
import org.openscada.ae.net.ListReplyMessage;
import org.openscada.core.client.net.ConnectionBase;
import org.openscada.core.client.net.ConnectionInfo;
import org.openscada.core.client.net.DisconnectReason;
import org.openscada.core.client.net.OperationTimedOutException;
import org.openscada.core.client.net.operations.OperationException;
import org.openscada.net.base.LongRunningOperation;
import org.openscada.net.base.MessageStateListener;
import org.openscada.net.base.LongRunningController.Listener;
import org.openscada.net.base.data.Message;
import org.openscada.net.io.IOProcessor;

public class Connection extends ConnectionBase
{

    public static final String VERSION = "0.1.0";

    private static Logger _log = Logger.getLogger ( Connection.class );
    
    private ListOperationController _listOperationController = null;

    public Connection ( ConnectionInfo connectionInfo )
    {
        super ( connectionInfo );
        
        _listOperationController = new ListOperationController ( _client );
        _listOperationController.register ( _client.getMessageProcessor () );
    }
    
    public Connection ( ConnectionInfo connectionInfo, IOProcessor processor )
    {
        super ( processor, connectionInfo );
        
        _listOperationController = new ListOperationController ( _client );
        _listOperationController.register ( _client.getMessageProcessor () );
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
    
    public LongRunningOperation startList ( Listener listener )
    {
        return _listOperationController.start ( listener );
    }
    
    public Set<QueryDescription> completeList ( LongRunningOperation op ) throws OperationException
    {
        if ( op.getError () != null )
        {
            throw new OperationException ( op.getError () );
        }
        if ( op.getReply () != null )
        {
            Message reply = op.getReply ();
            try
            {
                ListReplyMessage listReplyMessage = ListReplyMessage.fromMessage ( reply );
                return listReplyMessage.getQueries ();
            }
            catch ( Exception e )
            {
                throw new OperationException ( e );
            }
        }
        return null;
    }
    
    public Set<QueryDescription> list () throws InterruptedException, OperationException
    {
        LongRunningOperation op = startList ( null );
        op.waitForCompletion ();
        return completeList ( op );
    }
   
}
