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

package org.openscada.da.server.net;

import java.util.Map;

import org.openscada.core.InvalidSessionException;
import org.openscada.core.Variant;
import org.openscada.core.net.OperationController;
import org.openscada.da.core.WriteAttributeResults;
import org.openscada.da.core.server.Hive;
import org.openscada.da.core.server.InvalidItemException;
import org.openscada.da.core.server.Session;
import org.openscada.da.core.server.WriteAttributesOperationListener;
import org.openscada.net.base.data.Message;
import org.openscada.net.da.handler.WriteAttributesOperation;
import org.openscada.net.mina.Messenger;
import org.openscada.utils.lang.Holder;

public class WriteAttributesController extends OperationController implements WriteAttributesOperationListener
{
    private Hive hive = null;

    private Session session = null;

    private Messenger messenger = null;

    private Long id = null;

    public WriteAttributesController ( final Hive hive, final Session session, final Messenger messenger )
    {
        super ( messenger );
        this.hive = hive;
        this.session = session;
        this.messenger = messenger;
    }

    public void run ( final Message request )
    {
        try
        {
            final Holder<String> itemId = new Holder<String> ();
            final Holder<Map<String, Variant>> attributes = new Holder<Map<String, Variant>> ();

            WriteAttributesOperation.parseRequest ( request, itemId, attributes );

            this.id = this.hive.startWriteAttributes ( this.session, itemId.value, attributes.value, this );
        }
        catch ( final InvalidSessionException e )
        {
            sendFailure ( request, e );
        }
        catch ( final InvalidItemException e )
        {
            sendFailure ( request, e );
        }

        // send out ACK with operation id
        sendACK ( request, this.id );

        try
        {
            this.hive.thawOperation ( this.session, this.id );
        }
        catch ( final InvalidSessionException e )
        {
            // should never happen
        }
    }

    public void complete ( final WriteAttributeResults writeAttributeResults )
    {
        if ( this.id != null )
        {
            final Message message = WriteAttributesOperation.createResponse ( this.id, writeAttributeResults );
            this.messenger.sendMessage ( message );
        }
    }

    public void failed ( final Throwable error )
    {
        if ( this.id != null )
        {
            final Message message = WriteAttributesOperation.createResponse ( this.id, error );
            this.messenger.sendMessage ( message );
        }
    }
}
