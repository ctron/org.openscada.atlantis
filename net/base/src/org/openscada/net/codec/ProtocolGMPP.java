package org.openscada.net.codec;

import java.nio.ByteBuffer;
import java.util.Map;

import org.apache.log4j.Logger;
import org.openscada.net.base.MessageListener;
import org.openscada.net.base.data.DoubleValue;
import org.openscada.net.base.data.IntegerValue;
import org.openscada.net.base.data.ListValue;
import org.openscada.net.base.data.LongValue;
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

    private final static int HEADER_SIZE = 4 + 8 + 8 + 8 + 4 + 4; 
    
    private final static int BLOCK_SIZE =   4096;

    private static Logger _log = Logger.getLogger ( ProtocolGMPP.class );

    private Connection _connection = null;
    private MessageListener _listener = null;
    private ByteBuffer _inputBuffer = null;

    public ProtocolGMPP ( Connection connection, MessageListener listener )
    {
        _connection = connection;
        _listener = listener;	
    }
    
    private ByteBuffer encodeToStream ( ByteBuffer buffer, String data )
    {
        byte [] rawData = data.getBytes ();
        
        buffer = ensureCapacity ( buffer, 4 + rawData.length );
        buffer.putInt ( rawData.length );
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
        buffer = ensureCapacity ( buffer, 4 + 4 );
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

        outputBuffer.clear();
        outputBuffer.putInt ( message.getCommandCode() );
        outputBuffer.putLong ( message.getTimestamp () );
        outputBuffer.putLong ( message.getSequence() );
        outputBuffer.putLong ( message.getReplySequence() );

        outputBuffer = ensureCapacity ( outputBuffer, 4 + 4 );
        outputBuffer.putInt ( message.getValues().size () );
        
        int bodySizePosition = outputBuffer.position ();
        outputBuffer.putInt ( 0 ); // dummy body size
        
        int bodyStartPosition = outputBuffer.position ();
        for ( Map.Entry<String, Value> entry : message.getValues ().entrySet () )
        {
            outputBuffer = codeValue ( outputBuffer, entry.getKey (), entry.getValue () );
        }
        int bodyEndPosition = outputBuffer.position ();
        
        int bodySize = bodyEndPosition - bodyStartPosition;
        outputBuffer.putInt ( bodySizePosition, bodySize );
        
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
            byte [] data = new byte [ len ];
            buffer.get ( data );
            return new StringValue ( new String ( data ) );
        case VT_DOUBLE:
            return new DoubleValue ( buffer.getDouble() );
        case VT_VOID:
            return new VoidValue ();
            // nothing to read
        case VT_LIST:
            return decodeListValueFromStream ( buffer );
        default:
            // unknown type: only consume data
            buffer.position ( buffer.position() + len );
            break;
        }
        return null;
    }
    
    private void parseItem ( ByteBuffer buffer, Message message )
    {
        Value value = decodeValueFromStream ( buffer );

        // now read the item name
        int nameLen = buffer.getInt ();
        _log.debug ( "Reading " + nameLen + " name bytes" );

        byte[] data = new byte [ nameLen ];
        buffer.get ( data );
        String name = new String ( data );
        if ( value != null )
        {
            message.getValues ().put ( name, value );
        }
    }

    private Value decodeListValueFromStream ( ByteBuffer buffer )
    {
        ListValue listValue = new ListValue ();
        
        int items = buffer.getInt ();
        for ( int i = 0; i < items; i++ )
        {
            listValue.getValues ().add ( decodeValueFromStream ( buffer ) );
        }
        
        return listValue;
    }

    private void parse ()
    {
        long ts = System.currentTimeMillis ();

        while  ( _inputBuffer.remaining() >= HEADER_SIZE )
        {
            // peek body size
            int bodySize = _inputBuffer.getInt( _inputBuffer.position() + 4 + 8 + 8 + 8 + 4 );
            _log.debug("Body length: " + bodySize );

            if ( _inputBuffer.remaining() < HEADER_SIZE + bodySize )
            {
                _log.debug("Remaining: " + _inputBuffer.remaining() + " Header: " + HEADER_SIZE + " Body: " + bodySize );
                // message is not complete so skip for next try
                return;
            }

            // read the packet
            Message message = new Message();
            message.setCommandCode(_inputBuffer.getInt());
            long st;
            message.setTimestamp ( st = _inputBuffer.getLong() );
            message.setSequence(_inputBuffer.getLong());
            message.setReplySequence(_inputBuffer.getLong());

            // number of items to follow
            int numItems = _inputBuffer.getInt();
            if ( numItems < 0 )
                numItems = 0; // in case of a negativ number use zero instead

            // re-read body size to consume buffer
            bodySize = _inputBuffer.getInt();

            for ( int i = 0; i<numItems; i++ )
            {
                parseItem ( _inputBuffer, message );
            }

            _log.debug ( "Message time diff: " + (ts - st) );
            _log.debug ( "Bytes remaining: " + _inputBuffer.remaining() );
            _listener.messageReceived ( _connection, message );
            _log.debug ( "Returned from processing message" );

        }

    }
}
