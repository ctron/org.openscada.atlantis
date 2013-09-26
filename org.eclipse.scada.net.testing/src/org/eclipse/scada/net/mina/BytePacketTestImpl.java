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

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.eclipse.scada.net.mina.GMPPProtocolDecoder;
import org.eclipse.scada.net.mina.GMPPProtocolEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BytePacketTestImpl
{

    private final static Logger logger = LoggerFactory.getLogger ( BytePacketTestImpl.class );

    private int preDelay = 0;

    private final byte[] bytes;

    private final int postDelay = 0;

    static byte[] fromString ( final String str )
    {
        final String dataStr = str.trim ();
        final String[] toks = dataStr.split ( "\\s" );

        final byte[] binary = new byte[toks.length];

        for ( int i = 0; i < toks.length; i++ )
        {
            if ( toks[i].length () > 0 )
            {
                binary[i] = (byte)Integer.parseInt ( toks[i], 16 );
            }
        }
        return binary;
    }

    /**
     * 
     */
    public BytePacketTestImpl ( final byte[] bytes, final int preDelay, final int postDelay )
    {
        super ();
        this.preDelay = preDelay;
        this.bytes = bytes.clone ();
        this.preDelay = postDelay;
    }

    public BytePacketTestImpl ( final byte[] bytes, final int preDelay )
    {
        this ( bytes, preDelay, 0 );
    }

    public BytePacketTestImpl ( final String bytes )
    {
        this ( fromString ( bytes ) );
    }

    public BytePacketTestImpl ( final byte[] bytes )
    {
        this ( bytes, 0, 0 );
    }

    public void process ( final GMPPProtocolDecoder decoder, final GMPPProtocolEncoder encoder, final ProtocolDecoderOutput in ) throws Exception
    {
        final IoBuffer buffer = IoBuffer.allocate ( this.bytes.length );
        buffer.put ( this.bytes );
        buffer.flip ();

        try
        {
            Thread.sleep ( this.preDelay );
            decoder.decode ( null, buffer, in );
            Thread.sleep ( this.postDelay );
        }
        catch ( final InterruptedException e )
        {
            e.printStackTrace ();
        }
    }

    public byte[] getBytes ()
    {
        return this.bytes.clone ();
    }

    public boolean equalToBuffer ( final IoBuffer buffer )
    {
        if ( this.bytes.length != buffer.remaining () )
        {
            System.err.println ( String.format ( "Length mismatch: (e: %s, a: %s)", this.bytes.length, buffer.remaining () ) );
            return false;
        }

        for ( int i = 0; i < this.bytes.length; i++ )
        {
            logger.info ( "Expected/Current: " + this.bytes[i] + "/" + buffer.get ( i ) );
            if ( this.bytes[i] != buffer.get ( i ) )
            {
                return false;
            }
        }
        return true;
    }

}
