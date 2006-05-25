package org.openscada.net.codec.test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.openscada.net.codec.Protocol;

public class TestInputStream
{
    private List<TestBytePacket> _pseudoStream = new ArrayList<TestBytePacket> ();

    /**
     * 
     */
    public TestInputStream ()
    {
        super();
    }
    
    public TestInputStream ( Collection<TestBytePacket> packetList )
    {
        _pseudoStream = new ArrayList<TestBytePacket> ( packetList );
    }

    public void clear ()
    {
        _pseudoStream.clear ();
    }

    public void add ( TestBytePacket packet )
    {
        _pseudoStream.add ( packet );
    }

    public void run ( Protocol protocol )
    {
        TestBytePacket packet;
        Iterator<TestBytePacket> i = _pseudoStream.iterator ();
        while ( i.hasNext () )
        {
            packet = i.next ();
            packet.process ( protocol );
        }
    }
}
