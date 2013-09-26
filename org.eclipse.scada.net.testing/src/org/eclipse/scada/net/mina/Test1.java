/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2010 TH4 SYSTEMS GmbH (http://th4-systems.com)
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

package org.eclipse.scada.net.mina;

import org.eclipse.scada.net.base.data.IntegerValue;
import org.eclipse.scada.net.base.data.ListValue;
import org.eclipse.scada.net.base.data.MapValue;
import org.eclipse.scada.net.base.data.Message;
import org.eclipse.scada.net.base.data.Value;
import org.eclipse.scada.net.codec.InvalidValueTypeException;
import org.eclipse.scada.net.mina.GMPPProtocolEncoder;
import org.junit.Test;

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

        final Message message = createMessage ( 250 );

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
        return IntegerValue.valueOf ( index );
    }
}
