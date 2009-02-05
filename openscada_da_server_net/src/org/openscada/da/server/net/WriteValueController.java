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

package org.openscada.da.server.net;

import org.openscada.core.InvalidSessionException;
import org.openscada.core.Variant;
import org.openscada.core.net.OperationController;
import org.openscada.da.core.server.Hive;
import org.openscada.da.core.server.InvalidItemException;
import org.openscada.da.core.server.Session;
import org.openscada.da.core.server.WriteOperationListener;
import org.openscada.net.base.ConnectionHandlerBase;
import org.openscada.net.base.data.LongValue;
import org.openscada.net.base.data.Message;
import org.openscada.net.base.data.StringValue;
import org.openscada.net.da.handler.Messages;
import org.openscada.utils.lang.Holder;

public class WriteValueController extends OperationController implements WriteOperationListener
{
    private Hive _hive = null;
    private Session _session = null;
    private ConnectionHandlerBase _connection = null;
    
    private Long _id = null; 
    
    public WriteValueController ( Hive hive, Session session, ConnectionHandlerBase connection )
    {
        super ( connection );
        _hive = hive;
        _session = session;
        _connection = connection;
    }
    
    public void run ( Message request )
    {
        try
        {
            Holder<String> itemId = new Holder<String> ();
            Holder<Variant> value = new Holder<Variant> ();
            
            org.openscada.net.da.handler.WriteOperation.parse ( request, itemId, value );
            
            _id = _hive.startWrite ( _session, itemId.value, value.value, this );
        }
        catch ( InvalidSessionException e )
        {
            sendFailure ( request, e );
            return;
        }
        catch ( InvalidItemException e )
        {
            sendFailure ( request, e );
            return;
        }
        
        // send out ACK with operation id
        sendACK ( request, _id );
        
        try
        {
            _hive.thawOperation ( _session, _id );
        }
        catch ( InvalidSessionException e )
        {
            // should never happen
        }
    }
    
    public void failure ( Throwable throwable )
    {
        Message replyMessage = new Message ( Messages.CC_WRITE_OPERATION_RESULT );
        replyMessage.getValues ().put ( Message.FIELD_ERROR_INFO, new StringValue ( throwable.getMessage () ) );
        replyMessage.getValues ().put ( "id", new LongValue ( _id ) );
        _connection.getConnection().sendMessage ( replyMessage );
    }

    public void success ()
    {
        Message replyMessage = new Message ( Messages.CC_WRITE_OPERATION_RESULT );
        replyMessage.getValues ().put ( "id", new LongValue ( _id ) );
        _connection.getConnection().sendMessage ( replyMessage );
    }
}
