/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006 TH4 SYSTEMS GmbH (http://th4-systems.com)
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

package org.eclipse.scada.net.codec.test;

import org.apache.log4j.Logger;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.eclipse.scada.net.mina.GMPPProtocolDecoder;
import org.eclipse.scada.net.mina.GMPPProtocolEncoder;

public class BytePacketTestImpl
{
    private static Logger logger = Logger.getLogger ( BytePacketTestImpl.class );

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
