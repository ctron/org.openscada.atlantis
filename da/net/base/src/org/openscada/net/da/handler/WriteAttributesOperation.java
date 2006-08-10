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

package org.openscada.net.da.handler;

import java.util.Map;

import org.openscada.da.core.data.Variant;
import org.openscada.net.base.data.MapValue;
import org.openscada.net.base.data.Message;
import org.openscada.net.base.data.StringValue;
import org.openscada.net.base.data.Value;
import org.openscada.utils.lang.Holder;

public class WriteAttributesOperation
{

    public static Message createRequest ( String itemId, Map<String, Variant> attributes )
    {
        Message message = new Message ( Messages.CC_WRITE_ATTRIBUTES_OPERATION );
        
        message.getValues().put ( "item-id", new StringValue ( itemId ) );
        message.getValues().put ( "attributes", Messages.attributesToMap ( attributes ) );
        
        return message;
    }

    public static void parseRequest ( Message message, Holder<String> itemId, Holder<Map<String, Variant>> attributes )
    {
        // FIXME: handle missing item name
        itemId.value = message.getValues().get ( "item-id" ).toString();
        
        Value value = message.getValues ().get ( "attributes" );
        if ( value instanceof MapValue )
            attributes.value = Messages.mapToAttributes ( (MapValue)value );
    }
}
