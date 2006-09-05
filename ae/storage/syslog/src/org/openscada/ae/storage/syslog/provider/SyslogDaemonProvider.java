package org.openscada.ae.storage.syslog.provider;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
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
    private Thread _thread = new Thread ( this );

    private CharsetDecoder _decoder = Charset.forName ( "iso-8859-1" ).newDecoder ();
    
    public SyslogDaemonProvider ( DataStore store, int port ) throws SocketException
    {
        super ();
        _parser = new SyslogParser ( store, "syslog.net", "INFO" );
        
        _socket = new DatagramSocket ( port );

        _thread.setDaemon ( true );
        _thread.start ();
    }

    public void run ()
    {
        _log.debug ( "Reader running..." );
        
        while ( true )
        {
            byte [] buffer = new byte [ MAX_BUFFER ];
            DatagramPacket packet = new DatagramPacket ( buffer, buffer.length );
            try
            {
                _socket.receive ( packet );
                handlePacket ( packet );
            }
            catch ( Exception e )
            {
                _log.error ( "Failed to receive", e );
            }
        }
    }

    private void handlePacket ( DatagramPacket packet ) throws CharacterCodingException
    {
        String message = _decoder.decode ( ByteBuffer.wrap ( packet.getData () ) ).toString ();
        _parser.handleLine ( message.trim () );
    }
    
  

}
