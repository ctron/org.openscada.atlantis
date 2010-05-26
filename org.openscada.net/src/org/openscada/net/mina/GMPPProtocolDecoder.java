/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2010 inavare GmbH (http://inavare.com)
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

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GMPPProtocolDecoder extends CumulativeProtocolDecoder implements GMPPProtocol
{
    private final static Logger logger = LoggerFactory.getLogger ( GMPPProtocolDecoder.class );

    private final CharsetDecoder charDecoder = Charset.forName ( "utf-8" ).newDecoder ();

    private String decodeStringFromStream ( final IoBuffer buffer, final int size )
    {
        final byte[] data = new byte[size];
        buffer.get ( data );

        CharBuffer charBuffer;
        try
        {
            synchronized ( this.charDecoder )
            {
                charBuffer = this.charDecoder.decode ( ByteBuffer.wrap ( data ) );
            }
            return charBuffer.toString ();
        }
        catch ( final CharacterCodingException e )
        {
            return new String ( data );
        }
    }

    private String decodeStringFromStream ( final IoBuffer buffer )
    {
        return decodeStringFromStream ( buffer, buffer.getInt () );
    }

    private Value decodeValueFromStream ( final IoBuffer buffer )
    {
        final int type = buffer.getInt ();
        final int len = buffer.getInt ();

        logger.debug ( "Additional data: {} len: {}", type, len );

        switch ( type )
        {
        case VT_LONG:
            return new LongValue ( buffer.getLong () );
        case VT_INTEGER:
            return new IntegerValue ( buffer.getInt () );
        case VT_STRING:
            return new StringValue ( decodeStringFromStream ( buffer, len ) );
        case VT_DOUBLE:
            return decodeDoubleValueFromStream ( buffer );
        case VT_BOOLEAN:
            return new BooleanValue ( buffer.get () != 0 );
        case VT_VOID:
            return new VoidValue ();
            // nothing to read
        case VT_LIST:
            return decodeListValueFromStream ( buffer );
        case VT_MAP:
            return decodeMapValueFromStream ( buffer );
        default:
            // unknown type: only consume data
            buffer.position ( buffer.position () + len );
            break;
        }
        return null;
    }

    private DoubleValue decodeDoubleValueFromStream ( final IoBuffer buffer )
    {
        final Double d = Double.longBitsToDouble ( buffer.getLong () );
        return new DoubleValue ( d );
    }

    private ListValue decodeListValueFromStream ( final IoBuffer buffer )
    {
        final ListValue listValue = new ListValue ();

        final int items = buffer.getInt ();
        for ( int i = 0; i < items; i++ )
        {
            listValue.getValues ().add ( decodeValueFromStream ( buffer ) );
        }

        return listValue;
    }

    private MapValue decodeMapValueFromStream ( final IoBuffer buffer )
    {
        final MapValue mapValue = new MapValue ();

        final int items = buffer.getInt ();
        for ( int i = 0; i < items; i++ )
        {
            final Value value = decodeValueFromStream ( buffer );
            final String key = decodeStringFromStream ( buffer );
            mapValue.getValues ().put ( key, value );
        }

        return mapValue;
    }

    private Message decodeMessageFromStream ( final IoBuffer inputBuffer )
    {
        // read the packet
        final Message message = new Message ();
        message.setCommandCode ( inputBuffer.getInt () );
        message.setTimestamp ( inputBuffer.getLong () );
        message.setSequence ( inputBuffer.getLong () );
        message.setReplySequence ( inputBuffer.getLong () );

        inputBuffer.getInt (); // re-read to remove from buffer

        final Value value = decodeValueFromStream ( inputBuffer );
        if ( value instanceof MapValue )
        {
            message.setValues ( (MapValue)value );
        }

        return message;
    }

    @Override
    protected boolean doDecode ( final IoSession session, final IoBuffer inputBuffer, final ProtocolDecoderOutput out ) throws Exception
    {
        while ( inputBuffer.remaining () >= HEADER_SIZE )
        {
            // peek body size
            final int bodySize = inputBuffer.getInt ( inputBuffer.position () + 4 + 8 + 8 + 8 );

            if ( inputBuffer.remaining () < HEADER_SIZE + bodySize )
            {
                // message is not complete so skip for next try
                return false;
            }

            final Message message = decodeMessageFromStream ( inputBuffer );

            if ( message != null )
            {
                out.write ( message );
            }

        }
        return false;
    }

}
