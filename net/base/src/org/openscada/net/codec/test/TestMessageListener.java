package org.openscada.net.codec.test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.openscada.net.base.MessageListener;
import org.openscada.net.base.data.Message;
import org.openscada.net.base.data.Value;
import org.openscada.net.io.Connection;

public class TestMessageListener implements MessageListener
{
    private List<Message> _messages = new ArrayList<Message>();
    
    public void messageReceived ( Connection connection, Message message )
    {
        _messages.add ( message );  
    }

    public void assertMessages ( Collection<Message> assertMessages )
    {
        Assert.assertEquals ( "Number of messages", assertMessages.size(), _messages.size () );
        
        Iterator<Message> i1 = assertMessages.iterator ();
        Iterator<Message> i2 = _messages.iterator ();
        
        while ( i1.hasNext () )
        {
            Message m1 = i1.next ();
            Message m2 = i2.next ();
            
            Assert.assertEquals ( "Command code", m1.getCommandCode (), m2.getCommandCode () );
            Assert.assertEquals ( "Sequence", m1.getSequence (), m2.getSequence () );
            Assert.assertEquals ( "Reply sequence", m1.getReplySequence (), m2.getReplySequence () );
            Assert.assertEquals ( "Number of values", m1.getValues ().size (), m2.getValues ().size () );
            
            Iterator<Map.Entry<String, Value>> vi1 = m1.getValues ().entrySet ().iterator ();
            Iterator<Map.Entry<String, Value>> vi2 = m2.getValues ().entrySet ().iterator ();
            
            while ( vi1.hasNext () )
            {
                Map.Entry<String, Value> e1 = vi1.next ();
                Map.Entry<String, Value> e2 = vi2.next ();
                
                Assert.assertEquals ( "Entry name", e1.getKey(), e2.getKey() );
                Assert.assertEquals ( "Equal Type", e1.getValue ().getClass (), e2.getValue ().getClass () );
                Assert.assertEquals ( "Equal Value", e1.getValue (), e2.getValue () );
            }
        }
    }
}
