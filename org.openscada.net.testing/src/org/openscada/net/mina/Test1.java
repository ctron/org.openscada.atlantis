package org.openscada.net.mina;

import org.junit.Test;
import org.openscada.net.base.data.IntegerValue;
import org.openscada.net.base.data.ListValue;
import org.openscada.net.base.data.MapValue;
import org.openscada.net.base.data.Message;
import org.openscada.net.base.data.Value;
import org.openscada.net.codec.InvalidValueTypeException;

public class Test1
{

    public static void main ( final String[] args ) throws InvalidValueTypeException
    {
        new Test1 ().test1 ();
    }

    @Test
    public void test1 () throws InvalidValueTypeException
    {
        final GMPPProtocolEncoder encoder = new GMPPProtocolEncoder ();

        final Message message = createMessage ( 150 );

        encode ( encoder, message );
    }

    private void encode ( final GMPPProtocolEncoder encoder, final Message message ) throws InvalidValueTypeException
    {
        encoder.code ( message );
    }

    private Message createMessage ( final int count )
    {
        final Message message = new Message ( 0 );

        message.getValues ().put ( "values", createValue ( count ) );

        return message;
    }

    private Value createValue ( final int count )
    {
        final MapValue value = new MapValue ();

        final ListValue list = new ListValue ();
        for ( int i = 0; i < count; i++ )
        {
            final MapValue v = new MapValue ();
            for ( int j = 0; j < count; j++ )
            {
                v.put ( String.format ( "test%d", j ), createScalarValue ( j * i ) );
            }
            list.add ( v );
        }

        value.put ( "list", list );
        return value;
    }

    public Value createScalarValue ( final int index )
    {
        return new IntegerValue ( index );
    }
}
