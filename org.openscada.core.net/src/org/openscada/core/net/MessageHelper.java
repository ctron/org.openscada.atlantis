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

package org.openscada.core.net;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.openscada.core.NotConvertableException;
import org.openscada.core.NullValueException;
import org.openscada.core.Variant;
import org.openscada.net.base.data.BooleanValue;
import org.openscada.net.base.data.DoubleValue;
import org.openscada.net.base.data.IntegerValue;
import org.openscada.net.base.data.LongValue;
import org.openscada.net.base.data.MapValue;
import org.openscada.net.base.data.Message;
import org.openscada.net.base.data.StringValue;
import org.openscada.net.base.data.Value;
import org.openscada.net.base.data.VoidValue;

public class MessageHelper
{
    public static final int CC_CREATE_SESSION = 0x00010001;

    public static final int CC_CLOSE_SESSION = 0x00010002;

    /**
     * Convert a MapValue to a attributes map
     * @param mapValue the map value to convert
     * @return the attributes map
     * @note Only scalar entries in the map are converted. Other values are skipped.
     */
    public static Map<String, Variant> mapToAttributes ( final MapValue mapValue )
    {
        final Map<String, Variant> attributes = new HashMap<String, Variant> ();

        for ( final Map.Entry<String, Value> entry : mapValue.getValues ().entrySet () )
        {
            Variant value = null;
            final Value entryValue = entry.getValue ();

            value = valueToVariant ( entryValue, null );

            if ( value != null )
            {
                attributes.put ( new String ( entry.getKey () ), value );
            }
        }

        return attributes;
    }

    public static MapValue attributesToMap ( final Map<String, Variant> attributes )
    {
        final MapValue mapValue = new MapValue ();

        for ( final Map.Entry<String, Variant> entry : attributes.entrySet () )
        {
            final Value value = variantToValue ( entry.getValue () );
            if ( value != null )
            {
                mapValue.put ( new String ( entry.getKey () ), value );
            }
        }

        return mapValue;
    }

    public static Variant valueToVariant ( final Value value, final Variant defaultValue )
    {
        if ( value == null )
        {
            return defaultValue;
        }

        if ( value instanceof StringValue )
        {
            return new Variant ( ( (StringValue)value ).getValue () );
        }
        else if ( value instanceof BooleanValue )
        {
            return Variant.valueOf ( ( (BooleanValue)value ).getValue () );
        }
        else if ( value instanceof DoubleValue )
        {
            return new Variant ( ( (DoubleValue)value ).getValue () );
        }
        else if ( value instanceof LongValue )
        {
            return new Variant ( ( (LongValue)value ).getValue () );
        }
        else if ( value instanceof IntegerValue )
        {
            return new Variant ( ( (IntegerValue)value ).getValue () );
        }
        else if ( value instanceof VoidValue )
        {
            return Variant.NULL;
        }

        return defaultValue;
    }

    public static Value variantToValue ( final Variant value )
    {
        if ( value == null )
        {
            return null;
        }

        try
        {
            if ( value.isDouble () )
            {
                return new DoubleValue ( value.asDouble () );
            }
            else if ( value.isInteger () )
            {
                return new IntegerValue ( value.asInteger () );
            }
            else if ( value.isLong () )
            {
                return new LongValue ( value.asLong () );
            }
            else if ( value.isBoolean () )
            {
                return new BooleanValue ( value.asBoolean () );
            }
            else if ( value.isString () )
            {
                return new StringValue ( value.asString () );
            }
            else if ( value.isNull () )
            {
                return new VoidValue ();
            }
        }
        catch ( final NullValueException e )
        {
            return new VoidValue ();
        }
        catch ( final NotConvertableException e )
        {
        }
        return null;
    }

    /**
     * Construct a CC_CREATE_SESSION message
     * @param props the session properties
     * @return the create session message
     */
    public static Message createSession ( final Properties props )
    {
        final Message msg = new Message ( CC_CREATE_SESSION );

        msg.getValues ().put ( "properties", toValue ( props ) );

        return msg;
    }

    public static Message closeSession ()
    {
        return new Message ( CC_CLOSE_SESSION );
    }

    /**
     * Convert a map value to properties
     * <p>
     * If the value is not a {@link MapValue} or is <code>null</code> the properties will not be modified.
     * </p>
     * @param properties the properties to fill
     * @param value the value to parse
     */
    public static void getProperties ( final Properties properties, final Value value )
    {
        if ( value instanceof MapValue )
        {
            final MapValue mapValue = (MapValue)value;
            for ( final Map.Entry<String, Value> entry : mapValue.getValues ().entrySet () )
            {
                properties.put ( entry.getKey (), entry.getValue ().toString () );
            }
        }
    }

    public static Message createSessionACK ( final Message inputMessage, final Properties sessionProperties )
    {
        final Message message = new Message ( Message.CC_ACK, inputMessage.getSequence () );
        message.getValues ().put ( "properties", toValue ( sessionProperties ) );
        return message;
    }

    private static MapValue toValue ( final Properties sessionProperties )
    {
        final MapValue value = new MapValue ();
        if ( sessionProperties != null )
        {
            for ( final Map.Entry<Object, Object> entry : sessionProperties.entrySet () )
            {
                value.put ( entry.getKey ().toString (), new StringValue ( entry.getValue ().toString () ) );
            }
        }
        return value;
    }

}
