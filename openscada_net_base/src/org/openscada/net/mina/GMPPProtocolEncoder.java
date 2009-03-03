package org.openscada.net.mina;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
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

    private final CharsetEncoder charEncoder = Charset.forName ( "utf-8" ).newEncoder ();

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

    private IoBuffer encodeToStream ( final IoBuffer buffer, final String data )
    {
        ByteBuffer rawData;
        synchronized ( this.charEncoder )
        {
            try
            {
                rawData = this.charEncoder.encode ( CharBuffer.wrap ( data ) );
            }
            catch ( final CharacterCodingException e )
            {
                rawData = ByteBuffer.wrap ( data.getBytes () );
            }
        }

        buffer.expand ( 4 + rawData.remaining () );
        buffer.putInt ( rawData.remaining () );
        buffer.put ( rawData );

        return buffer;
    }

    private IoBuffer encodeToStream ( final IoBuffer buffer, final IntegerValue value )
    {
        buffer.expand ( 4 + 4 + 4 );
        buffer.putInt ( VT_INTEGER );
        buffer.putInt ( 4 );
        buffer.putInt ( value.getValue () );

        return buffer;
    }

    private IoBuffer encodeToStream ( final IoBuffer buffer, final LongValue value )
    {
        buffer.expand ( 4 + 4 + 8 );
        buffer.putInt ( VT_LONG );
        buffer.putInt ( 8 );
        buffer.putLong ( value.getValue () );

        return buffer;
    }

    private IoBuffer encodeToStream ( final IoBuffer buffer, final DoubleValue value )
    {
        buffer.expand ( 4 + 4 + 8 );
        buffer.putInt ( VT_DOUBLE );
        buffer.putInt ( 8 );
        buffer.putLong ( Double.doubleToRawLongBits ( value.getValue () ) );
        return buffer;
    }

    private IoBuffer encodeToStream ( final IoBuffer buffer, final VoidValue value )
    {
        buffer.expand ( 4 + 4 );
        buffer.putInt ( VT_VOID );
        buffer.putInt ( 0 );

        return buffer;
    }

    private IoBuffer encodeToStream ( final IoBuffer buffer, final BooleanValue value )
    {
        buffer.expand ( 4 + 4 + 1 );
        buffer.putInt ( VT_BOOLEAN );
        buffer.putInt ( 1 );
        buffer.put ( value.getValue () ? (byte)0xFF : (byte)0x00 );

        return buffer;
    }

    private IoBuffer encodeToStream ( IoBuffer buffer, final StringValue value )
    {
        buffer.expand ( 4 );
        buffer.putInt ( VT_STRING );
        buffer = encodeToStream ( buffer, value.getValue () );
        return buffer;
    }

    private IoBuffer encodeToStream ( IoBuffer buffer, final ListValue value ) throws InvalidValueTypeException
    {
        buffer.expand ( 4 + 4 + 4 );
        buffer.putInt ( VT_LIST );
        final int position = buffer.position (); // remember position
        buffer.putInt ( 0 ); // dummy size length

        final int startPos = buffer.position ();
        buffer.putInt ( value.getValues ().size () );

        for ( final Value valueEntry : value.getValues () )
        {
            buffer = codeValue ( buffer, valueEntry );
        }
        final int size = buffer.position () - startPos;

        buffer.putInt ( position, size ); // set value size
        return buffer;
    }

    private IoBuffer encodeToStream ( IoBuffer buffer, final MapValue value ) throws InvalidValueTypeException
    {
        buffer.expand ( 4 + 4 + 4 );
        buffer.putInt ( VT_MAP );
        final int position = buffer.position (); // remember position
        buffer.putInt ( 0 ); // dummy size length

        final int startPos = buffer.position ();
        buffer.putInt ( value.getValues ().size () );

        for ( final Map.Entry<String, Value> valueEntry : value.getValues ().entrySet () )
        {
            buffer = codeValue ( buffer, valueEntry.getKey (), valueEntry.getValue () );
        }
        final int size = buffer.position () - startPos;

        buffer.putInt ( position, size ); // set value size
        return buffer;
    }

    private IoBuffer codeValue ( IoBuffer buffer, final Value value ) throws InvalidValueTypeException
    {
        if ( value instanceof StringValue )
        {
            buffer = encodeToStream ( buffer, (StringValue)value );
        }
        else if ( value instanceof BooleanValue )
        {
            buffer = encodeToStream ( buffer, (BooleanValue)value );
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
        {
            throw new InvalidValueTypeException ();
        }
        return buffer;
    }

    private IoBuffer codeValue ( IoBuffer buffer, final String name, final Value value ) throws InvalidValueTypeException
    {
        buffer = codeValue ( buffer, value );
        buffer = encodeToStream ( buffer, name );

        return buffer;
    }

    protected IoBuffer code ( final Message message ) throws InvalidValueTypeException
    {
        IoBuffer outputBuffer = IoBuffer.allocate ( HEADER_SIZE );

        outputBuffer.clear ();
        outputBuffer.putInt ( message.getCommandCode () );
        outputBuffer.putLong ( message.getTimestamp () );
        outputBuffer.putLong ( message.getSequence () );
        outputBuffer.putLong ( message.getReplySequence () );
        final int sizePos = outputBuffer.position ();
        outputBuffer.putInt ( 0 ); // dummy body size

        final int startPos = outputBuffer.position ();
        outputBuffer = codeValue ( outputBuffer, message.getValues () );
        final int bodySize = outputBuffer.position () - startPos;
        outputBuffer.putInt ( sizePos, bodySize );

        outputBuffer.flip ();

        return outputBuffer;
    }
}
