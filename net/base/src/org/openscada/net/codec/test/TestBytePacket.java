package org.openscada.net.codec.test;

import java.nio.ByteBuffer;

import org.apache.log4j.Logger;
import org.openscada.net.codec.Protocol;

public class TestBytePacket
{
    private static Logger _log = Logger.getLogger ( TestBytePacket.class );
    
    private int _preDelay = 0;
    private byte [] _bytes;
    private int _postDelay = 0;

    static byte [] fromString ( String str )
    {
        String dataStr = str.trim ();
        String [] toks = dataStr.split ( "\\s" );

        byte [] binary = new byte[toks.length];

        for ( int i = 0; i<toks.length; i++ )
        {
            if ( toks[i].length() > 0 )
                binary[i] = (byte)Integer.parseInt(toks[i],16);
        }
        return binary;
    }

    /**
     * 
     */
    public TestBytePacket( byte [] bytes, int preDelay, int postDelay)
    {
        super();
        _preDelay = preDelay;
        _bytes = bytes.clone ();
        _preDelay = postDelay;
    }

    public TestBytePacket ( byte[] bytes, int preDelay )
    {
        this ( bytes, preDelay, 0 );
    }

    public TestBytePacket ( String bytes )
    {       
        this ( fromString ( bytes ) );
    }

    public TestBytePacket ( byte[] bytes )
    {
        this ( bytes, 0, 0 );
    }

    public void process ( Protocol decoder ) 
    {
        ByteBuffer buffer = ByteBuffer.allocate(_bytes.length);
        buffer.put(_bytes);
        buffer.flip ();

        try
        {    
            Thread.sleep ( _preDelay );
            decoder.decode ( buffer );     
            Thread.sleep ( _postDelay );
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }

    public byte[] getBytes ()
    {
        return _bytes;
    }
    
    public boolean equalToBuffer ( ByteBuffer buffer )
    {
        if ( _bytes.length != buffer.remaining () )
            return false;
        
        for ( int i = 0; i < _bytes.length ; i++ )
        {
            _log.info ( "Expected/Current: " + _bytes[i] + "/" + buffer.get(i) );
            if ( _bytes[i] != buffer.get(i) )
                return false;
        }
        return true;
    }

}
