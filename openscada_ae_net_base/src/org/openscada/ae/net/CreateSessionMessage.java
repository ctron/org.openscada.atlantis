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

import java.util.Map;
import java.util.Properties;

import org.openscada.net.base.data.Message;
import org.openscada.net.base.data.StringValue;
import org.openscada.net.base.data.Value;

public class CreateSessionMessage
{
    private Properties _properties = new Properties ();

    public Properties getProperties ()
    {
        return _properties;
    }

    public void setProperties ( Properties properties )
    {
        _properties = properties;
    }
    
    public Message toMessage ()
    {
        Message message = new Message ( Messages.CC_CREATE_SESSION );
        for ( Map.Entry<Object,Object> entry : _properties.entrySet () )
        {
             message.getValues ().put ( entry.getKey ().toString (), new StringValue ( entry.getValue ().toString () ) );
        }
        return message;
    }
    
    public static CreateSessionMessage fromMessage ( Message message )
    {
        CreateSessionMessage createSessionMessage = new CreateSessionMessage ();
        for ( Map.Entry<String, Value> entry : message.getValues ().getValues ().entrySet () )
        {
            createSessionMessage.getProperties ().put ( entry.getKey (), entry.getValue ().toString () );
        }
        return createSessionMessage;
    }
}
