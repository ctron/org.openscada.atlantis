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

package org.openscada.ae.net;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.openscada.ae.core.QueryDescription;
import org.openscada.core.net.MessageHelper;
import org.openscada.net.base.data.LongValue;
import org.openscada.net.base.data.MapValue;
import org.openscada.net.base.data.Message;
import org.openscada.net.base.data.Value;

public class ListReplyMessage
{
    private Set<QueryDescription> _queries = new HashSet<QueryDescription> ();

    public Set<QueryDescription> getQueries ()
    {
        return _queries;
    }

    public void setQueries ( Set<QueryDescription> queries )
    {
        _queries = queries;
    }
    
    /**
     * Create a message in order to send it back
     * @param id The operation id
     * @return
     */
    public Message toMessage ( long id )
    {
        Message message = new Message ( Messages.CC_LIST_REPLY );
        
        MapValue list = new MapValue ();
        
        for ( QueryDescription description : _queries )
        {
            list.put ( description.getId (), MessageHelper.attributesToMap ( description.getAttributes () ) );
        }
        
        message.getValues ().put ( "queries", list );
        message.getValues ().put ( "id", new LongValue ( id ) );
        
        return message;
    }
    
    public static ListReplyMessage fromMessage ( Message message )
    {
        ListReplyMessage listReplyMessage = new ListReplyMessage ();
        
        MapValue list = (MapValue)message.getValues ().get ( "queries" );
        
        for ( Map.Entry<String,Value> entry : list.getValues ().entrySet () )
        {
            listReplyMessage.getQueries ().add ( new QueryDescription (
                    entry.getKey (),
                    MessageHelper.mapToAttributes ( (MapValue)entry.getValue () )
                    ));
        }
        
        return listReplyMessage;
    }
    
}
