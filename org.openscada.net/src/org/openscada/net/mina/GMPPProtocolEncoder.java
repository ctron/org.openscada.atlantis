/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2010 TH4 SYSTEMS GmbH (http://inavare.com)
 *
 * OpenSCADA is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 3
 * only, as published by the Free Software Foundation.
 *
 * OpenSCADA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License version 3 for more details
 * (a copy is included in the LICENSE file that accompanied this code).
 *
 * You should have received a copy of the GNU Lesser General Public License
 * version 3 along with OpenSCADA. If not, see
 * <http://opensource.org/licenses/lgpl-3.0.html> for a copy of the LGPLv3 License.
 */

package org.openscada.net.mina;

import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.util.Map;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;
import org.openscada.net.base.data.BooleanValue;
import org.openscada.net.base.data.DoubleValue;
import org.openscada.net.base.data.IntegerValue;
import org.openscada.net.base.data.ListValue;
import org.openscada.net.base.data.LongValue;
import org.openscada.net.base.data.MapValue;
import org.openscada.net.base.data.Message;
import org.openscada.net.base.data.StringValue;
import org.openscada.net.base.data.Value;
import org.openscada.net.base.data.VoidValue;
import org.openscada.net.codec.InvalidValueTypeException;

public class GMPPProtocolEncoder implements ProtocolEncoder, GMPPProtocol
{
    private final ThreadLocal<CharsetEncoder> encoderLocal = new ThreadLocal<CharsetEncoder> () {
        @Override
        protected CharsetEncoder initialValue ()
        {
            return Charset.forName ( "utf-8" ).newEncoder ();
        }
    };

    public void dispose ( final IoSession session ) throws Exception
    {
        // Do nothing
    }

    public void encode ( final IoSession session, final Object message, final ProtocolEncoderOutput out ) throws Exception
    {
        if ( message instanceof Message )
        {
            out.write ( code ( (Message)message ) );
        }
        else
        {
            throw new IllegalArgumentException ( "Protocol encoder can only handle messages of type 'Message'" );
        }
    }

    private void encodeToStream ( final IoBuffer buffer, final String data )
    {
        buffer.putInt ( 0 );
        final int pos = buffer.position ();

        try
        {
            buffer.putString ( data, this.encoderLocal.get () );
            final int afterPos = buffer.position ();
            final int len = afterPos - pos;
            buffer.putInt ( pos - 4, len );
        }
        catch ( final CharacterCodingException e )
        {
            throw new RuntimeException ( "Failed to encode", e );
        }
    }

    private void encodeToStream ( final IoBuffer buffer, final IntegerValue value )
    {
        buffer.putInt ( VT_INTEGER );
        buffer.putInt ( 4 );
        buffer.putInt ( value.value );
    }

    private void encodeToStream ( final IoBuffer buffer, final LongValue value )
    {
        buffer.putInt ( VT_LONG );
        buffer.putInt ( 8 );
        buffer.putLong ( value.getValue () );
    }

    private void encodeToStream ( final IoBuffer buffer, final DoubleValue value )
    {
        buffer.putInt ( VT_DOUBLE );
        buffer.putInt ( 8 );
        buffer.putLong ( Double.doubleToRawLongBits ( value.getValue () ) );
    }

    private void encodeToStream ( final IoBuffer buffer, final VoidValue value )
    {
        buffer.putInt ( VT_VOID );
        buffer.putInt ( 0 );
    }

    private void encodeToStream ( final IoBuffer buffer, final BooleanValue value )
    {
        buffer.putInt ( VT_BOOLEAN );
        buffer.putInt ( 1 );
        buffer.put ( value.getValue () ? (byte)0xFF : (byte)0x00 );
    }

    private void encodeToStream ( final IoBuffer buffer, final StringValue value )
    {
        buffer.putInt ( VT_STRING );
        encodeToStream ( buffer, value.getValue () );
    }

    private void encodeToStream ( final IoBuffer buffer, final ListValue value ) throws InvalidValueTypeException
    {
        buffer.putInt ( VT_LIST );
        final int position = buffer.position (); // remember position
        buffer.putInt ( 0 ); // dummy size length

        final int startPos = buffer.position ();
        buffer.putInt ( value.getValues ().size () );

        for ( final Value valueEntry : value.getValues () )
        {
            codeValue ( buffer, valueEntry );
        }
        final int size = buffer.position () - startPos;

        buffer.putInt ( position, size ); // set value size
    }

    private void encodeToStream ( final IoBuffer buffer, final MapValue value ) throws InvalidValueTypeException
    {
        buffer.putInt ( VT_MAP );
        final int position = buffer.position (); // remember position
        buffer.putInt ( 0 ); // dummy size length

        final int startPos = buffer.position ();
        buffer.putInt ( value.getValues ().size () );

        for ( final Map.Entry<String, Value> valueEntry : value.getValues ().entrySet () )
        {
            codeValue ( buffer, valueEntry.getKey (), valueEntry.getValue () );
        }
        final int size = buffer.position () - startPos;

        buffer.putInt ( position, size ); // set value size
    }

    private void codeValue ( final IoBuffer buffer, final Value value ) throws InvalidValueTypeException
    {
        final Class<?> clazz = value.getClass ();

        if ( clazz == StringValue.class )
        {
            encodeToStream ( buffer, (StringValue)value );
        }
        else if ( clazz == BooleanValue.class )
        {
            encodeToStream ( buffer, (BooleanValue)value );
        }
        else if ( clazz == IntegerValue.class )
        {
            encodeToStream ( buffer, (IntegerValue)value );
        }
        else if ( clazz == LongValue.class )
        {
            encodeToStream ( buffer, (LongValue)value );
        }
        else if ( clazz == DoubleValue.class )
        {
            encodeToStream ( buffer, (DoubleValue)value );
        }
        else if ( clazz == VoidValue.class )
        {
            encodeToStream ( buffer, (VoidValue)value );
        }
        else if ( clazz == ListValue.class )
        {
            encodeToStream ( buffer, (ListValue)value );
        }
        else if ( clazz == MapValue.class )
        {
            encodeToStream ( buffer, (MapValue)value );
        }
        else
        {
            if ( value == null )
            {
                // at least provide some more information
                throw new NullPointerException ( "Trying to encode a 'null' value. Use VoidValue instead!" );
            }
            throw new InvalidValueTypeException ( String.format ( "The type '%s' is unknown", value.getClass ().getName () ) );
        }
    }

    private void codeValue ( final IoBuffer buffer, final String name, final Value value ) throws InvalidValueTypeException
    {
        codeValue ( buffer, value );
        encodeToStream ( buffer, name );
    }

    public IoBuffer code ( final Message message ) throws InvalidValueTypeException
    {
        final IoBuffer outputBuffer = IoBuffer.allocate ( HEADER_SIZE );

        outputBuffer.setAutoExpand ( true );
        outputBuffer.clear ();

        outputBuffer.putInt ( message.getCommandCode () );
        outputBuffer.putLong ( message.getTimestamp () );
        outputBuffer.putLong ( message.getSequence () );
        outputBuffer.putLong ( message.getReplySequence () );
        final int sizePos = outputBuffer.position ();
        outputBuffer.putInt ( 0 ); // dummy body size

        final int startPos = outputBuffer.position ();
        codeValue ( outputBuffer, message.getValues () );
        final int bodySize = outputBuffer.position () - startPos;
        outputBuffer.putInt ( sizePos, bodySize );

        outputBuffer.flip ();

        return outputBuffer;
    }
}
