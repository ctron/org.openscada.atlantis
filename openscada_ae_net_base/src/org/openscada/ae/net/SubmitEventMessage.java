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

import java.util.Calendar;
import java.util.Map;
import java.util.Properties;

import org.openscada.ae.core.Event;
import org.openscada.core.net.MessageHelper;
import org.openscada.net.base.data.LongValue;
import org.openscada.net.base.data.MapValue;
import org.openscada.net.base.data.Message;
import org.openscada.net.base.data.StringValue;
import org.openscada.net.base.data.Value;

public class SubmitEventMessage
{
    private Event _event = null;
    private Properties _properties = null;

    public Event getEvent ()
    {
        return _event;
    }

    public void setEvent ( Event event )
    {
        _event = event;
    }
    
    public Message toMessage ()
    {
        Message message = new Message ( Messages.CC_SUBMIT_EVENT );
       
        message.getValues ().put ( "id", new StringValue ( _event.getId () ) );
        message.getValues ().put ( "timestamp", new LongValue ( _event.getTimestamp ().getTimeInMillis () ) );
        message.getValues ().put ( "attributes", MessageHelper.attributesToMap ( _event.getAttributes () ) );
        
        MapValue properties = new MapValue ();
        for ( Map.Entry<Object, Object> entry : _properties.entrySet () )
        {
            properties.put ( entry.getKey ().toString (), new StringValue ( entry.getValue ().toString () ) );
        }
        message.getValues ().put ( "properties", properties );
        
        return message;
    }
    
    public static SubmitEventMessage fromMessage ( Message message )
    {
        SubmitEventMessage submitEventMessage = new SubmitEventMessage ();
        
        // get event
        Event event = new Event ( message.getValues ().get ( "id" ).toString () );
        
        Calendar c = Calendar.getInstance ();
        c.setTimeInMillis ( ((LongValue)message.getValues ().get ( "timestamp" )).getValue () );
        event.setTimestamp ( c );
        
        event.setAttributes ( MessageHelper.mapToAttributes ( (MapValue)message.getValues ().get ( "attributes" ) ) );
        submitEventMessage.setEvent ( event );

        // get properties
        Properties props = new Properties ();
        
        MapValue properties = (MapValue)message.getValues ().get ( "properties" );
        for ( Map.Entry<String, Value> entry : properties.getValues ().entrySet () )
        {
            props.put ( entry.getKey (), entry.getValue ().toString () );
        }
        
        submitEventMessage.setProperties ( props );
        
        // return result
        return submitEventMessage;
    }

    public Properties getProperties ()
    {
        return _properties;
    }

    public void setProperties ( Properties properties )
    {
        _properties = properties;
    }
}
