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

import org.openscada.da.core.WriteAttributesOperationListener.Results;
import org.openscada.da.core.WriteAttributesOperationListener.Result;
import org.openscada.da.core.data.Variant;
import org.openscada.net.base.data.LongValue;
import org.openscada.net.base.data.MapValue;
import org.openscada.net.base.data.Message;
import org.openscada.net.base.data.StringValue;
import org.openscada.net.base.data.Value;
import org.openscada.net.base.data.VoidValue;
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
    
    public static Message createResponse ( long id, Results results )
    {
        Message message = new Message ( Messages.CC_WRITE_ATTRIBUTES_OPERATION_RESULT );
    
        message.getValues (). put ( "id", new LongValue ( id ) );
        
        MapValue resultValues = new MapValue ();
        for ( Map.Entry<String, Result> result : results.entrySet () )
        {
            if ( result.getValue ().isError () )
                resultValues.put ( result.getKey (), new StringValue ( result.getValue ().getError ().getMessage () ) );
            else
                resultValues.put ( result.getKey (), new VoidValue () );
        }
        
        message.getValues ().put ( "results", resultValues );
        
        return message;
    }
    
    public static Results parseResponse ( Message message )
    {
        Results results = new Results ();
        
        if ( message.getValues ().containsKey ( "results" ) )
        {
            if ( message.getValues ().get ( "results" ) instanceof MapValue )
            {
                MapValue resultValues = (MapValue)message.getValues ().get ( "results" );
                for ( Map.Entry<String,Value> entry : resultValues.getValues ().entrySet () )
                {
                    String name = entry.getKey ();
                    if ( entry.getValue () instanceof VoidValue )
                        results.put ( name, new Result () );
                    else
                        results.put ( name, new Result ( new Exception ( entry.getValue ().toString () ) ) );
                }
            }
        }
        
        return results;
    }
}
