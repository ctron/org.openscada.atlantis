package org.openscada.net.line;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;

import org.apache.log4j.Logger;
import org.openscada.net.io.ConnectionListener;
import org.openscada.net.io.SocketConnection;

public class LineBasedConnection implements ConnectionListener
{
    private static Logger _log = Logger.getLogger ( LineBasedConnection.class );
    
    private SocketConnection _socket = null;
    private LineHandler _handler = null;
    private StringBuilder _inputBuffer = new StringBuilder ();
    
    static private CharsetEncoder _encoder = Charset.forName ( "US-ASCII" ).newEncoder ();
    
    public LineBasedConnection ( SocketConnection socket, LineHandler handler )
    {
        _handler = handler;
        _socket = socket;
        
        _handler.setConnection ( this );
        
        _socket.setListener ( this );
        
        _socket.triggerRead ();
    }
    
    @Override
    protected void finalize () throws Throwable
    {
        close ();
        super.finalize ();
    }
    
    /**
     * Schedule close for connection
     * 
     * @note This does not close the connection immediately but schedules
     * the close for the time the outbound buffers are empty.
     *
     */
    public void close ()
    {
        synchronized ( this )
        {
            if ( _socket != null )
            {
                _socket.scheduleClose ();
            }
        }
    }
    
    /**
     * Clean up the connection from references it holds
     *
     */
    private void clean ()
    {
        close ();
        synchronized ( this )
        {
            if ( _socket != null )
            {
                _socket.setListener ( null );
                _socket = null;
            }
        }
        _handler = null;
    }

    public void closed ()
    {
        if ( _handler != null )
            _handler.closed ();
        
        clean ();
    }

    public void connected ()
    {
        if ( _handler != null )
            _handler.connected ();
    }

    public void connectionFailed ( IOException e )
    {
        if ( _handler != null )
            _handler.connectionFailed ( e );
        
        clean ();
    }

    public void read ( ByteBuffer buffer )
    {
        _log.debug ( String.format ( "Received %d bytes", buffer.remaining () ) );
        
        while ( buffer.remaining () > 0 )
        {
            byte b = (byte)(buffer.get () & 0x7F);
            switch ( b )
            {
            case '\n':
                if ( _inputBuffer.length () > 0 )
                    triggerLine ( _inputBuffer.toString () );
                _inputBuffer = new StringBuilder ();
                break;
            case '\r':
                break;
            default:
                _inputBuffer.append ( (char)b );
                break;
            }
        }
    }
    
    public void sendLine ( String line )
    {
        try
        {
            ByteBuffer buffer;
            synchronized ( _encoder )
            {
                buffer = _encoder.encode ( CharBuffer.wrap ( line ) );
            }
            synchronized ( this )
            {
                _socket.scheduleWrite ( buffer );
                buffer = ByteBuffer.allocate ( 1 );
                buffer.put ( (byte)'\n' );
                _socket.scheduleWrite ( buffer );
            }
        }
        catch ( CharacterCodingException e )
        {
            return;
        }
    }

    public void written ()
    {
    }
    
    private void triggerLine ( String line )
    {
        if ( _handler != null )
            _handler.handleLine ( line );
    }
    
    public SocketConnection getConnection ()
    {
        return _socket;
    }

    public void setTimeout ( int timeout )
    {
        synchronized ( this )
        {
            if ( _socket != null )
            {
                _socket.setTimeout ( timeout );
            }
        }
    }
}
