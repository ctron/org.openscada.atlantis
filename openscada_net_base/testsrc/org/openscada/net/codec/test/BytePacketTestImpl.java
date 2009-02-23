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

package org.openscada.net.codec.test;

import java.nio.ByteBuffer;

import org.apache.log4j.Logger;
import org.openscada.net.codec.Protocol;

public class BytePacketTestImpl
{
    private static Logger _log = Logger.getLogger ( BytePacketTestImpl.class );

    private int _preDelay = 0;

    private final byte[] _bytes;

    private final int _postDelay = 0;

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
        this._preDelay = preDelay;
        this._bytes = bytes.clone ();
        this._preDelay = postDelay;
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

    public void process ( final Protocol decoder )
    {
        final ByteBuffer buffer = ByteBuffer.allocate ( this._bytes.length );
        buffer.put ( this._bytes );
        buffer.flip ();

        try
        {
            Thread.sleep ( this._preDelay );
            decoder.decode ( buffer );
            Thread.sleep ( this._postDelay );
        }
        catch ( final InterruptedException e )
        {
            e.printStackTrace ();
        }
    }

    public byte[] getBytes ()
    {
        return this._bytes;
    }

    public boolean equalToBuffer ( final ByteBuffer buffer )
    {
        if ( this._bytes.length != buffer.remaining () )
        {
            return false;
        }

        for ( int i = 0; i < this._bytes.length; i++ )
        {
            _log.info ( "Expected/Current: " + this._bytes[i] + "/" + buffer.get ( i ) );
            if ( this._bytes[i] != buffer.get ( i ) )
            {
                return false;
            }
        }
        return true;
    }

}
