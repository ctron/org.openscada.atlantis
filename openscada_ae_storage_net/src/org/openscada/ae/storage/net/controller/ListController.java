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

package org.openscada.ae.storage.net.controller;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.openscada.ae.core.ListOperationListener;
import org.openscada.ae.core.QueryDescription;
import org.openscada.ae.core.Session;
import org.openscada.ae.core.Storage;
import org.openscada.ae.net.ListReplyMessage;
import org.openscada.ae.net.Messages;
import org.openscada.core.InvalidSessionException;
import org.openscada.core.net.OperationController;
import org.openscada.net.base.ConnectionHandlerBase;
import org.openscada.net.base.data.LongValue;
import org.openscada.net.base.data.Message;
import org.openscada.net.base.data.StringValue;

public class ListController extends OperationController implements ListOperationListener
{
    private Storage _storage = null;
    private Long _id = null;
    private Session _session = null;
    private ConnectionHandlerBase _connection = null;
    
    public ListController ( Storage storage, Session session, ConnectionHandlerBase connection )
    {
        super ( connection );
        _storage = storage;
        _session = session;
        _connection = connection;
    }

    public void run ( Message request ) throws InvalidSessionException
    {
        _id = _storage.startList ( _session, this );
        sendACK ( request, _id );
        _storage.thawOperation ( _session, _id );
    }

    public void complete ( QueryDescription[] queries )
    {
        ListReplyMessage message = new ListReplyMessage ();
        Set<QueryDescription> q = new HashSet<QueryDescription> ();
        q.addAll ( Arrays.asList ( queries ) );
        message.setQueries ( q );
        _connection.getConnection ().sendMessage ( message.toMessage ( _id ) );
    }

    public void failed ( Throwable error )
    {
        Message replyMessage = new Message ( Messages.CC_LIST_REPLY );
        replyMessage.getValues ().put ( Message.FIELD_ERROR_INFO, new StringValue ( error.getMessage () ) );
        replyMessage.getValues ().put ( "id", new LongValue ( _id ) );
        _connection.getConnection().sendMessage ( replyMessage );
    }
}
