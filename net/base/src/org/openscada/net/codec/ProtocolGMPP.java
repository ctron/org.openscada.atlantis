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

package org.openscada.net.codec;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.Map;

import org.apache.log4j.Logger;
import org.openscada.net.base.MessageListener;
import org.openscada.net.base.data.DoubleValue;
import org.openscada.net.base.data.IntegerValue;
import org.openscada.net.base.data.ListValue;
import org.openscada.net.base.data.LongValue;
import org.openscada.net.base.data.MapValue;
import org.openscada.net.base.data.Message;
import org.openscada.net.base.data.StringValue;
import org.openscada.net.base.data.Value;
import org.openscada.net.base.data.VoidValue;
import org.openscada.net.io.Connection;

public class ProtocolGMPP implements Protocol
{

    public final static int VT_STRING = 	0x000000001;
    public final static int VT_LONG = 		0x000000002;
    public final static int VT_DOUBLE =  	0x000000003;
    public final static int VT_VOID =       0x000000004;
    public final static int VT_INTEGER =    0x000000005;
    public final static int VT_LIST =       0x000000006;
    public final static int VT_MAP =        0x000000007;

    private final static int HEADER_SIZE = 4 + 8 + 8 + 8 + 4;
    
    private final static int BLOCK_SIZE =   4096;

    private static Logger _log = Logger.getLogger ( ProtocolGMPP.class );

    private Connection _connection = null;
    private MessageListener _listener = null;
    private ByteBuffer _inputBuffer = null;
    private CharsetEncoder _charEncoder = Charset.forName ( "utf-8" ).newEncoder ();
    private CharsetDecoder _charDecoder = Charset.forName ( "utf-8" ).newDecoder ();

    public ProtocolGMPP ( Connection connection, MessageListener listener )
    {
        _connection = connection;
        _listener = listener;	
    }
    
    private ByteBuffer encodeToStream ( ByteBuffer buffer, String data )
    {
        ByteBuffer rawData;
        synchronized ( _charEncoder )
        {
            try
            {
                rawData = _charEncoder.encode ( CharBuffer.wrap ( data ) );
            }
            catch ( CharacterCodingException e )
            {
                rawData = ByteBuffer.wrap ( data.getBytes () );
            }
        }
        
        buffer = ensureCapacity ( buffer, 4 + rawData.remaining () );
        buffer.putInt ( rawData.remaining () );
        buffer.put ( rawData );
        
        return buffer;
    }
    
    private ByteBuffer encodeToStream ( ByteBuffer buffer, IntegerValue value )
    {
        buffer = ensureCapacity ( buffer, 4 + 4 + 4 );
        buffer.putInt ( VT_INTEGER );
        buffer.putInt ( 4 );
        buffer.putInt ( value.getValue () );
        
        return buffer;
    }
    
    private ByteBuffer encodeToStream ( ByteBuffer buffer, LongValue value )
    {
        buffer = ensureCapacity ( buffer, 4 + 4 + 4 );
        buffer.putInt ( VT_LONG );
        buffer.putInt ( 8 );
        buffer.putLong ( value.getValue () );
        
        return buffer;
    }
    
    private ByteBuffer encodeToStream ( ByteBuffer buffer, DoubleValue value )
    {
        buffer = ensureCapacity ( buffer, 4 + 4 + 4 );
        buffer.putInt ( VT_DOUBLE );
        buffer.putInt ( 8 );
        buffer.putDouble ( value.getValue() );
        
        return buffer;
    }
    
    private ByteBuffer encodeToStream ( ByteBuffer buffer, VoidValue value )
    {
        buffer = ensureCapacity ( buffer, 4 + 4 );
        buffer.putInt ( VT_VOID );
        buffer.putInt ( 0 );
        
        return buffer;
    }
    
    private ByteBuffer encodeToStream ( ByteBuffer buffer, StringValue value )
    {
        buffer = ensureCapacity ( buffer, 4 );
        buffer.putInt ( VT_STRING );
        buffer = encodeToStream ( buffer, value.getValue () );
        return buffer;
    }
    
    private ByteBuffer encodeToStream ( ByteBuffer buffer, ListValue value ) throws InvalidValueTypeException
    {
        buffer = ensureCapacity ( buffer, 4 + 4 + 4 );
        buffer.putInt ( VT_LIST );
        int position = buffer.position (); // remember position
        buffer.putInt ( 0 ); // dummy size length
        
        int startPos = buffer.position ();
        buffer.putInt ( value.getValues ().size () );
        
        for ( Value valueEntry : value.getValues () )
        {
            buffer = codeValue ( buffer, valueEntry );
        }
        int size = buffer.position () - startPos;
        
        buffer.putInt ( position, size ); // set value size
        return buffer;
    }
    
    private ByteBuffer encodeToStream ( ByteBuffer buffer, MapValue value ) throws InvalidValueTypeException
    {
        buffer = ensureCapacity ( buffer, 4 + 4 + 4 );
        buffer.putInt ( VT_MAP );
        int position = buffer.position (); // remember position
        buffer.putInt ( 0 ); // dummy size length
        
        int startPos = buffer.position ();
        buffer.putInt ( value.getValues ().size () );
        
        for ( Map.Entry < String, Value > valueEntry : value.getValues ().entrySet () )
        {
            buffer = codeValue ( buffer, valueEntry.getKey (), valueEntry.getValue () );
        }
        int size = buffer.position () - startPos;
        
        buffer.putInt ( position, size ); // set value size
        return buffer;
    }
    
    private ByteBuffer codeValue ( ByteBuffer buffer, Value value ) throws InvalidValueTypeException
    {
        if ( value instanceof StringValue )
        {
            buffer = encodeToStream ( buffer, (StringValue)value );
        }
        else if ( value instanceof IntegerValue )
        {
            buffer = encodeToStream ( buffer, (IntegerValue)value );
        }
        else if ( value instanceof LongValue )
        {
            buffer = encodeToStream ( buffer, (LongValue)value );
        }
        else if ( value instanceof DoubleValue )
        {
            buffer = encodeToStream ( buffer, (DoubleValue)value );
        }
        else if ( value instanceof VoidValue )
        {
            buffer = encodeToStream ( buffer, (VoidValue)value );
        }
        else if ( value instanceof ListValue )
        {
            buffer = encodeToStream ( buffer, (ListValue)value );
        }
        else if ( value instanceof MapValue )
        {
            buffer = encodeToStream ( buffer, (MapValue)value );
        }
        else
            throw new InvalidValueTypeException ();
        return buffer;
    }
    
    private ByteBuffer codeValue ( ByteBuffer buffer, String name, Value value ) throws InvalidValueTypeException
    {
        buffer = codeValue ( buffer, value );
        buffer = encodeToStream ( buffer, name );
        
        return buffer;
    }

    private ByteBuffer ensureCapacity ( ByteBuffer buffer, int size )
    {
        logBuffer ( "before ensure", buffer );
        
        ByteBuffer xBuffer = ensureCapacityPerform ( buffer, size );
        
        logBuffer ( "after ensure", xBuffer );
        
        return xBuffer;
    }
    
    private ByteBuffer ensureCapacityPerform ( ByteBuffer buffer, int size )
    {
        if ( buffer == null )
        {
            ByteBuffer newBuffer = ByteBuffer.allocate ( size );
            newBuffer.clear ();
            return newBuffer;
        }
            
        if ( buffer.remaining () > size )
            return buffer;
        
        int newSize = buffer.position ();
        newSize += size;
        
        int delta = newSize % BLOCK_SIZE;
        if ( delta > 0 )
            newSize += BLOCK_SIZE - delta;
        
        _log.debug ( "New size: " + newSize );
        
        ByteBuffer newBuffer = ByteBuffer.allocate ( newSize );
        buffer.flip ();
        newBuffer.put ( buffer );
        
        return newBuffer;
    }
    
    /* (non-Javadoc)
     * @see org.openscada.net.codec.Protocol#code(org.openscada.net.base.data.Message)
     */
    public ByteBuffer code ( Message message ) throws InvalidValueTypeException
    {
        ByteBuffer outputBuffer = null;
        
        outputBuffer = ensureCapacity ( outputBuffer, HEADER_SIZE );

        outputBuffer.clear ();
        outputBuffer.putInt ( message.getCommandCode() );
        outputBuffer.putLong ( message.getTimestamp () );
        outputBuffer.putLong ( message.getSequence() );
        outputBuffer.putLong ( message.getReplySequence() );
        int sizePos = outputBuffer.position ();
        outputBuffer.putInt ( 0 ); // dummy body size

        int startPos = outputBuffer.position ();
        outputBuffer = codeValue ( outputBuffer, message.getValues () );
        int bodySize = outputBuffer.position () - startPos;
        outputBuffer.putInt ( sizePos, bodySize );
        
        outputBuffer.flip ();
        
        return outputBuffer;
    }

    private void logBuffer ( String where, ByteBuffer buffer )
    {
        if ( buffer != null )
            if ( _log.isDebugEnabled () )
                _log.debug ( where + " - Position: " + buffer.position () + " Cap: " + buffer.capacity () + " Limit: " + buffer.limit () + " Rem: " + buffer.remaining () );
    }
    
    private void logInputBuffer ( String where )
    {
        logBuffer ( where, _inputBuffer );
    }
    
    private void dumpInputBuffer ( )
    {
        String str = "";
        for ( int i = _inputBuffer.position () ; i < _inputBuffer.limit (); i++ )
        {
            str += String.format ( "%1$02X ", _inputBuffer.get ( i ) );
            if ( str.length () > 250 )
                break;
        }
        _log.debug ( "Input buffer: " + str );
    }
    
    /* (non-Javadoc)
     * @see org.openscada.net.codec.Protocol#decode(java.nio.ByteBuffer)
     */
    public void decode ( ByteBuffer buffer )
    {
        _log.debug ( "decode entry: " + buffer.remaining () );
        
        logInputBuffer ( "1" );
        
        if ( _inputBuffer != null )
        {
            _inputBuffer.position ( _inputBuffer.limit () );
            _inputBuffer.limit ( _inputBuffer.capacity () );
        }
        
        logInputBuffer ( "1.5" );
        
        _inputBuffer = ensureCapacity ( _inputBuffer, buffer.remaining () );
        
        logInputBuffer ( "2" );
        
        _inputBuffer.put ( buffer );
        
        logInputBuffer ( "3" );

        _inputBuffer.flip ();
        
        logInputBuffer ( "4" );
        
        dumpInputBuffer ();
        parse ();
        logInputBuffer ( "5" );
        
        _inputBuffer.compact ();
        _inputBuffer.flip ();

        logInputBuffer ( "6" );
    }

    private String decodeStringFromStream ( ByteBuffer buffer, int size )
    {
        byte [] data = new byte [ size ];
        buffer.get ( data );
        
        CharBuffer charBuffer;
        try
        {
            charBuffer = _charDecoder.decode ( ByteBuffer.wrap ( data ) );
            return charBuffer.toString ();
        }
        catch ( CharacterCodingException e )
        {
            return new String ( data );
        }
    }
    
    private String decodeStringFromStream ( ByteBuffer buffer )
    {
        return decodeStringFromStream ( buffer, buffer.getInt () );
    }
    
    private Value decodeValueFromStream ( ByteBuffer buffer )
    {
        int type = buffer.getInt ();
        int len = buffer.getInt ();

        if ( _log.isDebugEnabled () )
            _log.debug ( "Additional data: " + type + " len: " + len );

        switch ( type )
        {
        case VT_LONG:
            return new LongValue ( buffer.getLong() );
        case VT_INTEGER:
            return new IntegerValue ( buffer.getInt() );
        case VT_STRING:
            return new StringValue ( decodeStringFromStream ( buffer, len ) );
        case VT_DOUBLE:
            return new DoubleValue ( buffer.getDouble() );
        case VT_VOID:
            return new VoidValue ();
            // nothing to read
        case VT_LIST:
            return decodeListValueFromStream ( buffer );
        case VT_MAP:
            return decodeMapValueFromStream ( buffer );
        default:
            // unknown type: only consume data
            buffer.position ( buffer.position() + len );
            break;
        }
        return null;
    }
  
    private ListValue decodeListValueFromStream ( ByteBuffer buffer )
    {
        ListValue listValue = new ListValue ();
        
        int items = buffer.getInt ();
        for ( int i = 0; i < items; i++ )
        {
            listValue.getValues ().add ( decodeValueFromStream ( buffer ) );
        }
        
        return listValue;
    }
    
    private MapValue decodeMapValueFromStream ( ByteBuffer buffer )
    {
        MapValue mapValue = new MapValue ();
        
        int items = buffer.getInt ();
        for ( int i = 0; i < items; i++ )
        {
            Value value = decodeValueFromStream ( buffer );
            String key = decodeStringFromStream ( buffer );
            mapValue.getValues ().put ( key, value );
        }
        
        return mapValue;
    }

    private Message decodeMessageFromStream ( ByteBuffer buffer )
    {
        // read the packet
        Message message = new Message ();
        message.setCommandCode ( _inputBuffer.getInt () );
        message.setTimestamp ( _inputBuffer.getLong () );
        message.setSequence ( _inputBuffer.getLong () );
        message.setReplySequence ( _inputBuffer.getLong () );

        _inputBuffer.getInt (); // re-read to remove from buffer
        
        Value value = decodeValueFromStream ( _inputBuffer );
        if ( value instanceof MapValue )
            message.setValues ( (MapValue)value );
        
        return message;
    }
    
    private void parse ()
    {
        long ts = System.currentTimeMillis ();

        while  ( _inputBuffer.remaining() >= HEADER_SIZE )
        {
            // peek body size
            int bodySize = _inputBuffer.getInt ( _inputBuffer.position() + 4 + 8 + 8 + 8 );
            _log.debug ( "Body length: " + bodySize );

            if ( _inputBuffer.remaining() < HEADER_SIZE + bodySize )
            {
                _log.debug ( "Remaining: " + _inputBuffer.remaining () + " Header: " + HEADER_SIZE + " Body: " + bodySize );
                // message is not complete so skip for next try
                return;
            }

            Message message = null;
            try
            {
                message = decodeMessageFromStream ( _inputBuffer );
            }
            catch ( Exception e )
            {
                _log.warn ( "Decoding message from stream failed", e );
            }
            
            if ( message != null )
            {
                _log.debug ( "Message time diff: " + ( ts - message.getTimestamp () ) );
                _log.debug ( "Bytes remaining: " + _inputBuffer.remaining() );
                _listener.messageReceived ( _connection, message );
                _log.debug ( "Returned from processing message" );
            }

        }

    }
}
