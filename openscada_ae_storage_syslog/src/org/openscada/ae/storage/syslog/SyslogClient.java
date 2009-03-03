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

package org.openscada.ae.storage.syslog;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.util.Locale;

import org.apache.log4j.Logger;

public class SyslogClient
{
    private static Logger _log = Logger.getLogger ( SyslogClient.class );

    private DatagramSocket _socket = null;

    private SocketAddress _address = null;

    private final CharsetEncoder _encoder = Charset.forName ( "iso-8859-1" ).newEncoder ();

    public SyslogClient ( final SocketAddress address ) throws SocketException
    {
        super ();
        this._address = address;
        this._socket = new DatagramSocket ();
    }

    protected void performMessage ( final SyslogMessage message ) throws IOException
    {
        final String msg = formatMessage ( message );
        _log.debug ( "Sending message: '" + msg + "'" );
        final ByteBuffer buffer = this._encoder.encode ( CharBuffer.wrap ( msg ) );

        final DatagramPacket packet = new DatagramPacket ( buffer.array (), buffer.capacity (), this._address );
        this._socket.send ( packet );
    }

    public void sendMessage ( final SyslogMessage message )
    {
        try
        {
            performMessage ( message );
        }
        catch ( final IOException e )
        {
            _log.warn ( "Unable to send syslog message: ", e );
        }
    }

    private String formatMessage ( final SyslogMessage message )
    {
        final int messageCode = message.getPriority ().getCode () + ( message.getFacility ().getCode () << 3 );
        String pid = "";
        if ( message.getProcessId () != null )
        {
            pid = "[" + message.getProcessId () + "]";
        }
        return String.format ( Locale.US, "<%1$d>%2$tb %2$2te %2$TT %3$s %4$s%6$s: %5$s%n", messageCode, message.getTimestamp (), message.getHost (), message.getApplication (), message.getMessage (), pid );
    }
}
