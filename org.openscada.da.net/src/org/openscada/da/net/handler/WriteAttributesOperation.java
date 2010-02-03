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

package org.openscada.da.net.handler;

import java.util.Map;

import org.openscada.core.Variant;
import org.openscada.core.net.MessageHelper;
import org.openscada.da.core.WriteAttributeResult;
import org.openscada.da.core.WriteAttributeResults;
import org.openscada.net.base.data.LongValue;
import org.openscada.net.base.data.MapValue;
import org.openscada.net.base.data.Message;
import org.openscada.net.base.data.StringValue;
import org.openscada.net.base.data.Value;
import org.openscada.net.base.data.VoidValue;
import org.openscada.utils.lang.Holder;

public class WriteAttributesOperation
{

    public static Message createRequest ( final String itemId, final Map<String, Variant> attributes )
    {
        final Message message = new Message ( Messages.CC_WRITE_ATTRIBUTES_OPERATION );

        message.getValues ().put ( "item-id", new StringValue ( itemId ) );
        message.getValues ().put ( "attributes", MessageHelper.attributesToMap ( attributes ) );

        return message;
    }

    public static void parseRequest ( final Message message, final Holder<String> itemId, final Holder<Map<String, Variant>> attributes )
    {
        // FIXME: handle missing item name
        itemId.value = message.getValues ().get ( "item-id" ).toString ();

        final Value value = message.getValues ().get ( "attributes" );
        if ( value instanceof MapValue )
        {
            attributes.value = MessageHelper.mapToAttributes ( (MapValue)value );
        }
    }

    public static Message createResponse ( final long id, final WriteAttributeResults writeAttributeResults )
    {
        final Message message = new Message ( Messages.CC_WRITE_ATTRIBUTES_OPERATION_RESULT );

        message.getValues ().put ( "id", new LongValue ( id ) );

        final MapValue resultValues = new MapValue ();
        for ( final Map.Entry<String, WriteAttributeResult> writeAttributeResult : writeAttributeResults.entrySet () )
        {
            if ( writeAttributeResult.getValue ().isError () )
            {
                resultValues.put ( writeAttributeResult.getKey (), new StringValue ( writeAttributeResult.getValue ().getError ().getMessage () ) );
            }
            else
            {
                resultValues.put ( writeAttributeResult.getKey (), new VoidValue () );
            }
        }

        message.getValues ().put ( "results", resultValues );

        return message;
    }

    public static Message createResponse ( final long id, final Throwable error )
    {
        final Message message = new Message ( Messages.CC_WRITE_ATTRIBUTES_OPERATION_RESULT );

        message.getValues ().put ( "id", new LongValue ( id ) );

        if ( error.getMessage () != null )
        {
            message.getValues ().put ( Message.FIELD_ERROR_INFO, new StringValue ( error.getMessage () ) );
        }
        else
        {
            message.getValues ().put ( Message.FIELD_ERROR_INFO, new StringValue ( error.toString () ) );
        }

        return message;
    }

    public static WriteAttributeResults parseResponse ( final Message message ) throws Exception
    {
        final WriteAttributeResults writeAttributeResults = new WriteAttributeResults ();

        if ( message.getValues ().containsKey ( Message.FIELD_ERROR_INFO ) )
        {
            throw new Exception ( message.getValues ().get ( Message.FIELD_ERROR_INFO ).toString () );
        }

        if ( message.getValues ().containsKey ( "results" ) )
        {
            if ( message.getValues ().get ( "results" ) instanceof MapValue )
            {
                final MapValue resultValues = (MapValue)message.getValues ().get ( "results" );
                for ( final Map.Entry<String, Value> entry : resultValues.getValues ().entrySet () )
                {
                    final String name = entry.getKey ();
                    if ( entry.getValue () instanceof VoidValue )
                    {
                        writeAttributeResults.put ( name, WriteAttributeResult.OK );
                    }
                    else
                    {
                        writeAttributeResults.put ( name, new WriteAttributeResult ( new Exception ( entry.getValue ().toString () ) ) );
                    }
                }
            }
        }

        return writeAttributeResults;
    }
}
