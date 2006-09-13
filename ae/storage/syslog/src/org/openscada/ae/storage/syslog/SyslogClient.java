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
    
    private CharsetEncoder _encoder = Charset.forName ( "iso-8859-1" ).newEncoder ();
    
    public SyslogClient ( SocketAddress address ) throws SocketException
    {
        super ();
        _address = address;
        _socket = new DatagramSocket ();
    }
    
    protected void performMessage ( SyslogMessage message ) throws IOException
    {
        String msg = formatMessage ( message );
        _log.debug ( "Sending message: '" + msg + "'" );
        ByteBuffer buffer = _encoder.encode ( CharBuffer.wrap ( msg ) );
        
        DatagramPacket packet = new DatagramPacket ( buffer.array (), buffer.capacity (), _address );
        _socket.send ( packet );
    }

    public void sendMessage ( SyslogMessage message )
    {
        try
        {
            performMessage ( message );
        }
        catch ( IOException e )
        {
            _log.warn ( "Unable to send syslog message: ", e );
        }
    }
    
    private String formatMessage ( SyslogMessage message )
    {
        int messageCode = message.getPriority ().getCode () + ( message.getFacility ().getCode () << 3 );
        String pid = "";
        if ( message.getProcessId () != null )
        {
            pid = "[" + message.getProcessId () + "]";
        }
        return String.format ( Locale.US, "<%1$d>%2$tb %2$2te %2$TT %3$s %4$s%6$s: %5$s%n",
                messageCode,
                message.getTimestamp (),
                message.getHost (),
                message.getApplication (),
                message.getMessage (),
                pid
        );
    }
}
