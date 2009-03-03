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

package org.openscada.ae.storage.syslog.provider;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

import org.apache.log4j.Logger;
import org.openscada.ae.storage.syslog.DataStore;

public class SyslogDaemonProvider implements Runnable
{
    private static Logger _log = Logger.getLogger ( SyslogDaemonProvider.class );

    private static final int MAX_BUFFER = 16 * 1024;

    private SyslogParser _parser = null;

    private DatagramSocket _socket = null;

    private final Thread _thread = new Thread ( this );

    private final CharsetDecoder _decoder = Charset.forName ( "iso-8859-1" ).newDecoder ();

    public SyslogDaemonProvider ( final DataStore store, final int port ) throws SocketException
    {
        super ();
        this._parser = new SyslogParser ( store, "syslog.net", "INFO" );

        this._socket = new DatagramSocket ( port );

        this._thread.setDaemon ( true );
        this._thread.start ();
    }

    public void run ()
    {
        _log.debug ( "Reader running..." );

        while ( true )
        {
            final byte[] buffer = new byte[MAX_BUFFER];
            final DatagramPacket packet = new DatagramPacket ( buffer, buffer.length );
            try
            {
                this._socket.receive ( packet );
                handlePacket ( packet );
            }
            catch ( final Exception e )
            {
                _log.error ( "Failed to receive", e );
            }
        }
    }

    private void handlePacket ( final DatagramPacket packet ) throws CharacterCodingException
    {
        final String message = this._decoder.decode ( ByteBuffer.wrap ( packet.getData () ) ).toString ();
        this._parser.handleLine ( message.trim () );
    }

}
