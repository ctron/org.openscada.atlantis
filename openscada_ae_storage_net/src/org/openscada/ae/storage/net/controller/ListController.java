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
import org.openscada.net.base.data.LongValue;
import org.openscada.net.base.data.Message;
import org.openscada.net.base.data.StringValue;
import org.openscada.net.mina.Messenger;

public class ListController extends OperationController implements ListOperationListener
{
    private Storage _storage = null;

    private Long _id = null;

    private Session _session = null;

    private Messenger _connection = null;

    public ListController ( final Storage storage, final Session session, final Messenger connection )
    {
        super ( connection );
        this._storage = storage;
        this._session = session;
        this._connection = connection;
    }

    public void run ( final Message request ) throws InvalidSessionException
    {
        this._id = this._storage.startList ( this._session, this );
        sendACK ( request, this._id );
        this._storage.thawOperation ( this._session, this._id );
    }

    public void complete ( final QueryDescription[] queries )
    {
        final ListReplyMessage message = new ListReplyMessage ();
        final Set<QueryDescription> q = new HashSet<QueryDescription> ();
        q.addAll ( Arrays.asList ( queries ) );
        message.setQueries ( q );
        this._connection.sendMessage ( message.toMessage ( this._id ) );
    }

    public void failed ( final Throwable error )
    {
        final Message replyMessage = new Message ( Messages.CC_LIST_REPLY );
        replyMessage.getValues ().put ( Message.FIELD_ERROR_INFO, new StringValue ( error.getMessage () ) );
        replyMessage.getValues ().put ( "id", new LongValue ( this._id ) );
        this._connection.sendMessage ( replyMessage );
    }
}
