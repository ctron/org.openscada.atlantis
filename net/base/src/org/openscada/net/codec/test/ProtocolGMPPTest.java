package org.openscada.net.codec.test;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.openscada.net.base.data.DoubleValue;
import org.openscada.net.base.data.ListValue;
import org.openscada.net.base.data.LongValue;
import org.openscada.net.base.data.Message;
import org.openscada.net.base.data.StringValue;
import org.openscada.net.codec.InvalidValueTypeException;
import org.openscada.net.codec.Protocol;
import org.openscada.net.codec.ProtocolGMPP;


public class ProtocolGMPPTest
{
    private static Logger _log = Logger.getLogger ( ProtocolGMPPTest.class );
    
    private void performTest ( Collection<TestBytePacket> packets, Collection<Message> messages )
    {
        TestInputStream stream = new TestInputStream ( packets );
        TestMessageListener listener = new TestMessageListener ();
        
        Protocol protocol = new ProtocolGMPP ( null, listener );
       
        stream.run ( protocol );
        
        listener.assertMessages ( messages );
    }
    
    private void performScatterTest ( Collection<TestBytePacket> packets, Collection<Message> messages, int scatterSize )
    {
        _log.info ( "Running scatter test with: " + scatterSize );
        
        List<TestBytePacket> scatteredPackets = new ArrayList<TestBytePacket> ();
        
        byte [] data = new byte[scatterSize];
        int pos = 0;
        
        for ( TestBytePacket packet : packets )
        {
            for ( byte b : packet.getBytes () )
            {
                data[pos] = b;
                pos++;
                if ( data.length == pos )
                {
                    scatteredPackets.add ( new TestBytePacket ( data ) );
                    pos = 0;
                }
            }
        }
        
        if ( pos > 0 )
        {
            byte [] addData = new byte[pos];
            for ( int i = 0 ; i < pos ; i++ )
                addData[i] = data[i];
            
            scatteredPackets.add ( new TestBytePacket ( addData ) );
        }
        
        performTest ( scatteredPackets, messages );
    }
    
    private void performScatterTest ( Collection<TestBytePacket> packets, Collection<Message> messages, int... scatterSize )
    {
        for ( int i : scatterSize )
        {
            performScatterTest ( packets, messages, i );
        }
    }
    
    private void performAllTests ( Collection<TestBytePacket> packets, Collection<Message> messages )
    {
        performTest ( packets, messages );

        performScatterTest ( packets, messages, 2, 3, 5, 10 );
    }
    
    @Test
    public void testPacket1 ()
    {
        _log.info ( "Running test1" );
        
        List<TestBytePacket> packets = new ArrayList<TestBytePacket> ();
        packets.add ( new TestBytePacket ( 
                " 00 00 00 01" +     // command code
                " 00 00 00 00 00 00 00 00" + // timestamp
                " 00 00 00 00 00 00 00 02" + // sequence
                " 00 00 00 00 00 00 00 03" + // reply sequence
                " 00 00 00 20" + // body size
                " 00 00 00 07" + // VT_MAP
                " 00 00 00 18" + // VT_MAP - size
                " 00 00 00 01" + // VT_MAP - item count
                " 00 00 00 01" + // VT_STRING
                " 00 00 00 04" + // string length
                " 74 65 73 74" + // empty string
                " 00 00 00 04" + // name length
                " 74 65 73 74" + // empty name
                ""
                ) );
        
        
        Message message;
        
        List<Message> messages = new ArrayList<Message> ();
        
        message = new Message ();
        message.setCommandCode ( 1 );
        message.setSequence ( 2 );
        message.setReplySequence ( 3 );
        message.getValues ().put ( "test", new StringValue ("test") );
        
        messages.add ( message );
        
        performAllTests ( packets, messages );
    }
    
    public void testPacket2 ()
    {
        _log.info ( "Running test2" );
        List<TestBytePacket> packets = new ArrayList<TestBytePacket> ();
        Message message;
        List<Message> messages = new ArrayList<Message> ();
        
        for ( int i = 0 ; i < 1000; i++ )
        {
            int commandCode = i % 255;
            String strCommandCode = Integer.toHexString ( commandCode );
            packets.add ( new TestBytePacket ( 
                    " 00 00 00 " + strCommandCode +     // command code
                    " 00 00 00 00 00 00 00 00" + // timestamp
                    " 00 00 00 00 00 00 00 02" + // sequence
                    " 00 00 00 00 00 00 00 03" + // reply sequence
                    " 00 00 00 00" + // number of values
                    " 00 00 00 00" + // body size
                    "" 
            ) );
            
            message = new Message ();
            message.setCommandCode ( commandCode );
            message.setSequence ( 2 );
            message.setReplySequence ( 3 );
            messages.add ( message );

        }
        
        performAllTests ( packets, messages );
    }
    
    private void performCode ( Message message, TestBytePacket expectedBuffer ) throws InvalidValueTypeException
    {
        Protocol protocol = new ProtocolGMPP ( null, null );
        ByteBuffer byteBuffer = protocol.code ( message );
        
        int max = Math.max ( byteBuffer.remaining (), expectedBuffer.getBytes ().length );
        System.out.println ( "Buffer sizes: " + byteBuffer.remaining () + "/" + expectedBuffer.getBytes ().length );
        for ( int i = 0; i < max; i ++ )
        {
            String line = "";
            
            Byte b1 = null;
            Byte b2 = null;
            
            if ( i < byteBuffer.remaining () )
            {
                b1 = byteBuffer.get ( i );
                line += String.format ( "%1$02X - ", b1 );
            }
            else
                line += "   - ";
            if ( i < expectedBuffer.getBytes ().length )
            {
                b2 = expectedBuffer.getBytes ()[i];
                line += String.format ( "%1$02X", b2 );
            }
            else
                line += "  ";
            
            if ( b1 == null && b2 == null )
                ;
            else
                if ( !b1.equals ( b2 ) )
                    line += " X";
            
            System.out.println ( line );
        }
        
        Assert.assertTrue ( "Compare buffer", expectedBuffer.equalToBuffer ( byteBuffer ) );
    }
    
    @Test
    public void testEncode1 () throws InvalidValueTypeException
    {
        _log.info ( "Running encode1" );
        
        Message message = new Message ();
        message.setTimestamp ( 0 );
        message.setCommandCode ( 1 );
        message.setSequence ( 2 );
        message.setReplySequence ( 3 );
        message.getValues ().put ( "test", new StringValue ("test") );
        
        TestBytePacket packet = new TestBytePacket ( 
                " 00 00 00 01" +     // command code
                " 00 00 00 00 00 00 00 00" + // timestamp
                " 00 00 00 00 00 00 00 02" + // sequence
                " 00 00 00 00 00 00 00 03" + // reply sequence
                " 00 00 00 20" + // body size
                " 00 00 00 07" + // VT_MAP
                " 00 00 00 18" + // VT_MAP - size
                " 00 00 00 01" + // VT_MAP - item count
                " 00 00 00 01" + // VT_STRING
                " 00 00 00 04" + // string length
                " 74 65 73 74" + // test string
                " 00 00 00 04" + // name length
                " 74 65 73 74" + // test name
                ""
                );
        
        performCode ( message, packet );
    }
    
    @Test
    public void testEncode2 () throws InvalidValueTypeException
    {
        _log.info ( "Running encode2" );
        
        Message message = new Message ();
        message.setTimestamp ( 0 );
        message.setCommandCode ( 1 + 0xFF );
        message.setSequence ( 2 );
        message.setReplySequence ( 1 + 0xFF );
        message.getValues ().put ( "test", new StringValue ( "test" ) );
        message.getValues ().put ( "tett", new StringValue ( "tett" ) );
        
        TestBytePacket packet = new TestBytePacket ( 
                " 00 00 01 00" +     // command code
                " 00 00 00 00 00 00 00 00" + // timestamp
                " 00 00 00 00 00 00 00 02" + // sequence
                " 00 00 00 00 00 00 01 00" + // reply sequence
                " 00 00 00 34" + // body size
                " 00 00 00 07" + // VT_MAP
                " 00 00 00 2C" + // VT_MAP - size
                " 00 00 00 02" + // VT_MAP - item count
                " 00 00 00 01" + // VT_STRING
                " 00 00 00 04" + // string length
                " 74 65 74 74" + // empty string
                " 00 00 00 04" + // name length
                " 74 65 74 74" + // test name
                " 00 00 00 01" + // VT_STRING
                " 00 00 00 04" + // string length
                " 74 65 73 74" + // test string
                " 00 00 00 04" + // name length
                " 74 65 73 74" + // test name
                ""
                );
        
        performCode ( message, packet );
    }
    
    @Test
    public void testEncode3 () throws InvalidValueTypeException
    {
        _log.info ( "Running encode3" );
        
        Message message = new Message ();
        message.setTimestamp ( 0 );
        message.setCommandCode ( 1 + 0xFF );
        message.setSequence ( 2 );
        message.setReplySequence ( 1 + 0xFF );
        ListValue list = new ListValue ();
        list.getValues ().add ( new StringValue ( "tett") );
        list.getValues ().add ( new StringValue ( "teut") );
        message.getValues ().put ( "test", list );
        
        TestBytePacket packet = new TestBytePacket ( 
                " 00 00 01 00" +     // command code
                " 00 00 00 00 00 00 00 00" + // timestamp
                " 00 00 00 00 00 00 00 02" + // sequence
                " 00 00 00 00 00 00 01 00" + // reply sequence
                " 00 00 00 38" + // body size
                " 00 00 00 07" + // VT_MAP
                " 00 00 00 30" + // VT_MAP - size
                " 00 00 00 01" + // VT_MAP - item count
                " 00 00 00 06" + // VT_LIST
                " 00 00 00 1C" + // data length
                " 00 00 00 02" + // number of list items
                " 00 00 00 01" + // VT_STRING
                " 00 00 00 04" + // string length
                " 74 65 74 74" + // test string
                " 00 00 00 01" + // VT_STRING
                " 00 00 00 04" + // string length
                " 74 65 75 74" + // test string
                " 00 00 00 04" + // name length - of VT_LIST
                " 74 65 73 74" + // test name
                ""
                );
        
        performCode ( message, packet );
    }
    
    
    public void testPerformance () throws InvalidValueTypeException
    {
        Message message = new Message ();
        
        message.getValues ().put ( "string-0", new StringValue ( "test" ) );
        message.getValues ().put ( "string-1", new StringValue ( "test" ) );
        message.getValues ().put ( "int-0", new LongValue ( 0 ) );
        message.getValues ().put ( "int-1", new LongValue ( 0 ) );
        message.getValues ().put ( "double-0", new DoubleValue ( 1.234 ) );
        message.getValues ().put ( "double-1", new DoubleValue ( 1.234 ) );
        
        Protocol protocol = new ProtocolGMPP ( null, null );
        
        _log.info ( "Start performance run" );
        
        long start = System.currentTimeMillis ();
        for ( int i = 0; i < 10 * 1000; i++ )
        {
            protocol.code ( message );
        }
        long end = System.currentTimeMillis ();
        
        _log.info ( "Processing took: " + (end-start) + " ms" );
        
    }
}
